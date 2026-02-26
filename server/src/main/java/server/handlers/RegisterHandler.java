package server.handlers;

import dataaccess.ExistingUserException;
import io.javalin.http.Context;
import model.RegisterRequest;
import server.UserService;

public class RegisterHandler extends BaseHandler {
    UserService service = new UserService();

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
