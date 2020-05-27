package database;

import java.io.*;
import java.sql.*;
import java.util.Properties;

/**
 * This class handles the database connection via a JDBC driver
 */
public class DBConnection {
    private static DBConnection instance;
    private Connection connection;
    private String jdbcHostname;
    private String jdbcDatabase;
    private String jdbcUsername;
    private String jdbcPassword;

    /**
     * Private constructor which checks if the 'config.properties' file exists and if it contains valid credentials
     * If it doesn't exist, it falls back to the environmental variables (which are then written to a file if they are set)
     * If neither of these options work, it creates a template 'config.properties' file and then terminates the JVM with the System.exit() method.
     *
     * @throws DataAccessException when SQLException inside the method
     */
    private DBConnection() throws DataAccessException { //if you want you could consider to use string builder to work with string instead of concatenation with +
        // Check if there is a config file next to the executable. If it doesn't exist, check the environmental variable
        // If the env variable exists, set the credentials into a config file for later use.
        String jdbc_url = "jdbc:sqlserver://";
        if (readConfig()) {
            jdbc_url += jdbcHostname + ";databaseName=";
            jdbc_url += jdbcDatabase + ";user=";
            jdbc_url += jdbcUsername + ";password=";
            jdbc_url += jdbcPassword + ";";
        } else if (System.getenv("TEVOS_Hostname") != null && System.getenv("TEVOS_Database") != null &&
                System.getenv("TEVOS_Username") != null && System.getenv("TEVOS_Password") != null) {
            jdbcHostname = System.getenv("TEVOS_Hostname");
            jdbcDatabase = System.getenv("TEVOS_Database");
            jdbcUsername = System.getenv("TEVOS_Username");
            jdbcPassword = System.getenv("TEVOS_Password");

            jdbc_url += jdbcHostname + ";databaseName=";
            jdbc_url += jdbcDatabase + ";user=";
            jdbc_url += jdbcUsername + ";password=";
            jdbc_url += jdbcPassword + ";";

            writeConfig(jdbcHostname, jdbcDatabase, jdbcUsername, jdbcPassword);
            System.err.println("The environmental variables has been written to the 'config.properties' file for later use.\n" +
                    "You can unset the 'TEVOS_*' environmental variables as they won't be used later.");
        } else {
            writeConfig();
            System.err.println("There is no config file or environmental variable set.\n" +
                    "A 'config.properties' file has been created. Please set the database credentials and try again.");
            System.exit(-1);
        }
        try {
            connection = DriverManager.getConnection(jdbc_url);
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * According to the singleton pattern, we have a private constructor and a public getInstance method which makes
     * sure there is only one instance of the DBConnection class at runtime.
     *
     * @return It returns the single available instance object of the DBConnection class
     * @throws DataAccessException when SQLException inside the method
     */
    public static DBConnection getInstance() throws DataAccessException {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    /**
     * Closes the connection and deletes the instance (sets it to null)
     */
    public void closeConnection() throws DataAccessException {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new DataAccessException("Couldn't close connection.\n\t" + e.getMessage());
        }
        instance = null;
    }

    public Connection getDBConn() {
        return connection;
    }

    public Boolean instanceIsNull() {
        return (instance == null);
    }

    /**
     * This method checks if a config file exists
     *
     * @return true if the file exists and the properties can be read properly, false if the file doesn't exist.
     */
    private Boolean readConfig() throws DataAccessException {
        Properties properties = new Properties();
        String fileName = "config.properties";
        InputStream is = null;
        try {
            is = new FileInputStream(fileName);
            properties.load(is);
            jdbcHostname = properties.getProperty("hostname");
            jdbcDatabase = properties.getProperty("database");
            jdbcUsername = properties.getProperty("username");
            jdbcPassword = properties.getProperty("password");
            is.close();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            throw new DataAccessException("Invalid config file.");
        }
    }

    /**
     * Writes out the given db credentials into a properties file
     *
     * @throws DataAccessException if the config file cannot be written to disk
     */
    private void writeConfig(String hostname, String database, String username, String password) throws DataAccessException {
        try {
            //Set the properties
            Properties properties = new Properties();
            properties.setProperty("hostname", hostname);
            properties.setProperty("database", database);
            properties.setProperty("username", username);
            properties.setProperty("password", password);

            //Save the properties to file
            properties.store(new FileOutputStream("../config.properties"), "Warehouse Management System - Properties");
        } catch (IOException e) {
            throw new DataAccessException("Error writing config file to disk.\n\t" + e.getMessage());
        }
    }

    /**
     * Writes out a template credentials properties file
     *
     * @throws DataAccessException if the config file cannot be written to disk
     */
    private void writeConfig() throws DataAccessException {
        try {
            //Set the properties
            Properties properties = new Properties();
            properties.setProperty("hostname", "CHANGEME");
            properties.setProperty("database", "CHANGEME");
            properties.setProperty("username", "CHANGEME");
            properties.setProperty("password", "CHANGEME");

            //Save the properties to file
            properties.store(new FileOutputStream("./config.properties"), "Warehouse Management System - Properties");
        } catch (IOException e) {
            throw new DataAccessException("Error writing config file to disk.\n\t" + e.getMessage());
        }
    }

    // HERE STARTS THE QUERY EXECUTION METHODS

    /**
     * Executes a given select query on the database
     *
     * @param query is a valid SQL PreparedStatement
     * @return It returns a ResultSet containing the query's result
     * @throws DataAccessException when there is a problem connection to the database or the given query is not valid
     */
    public ResultSet executeSelect(PreparedStatement query) throws DataAccessException {
        ResultSet rs;
        try {
            rs = query.executeQuery();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        return rs;
    }

    /**
     * Executes a given update or delete query on the database
     *
     * @param query is a valid SQL PreparedStatement
     * @return It returns the number of rows affected by the given query
     * @throws DataAccessException when there is a problem connection to the database or the given query is not valid
     */
    public int executeQuery(PreparedStatement query) throws DataAccessException {
        int result;
        try {
            result = query.executeUpdate();
            query.close();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return result;
    }

    /**
     * Executes a given insert query on the database
     *
     * @param query is a valid SQL PreparedStatement
     * @return It returns the generated key of the inserted row
     * @throws DataAccessException when there is a problem connection to the database or the given query is not valid
     */
    public int executeInsertWithID(PreparedStatement query) throws DataAccessException {
        ResultSet rs;
        int generatedKey = -1;
        try {
            query.executeUpdate();
            rs = query.getGeneratedKeys();
            if (rs.next()) {
                generatedKey = rs.getInt(1);
            }
            query.close();
        } catch (SQLException throwables) {
            throw new DataAccessException(throwables.getMessage());
        }
        return generatedKey;
    }
}
