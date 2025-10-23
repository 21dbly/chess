package service;

import dataaccess.*;
import exceptions.ResponseException;
import model.*;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class ChessService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ChessService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public AuthData register(UserData registerRequest)
            throws DataAccessException, RegistrationException, BadRequestException {
        if (!registerRequest.isComplete()) {
            throw new BadRequestException();
        }

        var existingUser = userDAO.getUser(registerRequest.username());
        if (existingUser != null) {
            throw new RegistrationException();
        }
        userDAO.createUser(registerRequest);
        AuthData authData = new AuthData(generateToken(), registerRequest.username());
        authDAO.createAuth(authData);
        return authData;
    }

    public AuthData login(LoginRequest loginRequest)
            throws DataAccessException, UnauthorizedException, BadRequestException {
        if (loginRequest.username() == null || loginRequest.password() == null) {
            throw new BadRequestException();
        }

        UserData user = userDAO.getUser(loginRequest.username());
        if (user == null) {
            throw new UnauthorizedException();
        }
        if (!Objects.equals(user.password(), loginRequest.password())) {
            throw new UnauthorizedException();
        }
        AuthData authData = new AuthData(generateToken(), user.username());
        authDAO.createAuth(authData);
        return authData;
    }

    public void logout(String authToken) {
        throw new RuntimeException("Not implemented");
    }

    public UserData authorize(String authToken) {
        throw new RuntimeException("Not implemented");
    }
}
