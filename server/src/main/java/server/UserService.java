package server;

import dataaccess.AuthMemoryDAO;
import dataaccess.DataAccessException;
import dataaccess.ExistingUserException;
import dataaccess.UserMemoryDAO;
import model.AuthData;
import model.RegisterRequest;
import model.RegisterResult;
import model.UserData;

public class UserService {
    UserMemoryDAO userDao = new UserMemoryDAO();
    AuthMemoryDAO authDao = new AuthMemoryDAO();

    public UserMemoryDAO getUserDao() {
        return userDao;
    }

    public AuthMemoryDAO getAuthDao() {
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
