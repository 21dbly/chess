package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

public class MemoryGameDAO implements GameDAO {
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
        if (gameName == null || gameName.isEmpty()) {
            throw new DataAccessException("must provide gameName");
        }
        int gameID = getNextID();
        games.put(gameID, new GameData(gameID, null, null, gameName, new ChessGame()));
        return gameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        var actualGames = games.values();
        // don't return actual game serialization for more efficiency
//        var gameInfos = actualGames.stream().map(game ->
//                new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), null))
//                .collect(Collectors.toList());
//        return gameInfos;
        return actualGames;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        if (gameData.game() == null || gameData.gameName() == null || gameData.gameName().isEmpty()) {
            throw new DataAccessException("must provide gameName and non null game");
        }
        games.put(gameData.gameID(), gameData);
    }
}
