package service;

import dataaccess.*;
import exceptions.ResponseException;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

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
        UserData hashedData = hashUserPassword(registerRequest);
        userDAO.createUser(hashedData);
        AuthData authData = new AuthData(generateToken(), registerRequest.username());
        authDAO.createAuth(authData);
        return authData;
    }

    private UserData hashUserPassword(UserData userData) {
        String hashedPass = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        return new UserData(userData.username(), hashedPass, userData.email());
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
        if (!BCrypt.checkpw(loginRequest.password(), user.password())) {
            throw new UnauthorizedException();
        }
        AuthData authData = new AuthData(generateToken(), user.username());
        authDAO.createAuth(authData);
        return authData;
    }

    public void logout(String authToken)
            throws DataAccessException, UnauthorizedException {
        authorize(authToken);
        authDAO.deleteAuth(authToken);
    }

    public String authorize(String authToken)
            throws DataAccessException, UnauthorizedException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedException();
        }
        return authData.username();
    }

    public int createGame(String gameName)
            throws DataAccessException, BadRequestException {
        if (gameName == null) {
            throw new BadRequestException();
        }
        return gameDAO.createGame(gameName); // returns gameID
    }

    public Collection<GameData> listGames() throws DataAccessException {
        return gameDAO.listGames();
    }

    public void joinGame(JoinGameRequest request, String userName)
            throws BadRequestException, DataAccessException, JoinException {
        if (!request.isValid()) {
            throw new BadRequestException();
        }
        GameData game = gameDAO.getGame(request.gameID());
        if (game == null) {
            throw new BadRequestException();
        }
        GameData updatedGame;
        if (Objects.equals(request.playerColor(), "WHITE")) {
            if (game.whiteUsername() != null) {
                throw new JoinException();
            }
            updatedGame = new GameData(game.gameID(), userName, game.blackUsername(), game.gameName(), game.game());
        } else {
            if (game.blackUsername() != null) {
                throw new JoinException();
            }
            updatedGame = new GameData(game.gameID(), game.whiteUsername(), userName, game.gameName(), game.game());
        }
        gameDAO.updateGame(updatedGame);
    }
}
