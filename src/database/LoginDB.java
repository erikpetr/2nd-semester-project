package database;

import model.Store;
import model.User;
import model.Warehouse;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO class for User via LoginDAO
 *
 * @author dmai0919-group3@UCNDK.onmicrosoft.com
 */
public class LoginDB implements LoginDAO {
    DBConnection db = DBConnection.getInstance();

    /**
     * Default empty constuctor
     *
     * @throws DataAccessException when there are some SQLException down the chain (most likely in the DBConnection)
     */
    public LoginDB() throws DataAccessException {
        //This constructor is empty because it only exists to allow DataAccessException to be passed over from DBConnection
    }

    /**
     * Takes a username and password and tries to find it in the database. If the user exists, return it
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return a User object if the credentials match something in the DB or null if there is no user
     * @throws DataAccessException when there is some SQLException catched in the method
     */
    @Override
    public User getByCredentials(String username, String password) throws DataAccessException {
        String queryStore = "SELECT TOP 1 * FROM Store WHERE name=? AND password=?";
        String queryWarehouse = "SELECT TOP 1 * FROM Warehouse WHERE name=? AND password=?";

        try (PreparedStatement s = db.getDBConn().prepareStatement(queryStore)) {
            s.setString(1, username);
            s.setString(2, password);
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
                try (PreparedStatement ps = db.getDBConn().prepareStatement(queryWarehouse)) {
                    ps.setString(1, username);
                    ps.setString(2, password);
                    rs = db.executeSelect(ps);
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
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

}
