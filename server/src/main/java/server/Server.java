package server;

import dataaccess.AuthMemoryDAO;
import dataaccess.GameMemoryDAO;
import dataaccess.UserMemoryDAO;
import io.javalin.*;
import io.javalin.http.Context;
import server.handlers.*;


public class Server {

    private final Javalin javalin;

    public Server() {
        UserMemoryDAO userDao = new UserMemoryDAO();
        AuthMemoryDAO authDao = new AuthMemoryDAO();
        GameMemoryDAO gameDao = new GameMemoryDAO();

        RegisterHandler registerHandler = new RegisterHandler(userDao, authDao);
        ClearHandler clearHandler = new ClearHandler(userDao, authDao, gameDao);
        CreateGameHandler createGameHandler = new CreateGameHandler(userDao, authDao, gameDao);
        LoginHandler loginHandler = new LoginHandler(userDao, authDao);
        LogoutHandler logoutHandler = new LogoutHandler(authDao);
        ListGamesHandler listGamesHandler = new ListGamesHandler(authDao, gameDao);
        JoinGameHandler joinGameHandler = new JoinGameHandler(userDao, authDao, gameDao);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))

        // Register your endpoints and exception handlers here.
            .post("/user", registerHandler::handleRequest)
            .post("/game", createGameHandler::handleRequest)
            .post("/session", loginHandler::handleRequest)
            .delete("/session", logoutHandler::handleRequest)
            .get("/game", listGamesHandler::handleRequest)
            .put("/game", joinGameHandler::handleRequest)
            .error(400, this::dataAccess)
            .error(401, this::unauthorized)
            .delete("/db", clearHandler::handleRequest);
    }

    public void unauthorized(Context context) {
        context.result("{ \"message\" : \"Error: unauthorized\" }");
    }

    public void dataAccess(Context context) {
        context.result("{ \"message\" : \"Error: bad request\" }");
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
