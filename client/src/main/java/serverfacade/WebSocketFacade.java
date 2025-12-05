package serverfacade;

import com.google.gson.Gson;
import exceptions.ResponseException;
import jakarta.websocket.*;
import ui.ServerMessageObserver;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    ServerMessageObserver serverMessageObserver;

    public WebSocketFacade(String url, ServerMessageObserver serverMessageObserver) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.serverMessageObserver = serverMessageObserver;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String messageStr) {
                    ServerMessage message = parseServerMessage(messageStr);
                    serverMessageObserver.notify(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(int gameID, String authToken) {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new RuntimeException("Unable to parse command: %s".formatted(ex.getMessage()));
        }
    }

    private ServerMessage parseServerMessage(String s) {
        // I don't like this solution but don't have time for something better
        var gson = new Gson();
        var message = gson.fromJson(s, ServerMessage.class);
        return switch (message.getServerMessageType()) {
            case NOTIFICATION -> gson.fromJson(s, NotificationMessage.class);
            case ERROR -> gson.fromJson(s, ErrorMessage.class);
            case LOAD_GAME -> gson.fromJson(s, LoadGameMessage.class);
        };
    }
}
