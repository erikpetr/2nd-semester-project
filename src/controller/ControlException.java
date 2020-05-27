package controller;

/**
 * This exception is thrown when there is some error in the controller layer of the program
 *
 * @author dmai0919-group3@UCNDK.onmicrosoft.com
 */
public class ControlException extends Exception {
    public ControlException(String message) {
        super(message);
    }
}
