package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import exceptions.ResponseException;
import model.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChessServiceTests {
    final ChessService service = new ChessService(
            new MemoryUserDAO(), new MemoryAuthDAO(), new MemoryGameDAO()
    );

    final UserData user1 = new UserData("userOne", "pass111", "one@mail.com");
    final LoginRequest user1Login = new LoginRequest("userOne", "pass111");
    final LoginRequest wrongPasswordLogin = new LoginRequest("userOne", "wrongPass");
    final UserData testSameUsername = new UserData("userOne", "differentPass", "different@mail.com");
    final UserData user2 = new UserData("userTwo", "pass222", "two@mail.com");
    final LoginRequest user2Login = new LoginRequest("userTwo", "pass222");
    final String game1Name = "game1!";
    final String game2Name = "game2?";

    @Test
    void registerValid() throws ResponseException {
        // requires login to work
        service.clear();
        service.register(user1);
        AuthData authData = service.login(user1Login);
        assertSame(authData.username(), user1.username());
    }

    @Test
    void registerExisting() throws ResponseException {
        service.clear();
        service.register(user1);
        assertThrows(RegistrationException.class, () ->
                service.register(testSameUsername));
    }

    @Test
    void loginValid() throws ResponseException {
        service.clear();
        service.register(user1);
        AuthData authData = service.login(user1Login);
        assertSame(authData.username(), user1.username());
        assertNotNull(authData.authToken());
    }

    @Test
    void loginWrongPassword() throws ResponseException {
        service.clear();
        service.register(user1);
        assertThrows(UnauthorizedException.class, () ->
                service.login(wrongPasswordLogin));
    }

    @Test
    void loginWrongUsername() throws ResponseException {
        service.clear();
        service.register(user1);
        assertThrows(UnauthorizedException.class, () ->
                service.login(user2Login));
    }

    @Test
    void authorizeValid() throws ResponseException {
        service.clear();
        AuthData authData = service.register(user1);
        String username = service.authorize(authData.authToken());
        assertSame(username, authData.username());
    }

    @Test
    void authorizeMultiple() throws ResponseException {
        service.clear();
        AuthData authData1 = service.register(user1);
        AuthData authData2 = service.register(user2);
        String username1 = service.authorize(authData1.authToken());
        String username2 = service.authorize(authData2.authToken());
        assertSame(username1, authData1.username());
        assertSame(username2, authData2.username());
    }

    @Test
    void authorizeInvalid() throws ResponseException {
        service.clear();
        service.register(user1);
        assertThrows(UnauthorizedException.class, () ->
                service.authorize("123456789"));
    }

    @Test
    void logoutValid() throws ResponseException {
        service.clear();
        AuthData authData = service.register(user1);
        service.logout(authData.authToken());
        assertThrows(UnauthorizedException.class, () ->
                service.authorize(authData.authToken()));
    }

    @Test
    void logoutInvalid() throws ResponseException {
        service.clear();
        AuthData authData = service.register(user1);
        assertThrows(UnauthorizedException.class, () ->
                service.logout("123456789"));
    }

    @Test
    void createGameValid() throws ResponseException {
        service.clear();
        AuthData authData = service.register(user1);
        int gameID = service.createGame(game1Name);
        assert(gameID >= 0);
    }

    @Test
    void listGamesEmpty() throws ResponseException {
        service.clear();
        AuthData authData = service.register(user1);
        var games = service.listGames();
        assert(games.isEmpty());
    }

    @Test
    void listGamesNotEmpty() throws ResponseException {
        // requires createGame to work
        service.clear();
        AuthData authData = service.register(user1);
        int game1ID = service.createGame(game1Name);
        int game2ID = service.createGame(game2Name);
        var games = service.listGames();
        assert(games.size() == 2);
        for (GameData game : games) {
            if (game.gameID() == game1ID) {
                assertSame(game.gameName(), game1Name);
            } else {
                assertSame(game.gameID(), game2ID);
                assertSame(game.gameName(), game2Name);
            }
        }
    }

    @Test
    void joinGameValid() throws ResponseException{
        // requires createGame and listGames to work
        service.clear();
        AuthData authData = service.register(user1);
        int game1ID = service.createGame(game1Name);
        service.joinGame(new JoinGameRequest("WHITE", game1ID), user1.username());
        var games = service.listGames();
        GameData game = games.iterator().next();
        assertSame(game.whiteUsername(), user1.username());
        assertNull(game.blackUsername());
        service.joinGame(new JoinGameRequest("BLACK", game1ID), user1.username());
        games = service.listGames();
        game = games.iterator().next();
        assertSame(game.blackUsername(), user1.username());
    }

    @Test
    void joinGameNonexistent() throws ResponseException{
        // requires createGame and listGames to work
        service.clear();
        AuthData authData = service.register(user1);
        int game1ID = service.createGame(game1Name);
        assertThrows(BadRequestException.class, () ->
                service.joinGame(new JoinGameRequest("WHITE", 12345), user1.username()));
    }

    @Test
    void joinGameTaken() throws ResponseException{
        // requires createGame and listGames to work
        service.clear();
        AuthData authData = service.register(user1);
        int game1ID = service.createGame(game1Name);
        service.joinGame(new JoinGameRequest("WHITE", game1ID), user1.username());
        assertThrows(JoinException.class, () ->
                service.joinGame(new JoinGameRequest("WHITE", game1ID), user1.username()));
        service.joinGame(new JoinGameRequest("BLACK", game1ID), user1.username());
        assertThrows(JoinException.class, () ->
                service.joinGame(new JoinGameRequest("BLACK", game1ID), user1.username()));
    }

    @Test
    void clearValid() throws ResponseException {
        service.clear();
        AuthData authData = service.register(user1);
        int game1ID = service.createGame(game1Name);
        service.clear();
        assertThrows(UnauthorizedException.class, () ->
                service.authorize(authData.authToken()));
        assertThrows(UnauthorizedException.class, () ->
                service.login(user1Login));
        assert(service.listGames().isEmpty());
    }
}
