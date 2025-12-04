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
        if (game.whiteUsername().equals(username) && !connections.hasWhite(gameID)) {
            playerType = PlayerType.WHITE;
        } else if (game.blackUsername().equals(username) && !connections.hasBlack(gameID)) {
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
        if (!move.getMoveColor(game.getBoard()).equals(teamColor)) {
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
            connections.broadcast(gameID, new NotificationMessage("%s is in checkmate.".formatted(opposite.name())), null);
        }
        else if (game.isInCheck(opposite)) {
            connections.broadcast(gameID, new NotificationMessage("%s is in check.".formatted(opposite.name())), null);
        }
        else if (game.isInStalemate(opposite)) {
            connections.broadcast(gameID, new NotificationMessage("%s is in stalemate.".formatted(opposite.name())), null);
        }
    }

    private void sendError(Session session, String errorMessage) throws IOException {
        session.getRemote().sendString((new ErrorMessage(errorMessage)).toString());
    }

    private PlayerType getPlayerType(Session session, int gameID, String authToken) throws DataAccessException {
        var authData = authDAO.getAuth(authToken);
        if (authData == null) {
            return PlayerType.UNAUTHORIZED;
        }
        String username = authData.username();
        return connections.findSessionType(gameID, session);
    }
}
