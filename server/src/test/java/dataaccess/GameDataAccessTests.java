package dataaccess;

import exceptions.ResponseException;
import model.GameData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class GameDataAccessTests {
    
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
    void test(Class<? extends GameDAO> gameDAOclass) throws ResponseException {
        GameDAO gameDAO = getGameDAO(gameDAOclass);
    }
    
}