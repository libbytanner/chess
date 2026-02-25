package server;

import com.google.gson.Gson;
import dataaccess.ExistingUserException;
import io.javalin.http.Context;
import model.RegisterRequest;
import model.RegisterResult;

public class RegisterHandler extends BaseHandler {
    UserService service = new UserService();

    public RegisterRequest fromJson(Context context) {
        var serializer = new Gson();
        return serializer.fromJson(context.body(), RegisterRequest.class);
    }

    public String toJson(RegisterResult result) {
        var serializer = new Gson();
        return serializer.toJson(result);
    }

    public void handleRequest(Context context) {
        RegisterRequest request = fromJson(context);
        try {
            var response = service.register(request);
            context.json(toJson(response));
        } catch (ExistingUserException exception) {
            context.status(403);
            ErrorResult result = new ErrorResult();
            context.result();
        }
    }
}
