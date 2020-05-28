package controller;

import database.DataAccessException;
import database.LoginDAO;
import database.LoginDB;
import model.Address;
import model.User;

/**
 * Controller class which connects the GUI with the DAO
 *
 * @author dmai0919-group3@UCNDK.onmicrosoft.com
 */
public class LoginController {
    private static User loggedInUser;

    private LoginController() {/* Private constructor to hide the implicit public one */}

    /**
     * Simple static getter for the loggedInUser
     *
     * @return the user logged into the system
     */
    public static User getLoggedInUser() {
        if (loggedInUser == null) {
            return new User(-1, "<debug user>", "password", "email",
                    new Address(-1, "number", "supplement", "street", "city", "zipcode", "region", "country"));
        }
        return loggedInUser;
    }

    /**
     * This method is called by the GUI to log in a user to the system. This checks the user's credentials too.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return true if a user matches the given credentials
     * @throws DataAccessException if there is some error on the DAO layer
     * @throws ControlException    if the given credentials don't match anything from the DB
     */
    public static boolean logIn(String username, String password) throws DataAccessException, ControlException {
        LoginDAO loginDB = new LoginDB();
        User user = loginDB.getByCredentials(username, password);
        if (user != null) {
            loggedInUser = user;
            return true;
        } else {
            throw new ControlException("The entered username or password doesn't match our records.");
        }
    }

    /**
     * Checks if there is a user logged in, if there is, sets the loggedInUser object to null
     *
     * @return true if there was a user logged in, false if there wasn't
     */
    public static boolean logOut() {
        if (loggedInUser != null) {
            loggedInUser = null;
            return true;
        } else {
            return false;
        }
    }
}
