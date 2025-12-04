package websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<Integer, GameConnections> connections = new ConcurrentHashMap<>();

    public boolean add(int gameID, Session session, PlayerType playerType) {
        connections.putIfAbsent(gameID, new GameConnections());
        var gameConnections = connections.get(gameID);
        if (playerType.equals(PlayerType.WHITE)) {
            if (gameConnections.white != null) {
                return false;
            }
            gameConnections.white = session;
            return true;
        }
        if (playerType.equals(PlayerType.BLACK)) {
            if (gameConnections.black != null) {
                return false;
            }
            gameConnections.black = session;
            return true;
        }
        if (playerType.equals(PlayerType.OBSERVER)) {
            gameConnections.observers.add(session);
            return true;
        }
        return false;
    }

    public boolean hasWhite(int gameID) {
        var gameConnections = connections.get(gameID);
        if (gameConnections == null) {
            return false;
        }
        return gameConnections.white != null;
    }

    public boolean hasBlack(int gameID) {
        var gameConnections = connections.get(gameID);
        if (gameConnections == null) {
            return false;
        }
        return gameConnections.black != null;
    }

    public void broadcast(int gameID, ServerMessage message, Session exclude) throws IOException {
        var gameConnections = connections.get(gameID);
        if (gameConnections == null) {
            return;
        }
        String strMessage = message.toString();
        if (gameConnections.white != null) {
            sendIfNotExclude(gameConnections.white, strMessage, exclude);
        }
        if (gameConnections.black != null) {
            sendIfNotExclude(gameConnections.black, strMessage, exclude);
        }
        for (Session session : gameConnections.observers) {
            sendIfNotExclude(session, strMessage, exclude);
        }
    }

    private void sendIfNotExclude(Session session, String message, Session exclude) throws IOException {
        if (session.equals(exclude)) {
            return;
        }
        if (!session.isOpen()) {
            return;
        }
        session.getRemote().sendString(message);
    }

    public PlayerType findSessionType(int gameID, Session session) {
        var gameConnection = connections.get(gameID);
        if (session.equals(gameConnection.white)) {
            return PlayerType.WHITE;
        }
        if (session.equals(gameConnection.black)) {
            return PlayerType.BLACK;
        }
        if (gameConnection.observers.contains(session)) {
            return PlayerType.OBSERVER;
        }
        return PlayerType.UNAUTHORIZED;
    }

    private static class GameConnections {
        public Session white;
        public Session black;
        public Collection<Session> observers;

        public GameConnections() {
            white = null;
            black = null;
            observers = new ArrayList<Session>();
        }
    }

    public void clear() {
        connections.clear();
    }
}
