package server.service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ClearService {
    UserDAO userDao;
    AuthDAO authDao;
    GameDAO gameDao;


    public ClearService(UserDAO userDao, AuthDAO authDao, GameDAO gameDao) {
        this.userDao = userDao;
        this.authDao = authDao;
        this.gameDao = gameDao;
    }

    public void clear() {
        userDao.clear();
        authDao.clear();
        gameDao.clear();
    }
}
