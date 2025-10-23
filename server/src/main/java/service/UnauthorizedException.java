package service;

import exceptions.ResponseException;

public class UnauthorizedException extends ResponseException {
    public UnauthorizedException() {
        super(401, "Error: unauthorized");
    }
}
