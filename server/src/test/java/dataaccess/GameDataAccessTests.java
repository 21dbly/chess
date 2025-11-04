package dataaccess;

import chess.ChessGame;
import exceptions.ResponseException;
import model.GameData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class GameDataAccessTests {

    final String game1Name = "game1!";
    final String game2Name = "game2?";

    private GameDAO getGameDAO(Class<? extends GameDAO> gameDAOClass) throws DataAccessException {
        GameDAO gameDAO;
        if (gameDAOClass.equals(SQLGameDAO.class)) {
            gameDAO = new SQLGameDAO();
        } else {
            gameDAO = new MemoryGameDAO();
        }
        gameDAO.clear();
        return gameDAO;
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLGameDAO.class, MemoryGameDAO.class})
    void createGameValid(Class<? extends GameDAO> gameDAOclass) throws ResponseException {
        GameDAO gameDAO = getGameDAO(gameDAOclass);
        assertDoesNotThrow(() ->
                gameDAO.createGame(game1Name));
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLGameDAO.class, MemoryGameDAO.class})
    void createGameNoName(Class<? extends GameDAO> gameDAOclass) throws ResponseException {
        GameDAO gameDAO = getGameDAO(gameDAOclass);
        assertThrows(DataAccessException.class, () ->
                gameDAO.createGame(null));
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLGameDAO.class, MemoryGameDAO.class})
    void createGameSameName(Class<? extends GameDAO> gameDAOclass) throws ResponseException {
        GameDAO gameDAO = getGameDAO(gameDAOclass);
        int firstID = gameDAO.createGame(game1Name);
        int secondID = gameDAO.createGame(game1Name);
        assertNotEquals(firstID, secondID);
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLGameDAO.class, MemoryGameDAO.class})
    void getGameValid(Class<? extends GameDAO> gameDAOclass) throws ResponseException {
        GameDAO gameDAO = getGameDAO(gameDAOclass);
        int gameID = gameDAO.createGame(game1Name);
        GameData returnedGame = gameDAO.getGame(gameID);
        assertEquals(gameID, returnedGame.gameID());
        assertEquals(game1Name, returnedGame.gameName());
        assertNotNull(returnedGame.game());
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLGameDAO.class, MemoryGameDAO.class})
    void getGameNone(Class<? extends GameDAO> gameDAOclass) throws ResponseException {
        GameDAO gameDAO = getGameDAO(gameDAOclass);
        int gameID = gameDAO.createGame(game1Name);
        GameData returnedGame = gameDAO.getGame(gameID + 1);
        assertNull(returnedGame);
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLGameDAO.class, MemoryGameDAO.class})
    void listGamesOne(Class<? extends GameDAO> gameDAOclass) throws ResponseException {
        GameDAO gameDAO = getGameDAO(gameDAOclass);
        int gameID = gameDAO.createGame(game1Name);
        var games = gameDAO.listGames();
        assert(games.size() == 1);
        GameData game = games.iterator().next();
        assertEquals(gameID, game.gameID());
        assertEquals(game1Name, game.gameName());
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLGameDAO.class, MemoryGameDAO.class})
    void listGamesTwo(Class<? extends GameDAO> gameDAOclass) throws ResponseException {
        GameDAO gameDAO = getGameDAO(gameDAOclass);
        int gameID1 = gameDAO.createGame(game1Name);
        int gameID2 = gameDAO.createGame(game2Name);
        var games = gameDAO.listGames();
        assert(games.size() == 2);
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLGameDAO.class, MemoryGameDAO.class})
    void listGamesNone(Class<? extends GameDAO> gameDAOclass) throws ResponseException {
        GameDAO gameDAO = getGameDAO(gameDAOclass);
        var games = gameDAO.listGames();
        assert(games.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLGameDAO.class, MemoryGameDAO.class})
    void updateGameValid(Class<? extends GameDAO> gameDAOclass) throws ResponseException {
        GameDAO gameDAO = getGameDAO(gameDAOclass);
        int gameID1 = gameDAO.createGame(game1Name);
        GameData newGameData = new GameData(gameID1, "white", "black", "newName", new ChessGame());
        gameDAO.updateGame(newGameData);
        assertEquals(newGameData, gameDAO.getGame(gameID1));
    }

    @ParameterizedTest
    @ValueSource(classes = {SQLGameDAO.class, MemoryGameDAO.class})
    void updateGameNullValues(Class<? extends GameDAO> gameDAOclass) throws ResponseException {
        GameDAO gameDAO = getGameDAO(gameDAOclass);
        int gameID1 = gameDAO.createGame(game1Name);
        GameData badGameData = new GameData(gameID1, "white", "black", null, null);
        assertThrows(DataAccessException.class, () ->
                gameDAO.updateGame(badGameData));
    }
    
}