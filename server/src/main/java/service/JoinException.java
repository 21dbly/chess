package service;

import exceptions.ResponseException;

public class JoinException extends ResponseException {
    public JoinException() {
        super(403, "Error: already taken");
    }
    public JoinException(int code, String message) {super(code, message);}
}
