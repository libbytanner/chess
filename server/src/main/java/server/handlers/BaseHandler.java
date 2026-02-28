package server.handlers;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.http.Context;
import model.Request;
import model.Result;

import java.lang.reflect.Type;

public class BaseHandler {
    UserDAO userDao;
    AuthDAO authDao;
    GameDAO gameDao;

    public BaseHandler(UserDAO userDao, AuthDAO authDao, GameDAO gameDao) {
        this.userDao = userDao;
        this.authDao = authDao;
        this.gameDao = gameDao;
    }

    public Request fromJson(Context context, Type requestType) {
        var serializer = new Gson();
        return serializer.fromJson(context.body(), requestType);
    }

    public String toJson(Result result) {
        var serializer = new Gson();
        return serializer.toJson(result);
    }

}
