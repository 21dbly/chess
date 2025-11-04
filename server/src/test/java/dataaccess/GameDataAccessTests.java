package dataaccess;

import exceptions.ResponseException;
import model.GameData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
    
}