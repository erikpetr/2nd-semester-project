package database;

import model.Warehouse;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class WarehouseDB implements DAOInterface<Warehouse>{
    DBConnection db = DBConnection.getInstance();
    
    public WarehouseDB() throws DataAccessException {
        //Empty constructor which allows DataAccessException to be thrown
	}
    
    /**
     * This method takes an object and converts it to a valid SQL INSERT query, which is the executed
     * Given a valid user which doesnt exist in the database, it inserts it to the DB
     * @param value it's the given T type object (in this case Warehouse)
     * @return the generated key after the insertion to the DB
     * @see DBConnection executeInsertWithID() method
     */
    @Override
    public int create(Warehouse value) throws DataAccessException {
        String queryWarehouse = "INSERT INTO Warehouse (name, email, password, addressID) VALUES (?, ?, ?, ?);";
        AddressDB addressDB = new AddressDB();
        int addressID = addressDB.create(value.getAddress());
        try (PreparedStatement s = db.getDBConn().prepareStatement(queryWarehouse, Statement.RETURN_GENERATED_KEYS)) {
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
    public Warehouse selectByID(int id) throws DataAccessException {
        String queryWarehouse = "SELECT TOP 1 * FROM Warehouse WHERE id=?";
        try (PreparedStatement s = db.getDBConn().prepareStatement(queryWarehouse)) {
            AddressDB addressDB = new AddressDB();
            s.setInt(1, id);
            ResultSet rs = db.executeSelect(s);
            if (rs.next()) {
                return new Warehouse(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("password"),
                        rs.getString("email"),
                        addressDB.selectByID(rs.getInt("addressID"))
                );
            }
        } catch (SQLException e) {
        	throw new DataAccessException();
        }
        return null;
    }

    /**
     *
     * @return List of all entities
     * @throws DataAccessException
     */
    @Override
    public List<Warehouse> all() throws DataAccessException {
        String query = "SELECT * FROM Warehouse";
        try (PreparedStatement s = db.getDBConn().prepareStatement(query)){
        	AddressDB addressDB = new AddressDB();
            try (ResultSet rs = s.executeQuery()) {
                List<Warehouse> resultList = new ArrayList<>();

                while (rs.next()) {
                    resultList.add(new Warehouse(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("password"),
                            rs.getString("email"),
                            addressDB.selectByID(rs.getInt("addressID"))));
                }
                return resultList;
            }
        } catch (SQLException e) {
            throw new DataAccessException();
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
    public int update(Warehouse value) throws DataAccessException {
        // Address is updated separately in AddressController
        String query= "UPDATE Warehouse SET name=?, password=?, email=? WHERE id=?;";
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
    public int delete(Warehouse value) throws DataAccessException {
        // Because of the tables cascade rule, we dont need to have separate stock query
        String query = "DELETE FROM Store WHERE id=?";
        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {
            s.setInt(1, value.getId());
            return db.executeQuery(s);
        } catch (SQLException e) {
            throw new DataAccessException();
        }
    }
}
