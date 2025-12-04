package websocket;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

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
            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getGameID(), command.getAuthToken(), ctx.session);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void connect(int gameID, String authToken, Session session) throws IOException, DataAccessException {
        var game = gameDAO.getGame(gameID);
        if (game == null) {
            return;
        }
        var user = authDAO.getAuth(authToken).username();

        // add session to connectionManager
        PlayerType playerType;
        if (game.whiteUsername().equals(user) && !connections.hasWhite(gameID)) {
            playerType = PlayerType.WHITE;
        } else if (game.blackUsername().equals(user) && !connections.hasBlack(gameID)) {
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
        };
        var notifyMessage = new NotificationMessage("'%s' has joined the game as %s.".formatted(user, playerTypeStr));
        connections.broadcast(gameID, notifyMessage, session);
    }
}
