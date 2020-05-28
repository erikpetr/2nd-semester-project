package database;

import model.Address;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for Address via DAOInterface
 *
 * @author dmai0919-group3@UCNDK.onmicrosoft.com
 */
public class AddressDB implements DAOInterface<Address> {

    DBConnection db = DBConnection.getInstance();

    /**
     * Default empty constructor so we can pass along the DataAccessException from DBConnection when it occurs
     *
     * @throws DataAccessException when DBConnection.getInstance() throws an exception.
     */
    public AddressDB() throws DataAccessException {
        /*
         This constructor doesn't need any contents because it's only purpose is to pass along an already
         thrown DataAccessException from a lower layer towards the controllers and GUI
         */
    }

    /**
     * @param value it's the given Address object
     * @return the generated key after the insertion to the DB
     * @throws DataAccessException when SQLException inside the method
     * @inheritDoc
     * @see DBConnection executeInsertWithID() method
     */
    @Override
    public int create(Address value) throws DataAccessException {
        String query = "insert into Address (country, region, zipcode, city, street, number, supplement)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?);";

        try (PreparedStatement s = db.getDBConn().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            s.setString(1, value.getCountry());
            s.setString(2, value.getRegion());
            s.setString(3, value.getZipcode());
            s.setString(4, value.getCity());
            s.setString(5, value.getStreet());
            s.setString(6, value.getNumber());
            s.setString(7, value.getSupplement());

            return db.executeInsertWithID(s);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * @param id is the ID which we want to search for in the database
     * @return the single Address object with the given ID
     * @throws DataAccessException when SQLException inside the method
     * @inheritDoc
     * @see DBConnection executeSelect() method
     */
    @Override
    public Address selectByID(int id) throws DataAccessException {
        String query = "SELECT TOP 1 * FROM Address WHERE id=?";
        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {

            s.setInt(1, id);

            ResultSet rs = db.executeSelect(s);
            if (rs.next()) {
                return new Address(
                        rs.getInt("id"),
                        rs.getString("number"),
                        rs.getString("supplement"),
                        rs.getString("street"),
                        rs.getString("city"),
                        rs.getString("zipcode"),
                        rs.getString("region"),
                        rs.getString("country")
                );
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    /**
     * @param value it's the given Address object
     * @return the number of rows affected by the update
     * @throws DataAccessException when SQLException inside the method
     * @inheritDoc
     * @see DBConnection executeQuery() method
     */
    @Override
    public int update(Address value) throws DataAccessException {
        String query = "UPDATE Address SET number=?, supplement=?, street=?, city=?, zipcode=?, region=?, country=? WHERE id=?";
        int rows = -1;
        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {
            s.setString(1, value.getNumber());
            s.setString(2, value.getSupplement());
            s.setString(3, value.getStreet());
            s.setString(4, value.getCity());
            s.setString(5, value.getZipcode());
            s.setString(6, value.getRegion());
            s.setString(7, value.getCountry());
            s.setInt(8, value.getId());
            rows = db.executeQuery(s);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return rows;
    }

    /**
     * @param value it's the given Address object
     * @return the number of rows deleted from the table
     * @throws DataAccessException when SQLException inside the method
     * @inheritDoc
     * @see DBConnection executeQuery()
     */
    @Override
    public int delete(Address value) throws DataAccessException {
        String query = "DELETE FROM Address WHERE id=?";
        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {
            s.setInt(1, value.getId());
            return db.executeQuery(s);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * @return a List containing all the Address objects from the database
     * @throws DataAccessException when SQLException inside the method
     * @inheritDoc
     */
    @Override
    public List<Address> all() throws DataAccessException {
        String query = "SELECT * FROM Address;";
        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {
            ResultSet rs = db.executeSelect(s);
            List<Address> resultList = new ArrayList<>();

            while (rs.next()) {
                Address address = new Address(
                        rs.getInt("id"),
                        rs.getString("number"),
                        rs.getString("supplement"),
                        rs.getString("street"),
                        rs.getString("city"),
                        rs.getString("zipcode"),
                        rs.getString("region"),
                        rs.getString("country")
                );
                resultList.add(address);
            }
            return resultList;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
