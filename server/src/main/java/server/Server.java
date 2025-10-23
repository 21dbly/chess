package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import exceptions.ResponseException;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import service.*;

public class Server {

    private final Javalin javalin;
    private final ChessService service;

    public Server() {
        this.service = new ChessService(new MemoryUserDAO(), new MemoryAuthDAO(), new MemoryGameDAO());

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::clear)
                .post("/user", this::register)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .post("/game", this::createGame)
                .get("/game", this::listGames)
                .put("/game", this::joinGame)
                .exception(ResponseException.class, this::exceptionHandler);
        // Register your endpoints and exception handlers here.

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void exceptionHandler(ResponseException ex, Context ctx) {
        ctx.status(ex.code());
        ctx.json(ex.toJson());
    }

    private void clear(Context ctx) throws DataAccessException {
        service.clear();
    }

    private void register(Context ctx) throws DataAccessException, RegistrationException, BadRequestException {
        UserData userData = new Gson().fromJson(ctx.body(), UserData.class);
        AuthData authData = service.register(userData);
        ctx.json(new Gson().toJson(authData));
    }

    private void login(Context ctx) throws DataAccessException, BadRequestException, UnauthorizedException {
        LoginRequest loginRequest = new Gson().fromJson(ctx.body(), LoginRequest.class);
        AuthData authData = service.login(loginRequest);
        ctx.json(new Gson().toJson(authData));
    }

    private void logout(Context ctx) throws UnauthorizedException, DataAccessException {
        service.logout(ctx.header("authorization"));
    }

    private void createGame(Context ctx) throws BadRequestException, DataAccessException, UnauthorizedException {
        service.authorize(ctx.header("authorization"));
        CreateGameRequest req = new Gson().fromJson(ctx.body(), CreateGameRequest.class);
        int gameID = service.createGame(req.gameName());
        ctx.json(new Gson().toJson(new CreateGameResponse(gameID)));
    }

    private void listGames(Context ctx) throws UnauthorizedException, DataAccessException {
        service.authorize(ctx.header("authorization"));
        var games = service.listGames();
        ctx.json(new Gson().toJson(new ListGamesResponse(games)));
    }

    private void joinGame(Context ctx) throws UnauthorizedException, DataAccessException, JoinException, BadRequestException {
        String username = service.authorize(ctx.header("authorization"));
        JoinGameRequest req = new Gson().fromJson(ctx.body(), JoinGameRequest.class);
        service.joinGame(req, username);
    }
}
