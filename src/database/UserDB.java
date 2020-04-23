package database;

import model.Address;
import model.Store;
import model.User;
import model.Warehouse;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.zip.DataFormatException;

public class UserDB implements DBInterface<User>{
    DBConnection db = DBConnection.getInstance();
    /**
     * This method takes an object and converts it to a valid SQL INSERT query, which is the executed
     * Given a valid user which doesnt exist in the database, it inserts it to the DB
     * @param value it's the given T type object (in this case User)
     * @return the generated key after the insertion to the DB
     * @see DBConnection executeInsertWithID() method
     */
    @Override
    public int create(User value) {
        String queryStore = "INSERT INTO 'Store' ('name', 'email', 'password', 'addressID') VALUES (?, ?, ?, ?);";
        String queryWarehouse = "INSERT INTO 'Warehouse' ('name', 'email', 'password', 'addressID') VALUES (?, ?, ?, ?);";
        AddressDB addressDB = new AddressDB();
        int addressID = addressDB.create(value.getAddress());
        try {
            PreparedStatement s;
            if (value instanceof Store) {
                s = db.getDBConn().prepareStatement(queryStore);
            } else if (value instanceof Warehouse) {
                s = db.getDBConn().prepareStatement(queryWarehouse);
            } else return -1;
            s.setString(1, value.getName());
            s.setString(2, value.getEmail());
            s.setString(3, value.getPassword());
            s.setInt(4, addressID);

            return db.executeInsertWithID(s.toString());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return -1;
    }

    /**
     * This method takes an ID and converts it to a valid SQL SELECT query, which is the executed
     *
     * @param id is the ID which we want to search for in the database
     * @return the single object with the given ID
     * @see DBConnection executeSelect() method
     */
    @Override
    public User selectByID(int id) {
        return null;
    }

    /**
     * This method takes a column name and a search value, converts it to a valid SQL SELECT query, which is the executed
     *
     * @param column the columns name we want to search in
     * @param value  the value we want to search for
     * @return the ResultSet containing all the results of the query
     * @see DBConnection executeSelect() method
     */
    @Override
    public ResultSet selectByString(String column, String value) {
        return null;
    }

    /**
     * This method takes an object and converts it to a valid SQL UPDATE query, which is the executed
     *
     * @param value it's the given T type object
     * @return the number of rows affected by the update
     * @see DBConnection executeQuery() method
     */
    @Override
    public int update(User value) {
        return 0;
    }

    /**
     * This method takes an object and converts it to a valid SQL DELETE query, which is the executed
     *
     * @param value it's the given T type object
     * @return the number of rows deleted from the table
     * @see DBConnection executeQuery()
     */
    @Override
    public int delete(User value) {
        return 0;
    }
}