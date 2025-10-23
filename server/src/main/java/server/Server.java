package server;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.*;
import io.javalin.http.Context;
import service.ChessService;

public class Server {

    private final Javalin javalin;
    private final ChessService service;

    public Server() {
        this.service = new ChessService(new MemoryUserDAO(), new MemoryAuthDAO(), new MemoryGameDAO());

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::clear);
        // Register your endpoints and exception handlers here.

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void clear(Context ctx) throws DataAccessException {
        service.clear();
    }
}
