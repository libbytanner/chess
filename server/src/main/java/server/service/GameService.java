package server.service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.http.UnauthorizedResponse;
import model.*;

public class GameService extends Service{

    public GameService(UserDAO userDao, AuthDAO authDao, GameDAO gameDao) {
        super(userDao, authDao, gameDao);
    }

    public CreateGameResult createGame(CreateGameRequest request) throws UnauthorizedResponse{
        if (!verifyAuth(request.authToken())) {
            throw new UnauthorizedResponse("authToken is invalid");
        }
        int size = gameDao.getListGames().size();
        GameData game = new GameData(size + 1, null, null, request.gameName(),new ChessGame());
        gameDao.addGame(game);
        return new CreateGameResult(game.gameID());
    }

    public ListGamesResult listGames(ListGamesRequest request) throws UnauthorizedResponse {
        if (!verifyAuth(request.authToken())) {
            throw new UnauthorizedResponse("authToken is invalid");
        }
        return new ListGamesResult(gameDao.getListGames());
    }
}
