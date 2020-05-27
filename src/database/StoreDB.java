package database;

import model.Store;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StoreDB implements DAOInterface<Store>{
    DBConnection db = DBConnection.getInstance();
    
    public StoreDB() throws DataAccessException {
        //This constructor is empty because it only exists to pass along DataAccessException from DBConnection.getInstance()
	}
    
    /**
     * This method takes an object and converts it to a valid SQL INSERT query, which is the executed
     * Given a valid user which doesnt exist in the database, it inserts it to the DB
     * @param value it's the given T type object (in this case Store)
     * @return the generated key after the insertion to the DB
     * @see DBConnection executeInsertWithID() method
     */
    @Override
    public int create(Store value) throws DataAccessException {
        String queryStore = "INSERT INTO Store (name, email, password, addressID) VALUES (?, ?, ?, ?);";
        AddressDB addressDB = new AddressDB();
        int addressID = addressDB.create(value.getAddress());
        try (PreparedStatement s = db.getDBConn().prepareStatement(queryStore, Statement.RETURN_GENERATED_KEYS)) {
            s.setString(1, value.getName());
            s.setString(2, value.getEmail());
            s.setString(3, value.getPassword());
            s.setInt(4, addressID);

            return db.executeInsertWithID(s);
        } catch (SQLException e) {
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
    public Store selectByID(int id) throws DataAccessException {
        String queryStore = "SELECT TOP 1 * FROM Store WHERE id=?";
        try (PreparedStatement s = db.getDBConn().prepareStatement(queryStore)) {
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
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    /**
     *
     * @return List of all entities
     * @throws DataAccessException
     */
    @Override
    public List<Store> all() throws DataAccessException {
        String query = "SELECT * FROM Store;";
        try {
        	AddressDB addressDB = new AddressDB();
            PreparedStatement s = db.getDBConn().prepareStatement(query);
            ResultSet rs = db.executeSelect(s);
            List<Store> resultList = new ArrayList<>();

            while (rs.next()) {
                resultList.add(new Store(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("password"),
                        rs.getString("email"),
                        addressDB.selectByID(rs.getInt("addressID")))) ;
            }
            return resultList;

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }



    /**
     * This method takes an object and converts it to a valid SQL UPDATE query, which is the executed
     *
     * @param value it's the given T type object
     * @return the number of rows affected by the update
     * @throws DataAccessException 
     * @see DBConnection executeQuery() method
     */

    @Override
    public int update(Store value) throws DataAccessException {
        // Address is to be updated in updateAddress method in AddressCtrl
        // TODO : See if password have to be salted and hashed
        String query = "UPDATE Store SET name=?, password=?, email=? WHERE id=?;";
        int rows = -1;
        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {
            s.setString(1, value.getName());
            s.setString(2, value.getPassword());
            s.setString(3, value.getEmail());
            s.setInt(4, value.getId());
            rows = db.executeQuery(s);
        } catch (SQLException e) {
            throw new DataAccessException();
        }
        return rows;
    }

    /**
     * This method takes an object and converts it to a valid SQL DELETE query, which is the executed
     *
     * @param value it's the given T type object
     * @return the number of rows deleted from the table
     * @throws DataAccessException 
     * @see DBConnection executeQuery()
     */
    @Override
    public int delete(Store value) throws DataAccessException {
        // Because of the tables cascade rule, we dont need to have separate stock query
        String query = "DELETE FROM Store WHERE id=?";
        int rows = -1;
        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {
            s.setInt(1, value.getId());
            rows = db.executeQuery(s);
        } catch (SQLException e) {
            throw new DataAccessException();
        }
        return rows;
    }
}
