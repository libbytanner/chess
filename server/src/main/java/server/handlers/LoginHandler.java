package server.handlers;

import dataaccess.*;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import model.LoginRequest;
import server.service.UserService;

public class LoginHandler extends BaseHandler {
    UserService service;

    public LoginHandler(UserDAO userDao, AuthDAO authDao) {
        super(userDao, authDao, null);
        this.service = new UserService(userDao, authDao);
    }

    public void handleRequest(Context context) {
        LoginRequest request = (LoginRequest) fromJson(context, LoginRequest.class);
        if (request.username() == null || request.password() == null) {
            context.status(400);
            context.result("{ \"message\" : \"Error: bad request\" }");
        } else {
            try {
                var response = service.login(request);
                context.json(toJson(response));
            } catch (UnauthorizedResponse exception) {
                context.status(401);
                context.result("{ \"message\" : \"Error: unauthorized\" }");
            }
        }

    }
}
