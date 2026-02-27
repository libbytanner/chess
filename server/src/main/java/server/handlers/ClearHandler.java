package server.handlers;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.UserMemoryDAO;
import io.javalin.http.Context;
import server.UserService;

public class ClearHandler extends BaseHandler {
    UserDAO userDao;
    AuthDAO authDao;
    UserService service;


    public ClearHandler(UserDAO userDao, AuthDAO authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
        this.service = new UserService(userDao, authDao);
    }

    public void handleRequest(Context context) {

    }
}
