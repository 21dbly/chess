package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;
    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler() {
        try {
            gameDAO = new SQLGameDAO();
            authDAO = new SQLAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleConnect(WsConnectContext wsConnectContext) throws Exception {
        System.out.println("Websocket connected");
        wsConnectContext.enableAutomaticPings();
    }

    @Override
    public void handleClose(WsCloseContext wsCloseContext) throws Exception {
        System.out.println("Websocket closed");
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws Exception {
        System.out.println("Message received: "+ctx.message());
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            if (command.getCommandType().equals(UserGameCommand.CommandType.MAKE_MOVE)) {
                command = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
            }
            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getGameID(), command.getAuthToken(), ctx.session);
                case MAKE_MOVE -> makeMove((MakeMoveCommand)command, ctx.session);
                case LEAVE -> leave(command.getGameID(), command.getAuthToken(), ctx.session);
                case RESIGN -> resign(command.getGameID(), command.getAuthToken(), ctx.session);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void connect(int gameID, String authToken, Session session) throws IOException, DataAccessException {
        var game = gameDAO.getGame(gameID);
        if (game == null) {
            sendError(session, "That game does not exist");
            return;
        }
        var user = authDAO.getAuth(authToken);
        if (user == null) {
            sendError(session, "Unauthorized");
            return;
        }
        String username = user.username();

        // add session to connectionManager
        PlayerType playerType;
        if (username.equals(game.whiteUsername()) && !connections.hasWhite(gameID)) {
            playerType = PlayerType.WHITE;
        } else if (username.equals(game.blackUsername()) && !connections.hasBlack(gameID)) {
            playerType = PlayerType.BLACK;
        } else {
            playerType = PlayerType.OBSERVER;
        }
        connections.add(gameID, session, playerType);

        // send load game message to root
        String loadMessage = new LoadGameMessage(game.game().getBoard()).toString();
        session.getRemote().sendString(loadMessage);

        // broadcast to others
        String playerTypeStr = switch (playerType) {
            case WHITE -> "White";
            case BLACK -> "Black";
            case OBSERVER -> "an observer";
            default -> null;
        };
        var notifyMessage = new NotificationMessage("'%s' has joined the game as %s.".formatted(username, playerTypeStr));
        connections.broadcast(gameID, notifyMessage, session);
    }

    private void makeMove(MakeMoveCommand command, Session session) throws DataAccessException, IOException {
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();
        ChessMove move = command.getMove();

        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            sendError(session, "That game does not exist");
            return;
        }
        ChessGame game = gameData.game();

        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            sendError(session, "You are not logged in");
            return;
        }
        String username = authData.username();

        // check if game is over
        if (game.isOver()) {
            sendError(session, "The game is over");
            return;
        }

        // verify correct color
        var playerType = connections.findSessionType(gameID, session);
        if (playerType.equals(PlayerType.UNAUTHORIZED) || playerType.equals(PlayerType.OBSERVER)) {
            sendError(session, "You are not playing that game");
            return;
        }
        ChessGame.TeamColor teamColor;
        if (playerType.equals(PlayerType.WHITE)) {
            teamColor = ChessGame.TeamColor.WHITE;
        } else {
            teamColor = ChessGame.TeamColor.BLACK;
        }
        if (move.getMoveColor(game.getBoard()) != teamColor) {
            sendError(session, "That is not your piece");
            return;
        }

        // verify valid move and perform move
        try {
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            sendError(session, e.getMessage());
            return;
        }

        // update game in database
        gameDAO.updateGame(gameData);

        // broadcast messages
        var loadMessage = new LoadGameMessage(game.getBoard());
        connections.broadcast(gameID, loadMessage, null);

        String notifyString = "'%s' has made the move %s.".formatted(username, move.toString());
        var notifyMessage = new NotificationMessage(notifyString);
        connections.broadcast(gameID, notifyMessage, session);

        // notify of checks
        ChessGame.TeamColor opposite = teamColor.opposite();
        if (game.isInCheckmate(opposite)) {
            notifyString = "%s (%s) is in checkmate.".formatted(gameData.getUsername(opposite), opposite.name());
            connections.broadcast(gameID, new NotificationMessage(notifyString), null);
        }
        else if (game.isInCheck(opposite)) {
            notifyString = "%s (%s) is in check.".formatted(gameData.getUsername(opposite), opposite.name());
            connections.broadcast(gameID, new NotificationMessage(notifyString), null);
        }
        else if (game.isInStalemate(opposite)) {
            connections.broadcast(gameID, new NotificationMessage("Stalemate!"), null);
        }
    }

    public void leave(int gameID, String authToken, Session session) throws DataAccessException, IOException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            sendError(session, "You are not logged in");
            return;
        }
        String username = authData.username();

        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            sendError(session, "That game does not exist");
            return;
        }

        var playerType = connections.findSessionType(gameID, session);
        if (playerType == PlayerType.UNAUTHORIZED) {
            sendError(session, "You are not in that game");
            return;
        }
        connections.remove(gameID, session);
        if (playerType == PlayerType.WHITE) {
            gameDAO.updateGame(removedPlayer(gameData, ChessGame.TeamColor.WHITE));
        }
        else if (playerType == PlayerType.BLACK) {
            gameDAO.updateGame(removedPlayer(gameData, ChessGame.TeamColor.BLACK));
        }

        String notifyString = "'%s' has left the game.".formatted(username);
        var notifyMessage = new NotificationMessage(notifyString);
        connections.broadcast(gameID, notifyMessage, null);
    }

    private GameData removedPlayer(GameData gameData, ChessGame.TeamColor colorToRemove) {
        String whiteName = gameData.whiteUsername();
        String blackName = gameData.blackUsername();
        String gameName = gameData.gameName();
        int gameID = gameData.gameID();
        ChessGame game = gameData.game();
        if (ChessGame.TeamColor.WHITE.equals(colorToRemove)) {
            whiteName = null;
        }
        else if (ChessGame.TeamColor.BLACK.equals(colorToRemove)) {
            blackName = null;
        }
        return new GameData(gameID, whiteName, blackName, gameName, game);
    }

    public void resign(int gameID, String authToken, Session session) throws DataAccessException, IOException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) {
            sendError(session, "You are not logged in");
            return;
        }
        String username = authData.username();

        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            sendError(session, "That game does not exist");
            return;
        }

        var playerType = connections.findSessionType(gameID, session);
        if (playerType == PlayerType.UNAUTHORIZED || playerType == PlayerType.OBSERVER) {
            sendError(session, "You are not playing that game");
            return;
        }

        if (gameData.game().isOver()) {
            sendError(session, "Game is over");
            return;
        }

        ChessGame.TeamColor teamColor;
        if (playerType == PlayerType.WHITE) {
            teamColor = ChessGame.TeamColor.WHITE;
        } else {
            teamColor = ChessGame.TeamColor.BLACK;
        }
        gameData.game().resign(teamColor);
        gameDAO.updateGame(gameData);
        ChessGame.TeamColor winner = teamColor.opposite();

        String notifyString = "'%s' has resigned. %s wins!".formatted(username, winner.name());
        var notifyMessage = new NotificationMessage(notifyString);
        connections.broadcast(gameID, notifyMessage, null);
    }

    private void sendError(Session session, String errorMessage) throws IOException {
        session.getRemote().sendString((new ErrorMessage(errorMessage)).toString());
    }

    public void clearConnections() {
        connections.clear();
    }
}
