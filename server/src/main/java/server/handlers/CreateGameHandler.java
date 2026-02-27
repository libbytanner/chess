package server.handlers;

import dataaccess.*;
import io.javalin.http.Context;
import model.CreateGameRequest;
import server.service.GameService;

public class CreateGameHandler extends BaseHandler {
    GameService service;
    AuthDAO authDao;
    GameDAO gameDao;
    UserDAO userDao;
    public CreateGameHandler(UserDAO userDao, AuthDAO authDao, GameDAO gameDao) {
        this.userDao = userDao;
        this.authDao = authDao;
        this.gameDao = gameDao;
        service = new GameService(userDao, authDao, gameDao);
    }

    public void handleRequest(Context context) {
        CreateGameRequest request = (CreateGameRequest) fromJson(context, CreateGameRequest.class);
        request = new CreateGameRequest(context.header("authorization"), request.gameName());
        try {
            var response = service.createGame(request);
            context.json(toJson(response));
        } catch (UnauthorizedException exception) {
            context.status(401);
            context.result("{ \"message: \" : \"Error: unauthorized\" }");
        }
    }
}
