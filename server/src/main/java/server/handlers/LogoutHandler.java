package server.handlers;

import dataaccess.AuthDAO;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import model.LogoutRequest;
import server.service.UserService;

public class LogoutHandler extends BaseHandler {
    UserService service;

    public LogoutHandler(AuthDAO authDao) {
        super(null, authDao, null);
        service = new UserService(null, authDao);
    }


    public void handleRequest(Context context) {
        LogoutRequest request = new LogoutRequest(context.header("authorization"));
        try {
            service.logout(request);
            context.status(200);
            context.result("{}");
        } catch (UnauthorizedResponse exception) {
            context.status(401);
            context.result("{ \"message\" : \"Error: unauthorized\" }");
        }
    }
}
