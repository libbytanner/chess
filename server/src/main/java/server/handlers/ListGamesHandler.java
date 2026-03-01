package server.handlers;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.http.Context;
import server.service.GameService;

public class ListGamesHandler extends BaseHandler {
    GameService service = new GameService(null, authDao, gameDao);
    public ListGamesHandler(AuthDAO authDao, GameDAO gameDao) {
        super(null, authDao, gameDao);
    }

    public void handleRequest(Context context) {

    }
}
