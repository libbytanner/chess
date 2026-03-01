package server.service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
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

    public ListGamesResult listGames(ListGamesRequest request) {
        if (!verifyAuth(request.authToken())) {
            throw new UnauthorizedResponse("authToken is invalid");
        }
        return new ListGamesResult(gameDao.getListGames());
    }

    public void joinGame(JoinGameRequest request) throws DataAccessException {
        if (!verifyAuth(request.authToken())) {
            throw new UnauthorizedResponse("authToken is invalid");
        }
        String username = authDao.findAuth(request.authToken()).username();
        GameData game = gameDao.getGame(request.gameID());
        if (game == null) {
            throw new DataAccessException("Game does not exist");
        } else if (request.playerColor().equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("Color Taken");
            }
            gameDao.updateGame(game, ChessGame.TeamColor.WHITE, username);
        } else if (request.playerColor().equals("BLACK")) {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Color Taken");
            }
            gameDao.updateGame(game, ChessGame.TeamColor.BLACK, username);
        }
    }
}
