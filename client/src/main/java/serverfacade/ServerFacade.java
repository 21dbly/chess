package serverfacade;

import model.*;

import java.net.http.HttpClient;
import java.util.Collection;

public class ServerFacade {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void clear() {
        throw new RuntimeException("Not implemented");
    }

    public AuthData register(UserData userData) {
        throw new RuntimeException("Not implemented");
    }

    public AuthData login(String username, String password) {
        throw new RuntimeException("Not implemented");
    }

    public void logout(String authToken) {
        throw new RuntimeException("Not implemented");
    }

    public int createGame(String authToken, String gameName) {
        throw new RuntimeException("Not implemented");
    }

    public Collection<GameData> listGames(String authToken) {
        throw new RuntimeException("Not implemented");
    }

    public void joinGame(String authToken, String playerColor, int gameID) {
        throw new RuntimeException("Not implemented");
    }

}
