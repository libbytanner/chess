package server.service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UnauthorizedException;
import dataaccess.UserDAO;
import model.CreateGameRequest;
import model.CreateGameResult;

public class GameService extends Service{

    public GameService(UserDAO userDao, AuthDAO authDao, GameDAO gameDao) {
        super(userDao, authDao, gameDao);
    }

    public CreateGameResult createGame(CreateGameRequest request) {
        if (!verifyAuth(request.authToken())) {
            throw new UnauthorizedException("authToken is invalid");
        }
        gameDao.createGame(request.gameName());
        return null;
    }
}
