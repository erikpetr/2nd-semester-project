package database;

import model.User;

/**
 * This DAO interface is used by the LoginController
 *
 * @see controller.LoginController
 */
public interface LoginDAO {

    User getByCredentials(String username, String password) throws DataAccessException;

}
