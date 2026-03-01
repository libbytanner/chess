package server.handlers;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import model.JoinGameRequest;
import server.service.GameService;

public class JoinGameHandler extends BaseHandler {
    GameService service;

    public JoinGameHandler(UserDAO userDao, AuthDAO authDao, GameDAO gameDao) {
        super(userDao, authDao, gameDao);
        service = new GameService(userDao, authDao, gameDao);
    }

    public void handleRequest(Context context) {
        JoinGameRequest request = (JoinGameRequest) fromJson(context, JoinGameRequest.class);
        request = new JoinGameRequest(context.header("authorization"), request.playerColor(), request.gameID());
        if (request.playerColor() == null) {
            context.status(400);
        } else {
        try {
            service.joinGame(request);
        } catch (UnauthorizedResponse exception) {
            context.status(401);
        } catch (DataAccessException exception) {
            context.status(400);
        }
        }
    }
}
