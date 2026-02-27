package server.handlers;

import dataaccess.*;
import io.javalin.http.Context;
import model.CreateGameRequest;
import model.CreateGameResult;
import server.service.GameService;

public class CreateGameHandler extends BaseHandler {
    GameService service;

    public CreateGameHandler(UserDAO userDao, AuthDAO authDao, GameDAO gameDao) {
        super();
        service = new GameService(userDao, authDao, gameDao);
    }

    public CreateGameResult handleRequest(Context context) {

        CreateGameRequest request = (CreateGameRequest) fromJson(context, CreateGameRequest.class);
        var response = service.createGame(request);
        context.json(toJson(response));
        return null;
    }
}
