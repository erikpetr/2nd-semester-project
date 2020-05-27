package database;

/**
 * This exception is thrown when SQLException inside method
 *
 * @author dmai0919-group3@UCNDK.onmicrosoft.com
 */
public class DataAccessException extends Exception {
    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException() {
        super("Unknown Database Error");
    }
}
