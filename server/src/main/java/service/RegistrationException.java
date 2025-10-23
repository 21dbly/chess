package service;

import exceptions.ResponseException;

public class RegistrationException extends ResponseException {
    public RegistrationException() {
        super(403, "Error: already taken");
    }
    public RegistrationException(int code, String message) {super(code, message);}
}
