package service;

import dataaccess.*;
import model.*;

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

    public AuthData register(UserData registerRequest) throws DataAccessException, RegistrationException{
        var existingUser = userDAO.getUser(registerRequest.username());
        if (existingUser != null) {
            throw new RegistrationException("User already exists");
        }
        throw new RuntimeException("Not fully implemented");
    }

    public AuthData login(LoginRequest loginRequest) {
        throw new RuntimeException("Not implemented");
    }

    public void logout(String authToken) {
        throw new RuntimeException("Not implemented");
    }
}
