package server.handlers;

import dataaccess.AuthDAO;
import dataaccess.ExistingUserException;
import dataaccess.UserDAO;
import io.javalin.http.Context;
import model.RegisterRequest;
import server.service.UserService;

public class RegisterHandler extends BaseHandler {
    UserDAO userDao;
    AuthDAO authDao;
    UserService service;


    public RegisterHandler(UserDAO userDao, AuthDAO authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
        this.service = new UserService(userDao, authDao);
    }

    public void handleRequest(Context context) {
        RegisterRequest request = (RegisterRequest) fromJson(context, RegisterRequest.class);
        try {
            var response = service.register(request);
            context.json(toJson(response));
        } catch (ExistingUserException exception) {
            context.status(403);
            context.result("{\"message\" : \"Error: username already taken\" }");
        }
    }
}
