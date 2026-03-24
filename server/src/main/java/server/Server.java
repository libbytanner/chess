package server;

import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import server.handlers.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        UserDAO userDao = new UserDatabaseDAO();
        AuthDAO authDao = new AuthDatabaseDAO();
        GameDAO gameDao = new GameDatabaseDAO();

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

            .error(400, this::badRequest)
            .error(401, this::unauthorized)
            .error(403, this::forbidden)
            .exception(Exception.class, this::exceptionHandler)
            .error(500, this::serverError)
            .delete("/db", clearHandler::handleRequest);
    }

    public void unauthorized(Context context) {
        context.result("{ \"message\" : \"Error: unauthorized\" }");
    }

    public void badRequest(Context context) {
        context.result("{ \"message\" : \"Error: bad request\" }");
    }


    public void forbidden(Context context) {
        context.result("{ \"message\" : \"Error: forbidden\" }");
    }

    public void serverError(Context context) {
        context.status(500);
        context.result("{ \"message\" : \"Error: server error\" }");
    }

    public void exceptionHandler(Exception e, Context context) {
        context.status(500);
        context.result("{ \"message\" : \"Error: server error\" }");

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
