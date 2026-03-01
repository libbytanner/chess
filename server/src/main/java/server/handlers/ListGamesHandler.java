package server.handlers;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import model.ListGamesRequest;
import server.service.GameService;

public class ListGamesHandler extends BaseHandler {
    GameService service = new GameService(null, authDao, gameDao);
    public ListGamesHandler(AuthDAO authDao, GameDAO gameDao) {
        super(null, authDao, gameDao);
    }

    public void handleRequest(Context context) {
        ListGamesRequest request = new ListGamesRequest(context.header("authorization"));
        try {
            var response = service.listGames(request);
            context.json(toJson(response));
        } catch (UnauthorizedResponse exception) {
            context.status(401);
        }
    }
}
