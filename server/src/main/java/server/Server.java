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
import service.ChessService;
import service.RegistrationException;

public class Server {

    private final Javalin javalin;
    private final ChessService service;

    public Server() {
        this.service = new ChessService(new MemoryUserDAO(), new MemoryAuthDAO(), new MemoryGameDAO());

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::clear)
                .post("/user", this::register)
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

    private void register(Context ctx) throws DataAccessException, RegistrationException {
        UserData userData = new Gson().fromJson(ctx.body(), UserData.class);
        AuthData authData = service.register(userData);
        ctx.json(new Gson().toJson(authData));
    }
}
