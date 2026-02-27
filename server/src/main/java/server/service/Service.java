package server.service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class Service {
    UserDAO userDao;
    AuthDAO authDao;
    GameDAO gameDao;
    public Service(UserDAO userDao, AuthDAO authDao, GameDAO gameDao) {
        this.userDao = userDao;
        this.authDao = authDao;
        this.gameDao = gameDao;
    }

    public boolean verifyAuth(String authToken) {
        return authDao.getAuth(authToken) != null;
    }
}
