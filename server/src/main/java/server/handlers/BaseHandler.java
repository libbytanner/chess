package server.handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import model.Request;
import model.Result;

import java.lang.reflect.Type;

public class BaseHandler {
    public Request fromJson(Context context, Type requestType) {
        var serializer = new Gson();
        return serializer.fromJson(context.body(), requestType);
    }

    public String toJson(Result result) {
        var serializer = new Gson();
        return serializer.toJson(result);
    }

}
