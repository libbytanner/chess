package server.service;

import dataaccess.*;
import model.AuthData;
import model.RegisterRequest;
import model.RegisterResult;
import model.UserData;

public class UserService {
    UserDAO userDao;
    AuthDAO authDao;

    public UserService(UserDAO userDao, AuthDAO authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public UserDAO getUserDao() {
        return userDao;
    }

    public AuthDAO getAuthDao() {
        return authDao;
    }

    public RegisterResult register(RegisterRequest request) throws ExistingUserException {
        if (userDao.getUser(request.username()) != null) {
            throw new ExistingUserException("User already exists");
        }
        UserData user = new UserData(request.username(), request.password(), request.email());
        userDao.createUser(user);
        String token = authDao.generateToken();
        AuthData auth = new AuthData(token, request.username());
        authDao.createAuth(auth);
        return new RegisterResult(request.username(), token);
    }
}
