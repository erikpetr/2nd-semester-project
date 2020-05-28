package database;

import java.util.List;

/**
 * This interface is used in connection with the DAO pattern
 *
 * @param <T> with this parameter you can set a type which you will use in later methods as parameters
 */
public interface DAOInterface<T> {

    /**
     * This method takes an object and converts it to a valid SQL INSERT query, which is the executed
     *
     * @param value it's the given T type object
     * @return the generated key after the insertion to the DB
     * @throws DataAccessException when SQLException inside the method
     * @see DBConnection executeInsertWithID() method
     */
    int create(T value) throws DataAccessException;

    /**
     * This method takes an ID and converts it to a valid SQL SELECT query, which is the executed
     *
     * @param id is the ID which we want to search for in the database
     * @return the single object with the given ID
     * @throws DataAccessException when SQLException inside the method
     * @see DBConnection executeSelect() method
     */
    T selectByID(int id) throws DataAccessException;

    /**
     * @return List of all entities
     * @throws DataAccessException when SQLException inside the method
     */
    List<T> all() throws DataAccessException;

    /**
     * This method takes an object and converts it to a valid SQL UPDATE query, which is the executed
     *
     * @param value it's the given T type object
     * @return the number of rows affected by the update
     * @throws DataAccessException when SQLException inside the method
     * @see DBConnection executeQuery() method
     */
    int update(T value) throws DataAccessException;

    /**
     * This method takes an object and converts it to a valid SQL DELETE query, which is the executed
     *
     * @param value it's the given T type object
     * @return the number of rows deleted from the table
     * @throws DataAccessException when SQLException inside the method
     * @see DBConnection executeQuery()
     */
    int delete(T value) throws DataAccessException;
}
