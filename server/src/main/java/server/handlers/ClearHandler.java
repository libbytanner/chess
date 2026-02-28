package server.handlers;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.http.Context;
import server.service.ClearService;

public class ClearHandler extends BaseHandler {
    ClearService clearService;


    public ClearHandler(UserDAO userDao, AuthDAO authDao, GameDAO gameDao) {
        super(userDao, authDao, gameDao);
        this.clearService = new ClearService(userDao, authDao, gameDao);
    }

    public void handleRequest(Context context) {
        clearService.clear();
    }
}
