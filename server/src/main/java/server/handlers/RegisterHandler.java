package server.handlers;

import dataaccess.AuthDAO;
import dataaccess.ExistingUserException;
import dataaccess.UserDAO;
import io.javalin.http.Context;
import model.RegisterRequest;
import server.service.UserService;

public class RegisterHandler extends BaseHandler {
    UserService service;


    public RegisterHandler(UserDAO userDao, AuthDAO authDao) {
        super(userDao, authDao, null);
        this.service = new UserService(userDao, authDao);
    }

    public void handleRequest(Context context) {
        RegisterRequest request = (RegisterRequest) fromJson(context, RegisterRequest.class);
        if (request.username() == null || request.password() == null || request.email() == null) {
            context.status(400);
            context.result("{\"message\" : \"Error: bad request\" }");
        } else {
            try {
                var response = service.register(request);
                context.json(toJson(response));
            } catch (ExistingUserException exception) {
                context.status(403);
                context.result("{\"message\" : \"Error: username already taken\" }");
            }
        }
    }
}
