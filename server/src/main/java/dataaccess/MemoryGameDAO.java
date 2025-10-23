package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    final private HashMap<Integer, GameData> games = new HashMap<>();
    private int nextID = 1;

    private int getNextID() {
        return nextID++;
    }

    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        int gameID = getNextID();
        games.put(gameID, new GameData(gameID, null, null, gameName, null));
        return gameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        games.put(gameData.gameID(), gameData);
    }
}
