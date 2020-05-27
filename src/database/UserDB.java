package database;

import model.Store;
import model.User;
import model.Warehouse;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDB implements DAOInterface<User>{
    DBConnection db = DBConnection.getInstance();

    public UserDB() throws DataAccessException {

    }

    /**
     * This method takes an object and converts it to a valid SQL INSERT query, which is the executed
     * Given a valid user which doesnt exist in the database, it inserts it to the DB
     * @param value it's the given T type object (in this case User)
     * @return the generated key after the insertion to the DB
     * @see DBConnection executeInsertWithID() method
     */
    @Override
    public int create(User value) throws DataAccessException {
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

            return db.executeInsertWithID(s);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new DataAccessException();
        }
    }

    /**
     * This method takes an ID and converts it to a valid SQL SELECT query, which is the executed
     *
     * @param id is the ID which we want to search for in the database
     * @return the single object with the given ID
     * @see DBConnection executeSelect() method
     */
    @Override
    public User selectByID(int id) throws DataAccessException {
        String queryStore = "SELECT TOP 1 * FROM 'Store' WHERE id=?";
        String queryWarehouse = "SELECT TOP 1 * FROM 'Warehouse' WHERE id=?";
        try {
            PreparedStatement s = db.getDBConn().prepareStatement(queryStore);
            s.setInt(1, id);
            ResultSet rs = db.executeSelect(s);
            AddressDB addressDB = new AddressDB();
            if (rs.next()) {
                return new Store(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("password"),
                        rs.getString("email"),
                        addressDB.selectByID(rs.getInt("addressID"))
                );
            } else {
                s = db.getDBConn().prepareStatement(queryWarehouse);
                s.setInt(1, id);
                rs = db.executeSelect(s);
                if (rs.next()) {
                    return new Warehouse(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("password"),
                            rs.getString("email"),
                            addressDB.selectByID(rs.getInt("addressID"))
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new DataAccessException();
        }
        return null;
    }

    @Override
    public List<User> all() throws DataAccessException {
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
    public int update(User value) throws DataAccessException {
        String queryStore = "UPDATE 'Store' SET name=?, email=?, password=?;";
        String queryWarehouse = "UPDATE 'Warehouse' SET name=?, email=?, password=?;";
        PreparedStatement s;
        AddressDB addressDB = new AddressDB();
        int rows = -1;
        try {
            if (value instanceof Store) {
                s = db.getDBConn().prepareStatement(queryStore);
            } else if (value instanceof Warehouse) {
                s = db.getDBConn().prepareStatement(queryWarehouse);
            } else return rows;
            s.setString(1, value.getName());
            s.setString(2, value.getEmail());
            s.setString(3, value.getPassword());
            rows = db.executeQuery(s);
            rows += addressDB.update(value.getAddress());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new DataAccessException();
        }
        return rows;
    }

    /**
     * This method takes an object and converts it to a valid SQL DELETE query, which is the executed
     *
     * @param value it's the given T type object
     * @return the number of rows deleted from the table
     * @see DBConnection executeQuery()
     */
    @Override
    public int delete(User value) throws DataAccessException {
        String queryStore = "DELETE FROM 'Store' WHERE id=?;";
        String queryWarehouse = "DELETE FROM 'Warehouse' WHERE id=?;";
        PreparedStatement s;
        int rows = -1;
        try {
            if (value instanceof Store) {
                s = db.getDBConn().prepareStatement(queryStore);
            } else if (value instanceof Warehouse) {
                s = db.getDBConn().prepareStatement(queryWarehouse);
            } else return rows;
            s.setInt(1, value.getId());
            rows = db.executeQuery(s);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new DataAccessException();
        }
        return rows;
    }
}
