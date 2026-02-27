package server;

import dataaccess.AuthMemoryDAO;
import dataaccess.GameMemoryDAO;
import dataaccess.UserMemoryDAO;
import io.javalin.*;
import server.handlers.ClearHandler;
import server.handlers.CreateGameHandler;
import server.handlers.RegisterHandler;


public class Server {

    private final Javalin javalin;

    public Server() {
        UserMemoryDAO userDao = new UserMemoryDAO();
        AuthMemoryDAO authDao = new AuthMemoryDAO();
        GameMemoryDAO gameDao = new GameMemoryDAO();

        RegisterHandler registerHandler = new RegisterHandler(userDao, authDao);
        ClearHandler clearHandler = new ClearHandler(userDao, authDao, gameDao);
        CreateGameHandler createGameHandler = new CreateGameHandler(userDao, authDao, gameDao);
        javalin = Javalin.create(config -> config.staticFiles.add("web"))

        // Register your endpoints and exception handlers here.
            .post("/user", registerHandler::handleRequest)
            .post("/game", createGameHandler::handleRequest)
            .delete("/db", clearHandler::handleRequest);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
