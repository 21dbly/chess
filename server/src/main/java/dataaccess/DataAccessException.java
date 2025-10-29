package dataaccess;

import exceptions.ResponseException;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends ResponseException {
    // is it a bad idea to modify this?
    public DataAccessException(String message) {
        super(500, message);
    }
    public DataAccessException(String message, Throwable ex) {
        super(500, message, ex);
    }
}
