package server;

import io.javalin.*;
import server.handlers.RegisterHandler;


public class Server {

    private final Javalin javalin;

    public Server() {
        RegisterHandler registerHandler = new RegisterHandler();
        javalin = Javalin.create(config -> config.staticFiles.add("web"))

        // Register your endpoints and exception handlers here.
            .post("/user", registerHandler::handleRequest);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
