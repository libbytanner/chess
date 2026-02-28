package server.service;

import dataaccess.*;
import io.javalin.http.UnauthorizedResponse;
import model.*;

public class UserService extends Service {

    public UserService(UserDAO userDao, AuthDAO authDao) {
        super(userDao, authDao, null);
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
        userDao.addUser(user);
        String token = authDao.generateToken();
        AuthData auth = new AuthData(token, request.username());
        authDao.addAuth(auth);
        return new RegisterResult(request.username(), token);
    }

    public LoginResult login(LoginRequest request) throws UnauthorizedResponse {
        UserData user = userDao.getUser(request.username());
        if (user == null || !user.password().equals(request.password())) {
            throw new UnauthorizedResponse();
        }
        String token = authDao.generateToken();
        AuthData auth = new AuthData(token, request.username());
        authDao.addAuth(auth);
        return new LoginResult(request.username(), token);
    }

    public void logout(LogoutRequest request) throws UnauthorizedResponse {
        if (!verifyAuth(request.authToken())) {
            throw new UnauthorizedResponse();
        }
        AuthData auth = authDao.findAuth(request.authToken());
        authDao.deleteAuth(auth);
    }
}
