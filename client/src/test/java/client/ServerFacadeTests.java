package client;

import exceptions.ResponseException;
import model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import server.Server;
import serverfacade.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        facade = new ServerFacade("http://localhost:"+port);
    }

    @BeforeEach
    public void clear() throws ResponseException {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    final UserData user1 = new UserData("userOne", "pass111", "one@mail.com");
    final String game1Name = "game1!";
    final String game2Name = "game2?";

    @Test
    public void registerValid() throws ResponseException{
        AuthData authData = facade.register(user1);
        assertNotNull(authData);
        assertEquals(authData.username(), user1.username());
        assertNotNull(authData.authToken());
    }

    @Test
    public void registerExisting() throws ResponseException{
        facade.register(user1);
        assertThrows(ResponseException.class, () ->
                facade.register(user1));
    }

    @Test
    void loginValid() throws ResponseException {
        facade.register(user1);
        AuthData authData = facade.login(user1.username(), user1.password());
        assertEquals(authData.username(), user1.username());
        assertNotNull(authData.authToken());
    }

    @Test
    void loginWrongPassword() throws ResponseException {
        facade.register(user1);
        assertThrows(ResponseException.class, () ->
                facade.login(user1.username(), "wrongpassword"));
    }

    @Test
    void loginWrongUsername() throws ResponseException {
        facade.register(user1);
        assertThrows(ResponseException.class, () ->
                facade.login("wrongusername", user1.password()));
    }

    @Test
    void logoutValid() throws ResponseException {
        // requires listGames to work
        AuthData authData = facade.register(user1);
        facade.logout(authData.authToken());
        assertThrows(ResponseException.class, () ->
                facade.listGames(authData.authToken()));
    }

    @Test
    void logoutInvalid() throws ResponseException {
        AuthData authData = facade.register(user1);
        assertThrows(ResponseException.class, () ->
                facade.logout("notAValidAuthToken"));
    }

    @Test
    void createGameValid() throws ResponseException {
        AuthData authData = facade.register(user1);
        assertDoesNotThrow(() ->
            facade.createGame(authData.authToken(), game1Name));
    }

    @Test
    void createGameUnauthorized() throws ResponseException {
        AuthData authData = facade.register(user1);
        assertThrows(ResponseException.class, ()->
                facade.createGame("invalidAuthToken", game1Name));
    }

    @Test
    void listGamesEmpty() throws ResponseException {
        AuthData authData = facade.register(user1);
        var games = facade.listGames(authData.authToken());
        assert(games.isEmpty());
    }

    @Test
    void listGamesNotEmpty() throws ResponseException {
        AuthData authData = facade.register(user1);
        facade.createGame(authData.authToken(), game1Name);
        facade.createGame(authData.authToken(), game2Name);
        var games = facade.listGames(authData.authToken());
        assert(games.size() == 2);
        int game1sFound = 0;
        int game2sFound = 0;
        for (GameData game : games) {
            if (game.gameName().equals(game1Name)) {
                game1sFound++;
            } else if (game.gameName().equals(game2Name)){
                game2sFound++;
            }
        }
        assertEquals(1, game1sFound);
        assertEquals(1, game2sFound);
    }

    @Test
    void listGamesUnauthorized() throws ResponseException {
        AuthData authData = facade.register(user1);
        assertThrows(ResponseException.class, ()->
                facade.listGames("invalidAuthToken"));
    }

    @Test
    void joinGameValid() throws ResponseException{
        AuthData authData = facade.register(user1);
        facade.createGame(authData.authToken(), game1Name);
        var games = facade.listGames(authData.authToken());
        int game1ID = games.iterator().next().gameID();
        facade.joinGame(authData.authToken(), "WHITE", game1ID);
        games = facade.listGames(authData.authToken());
        GameData game = games.iterator().next();
        assertEquals(game.whiteUsername(), user1.username());
        assertNull(game.blackUsername());
        facade.joinGame(authData.authToken(), "BLACK", game1ID);
        games = facade.listGames(authData.authToken());
        game = games.iterator().next();
        assertEquals(game.whiteUsername(), user1.username());
        assertEquals(game.blackUsername(), user1.username());
    }

    @Test
    void joinGameNonexistent() throws ResponseException{
        AuthData authData = facade.register(user1);
        facade.createGame(authData.authToken(), game1Name);
        assertThrows(ResponseException.class, () ->
                facade.joinGame(authData.authToken(), "WHITE", 123));
    }

    @Test
    void joinGameTaken() throws ResponseException{
        AuthData authData = facade.register(user1);
        facade.createGame(authData.authToken(), game1Name);
        var games = facade.listGames(authData.authToken());
        int game1ID = games.iterator().next().gameID();
        facade.joinGame(authData.authToken(), "WHITE", game1ID);
        assertThrows(ResponseException.class, () ->
                facade.joinGame(authData.authToken(), "WHITE", game1ID));
        facade.joinGame(authData.authToken(), "BLACK", game1ID);
        assertThrows(ResponseException.class, () ->
                facade.joinGame(authData.authToken(), "BLACK", game1ID));
    }

    @Test
    void clearValid() throws ResponseException {
        AuthData authData = facade.register(user1);
        facade.createGame(authData.authToken(), game1Name);
        facade.clear();
        assertThrows(ResponseException.class, () ->
                facade.listGames(authData.authToken()));
        assertThrows(ResponseException.class, () ->
                facade.login(user1.username(), user1.password()));
        AuthData authData2 = facade.register(user1);
        assert(facade.listGames(authData2.authToken()).isEmpty());
    }
}
