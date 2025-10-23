package service;

import exceptions.ResponseException;

public class BadRequestException extends ResponseException {
    public BadRequestException() {
        super(400, "Error: bad request");
    }
    public BadRequestException(int code, String message) {super(code, message);}
}
