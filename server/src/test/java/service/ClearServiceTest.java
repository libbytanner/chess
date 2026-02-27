package service;

import chess.ChessGame;
import dataaccess.AuthMemoryDAO;
import dataaccess.GameMemoryDAO;
import dataaccess.UserMemoryDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;
import server.service.ClearService;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClearServiceTest {
    @Test
    public void clearTest() {
        UserMemoryDAO userDao = new UserMemoryDAO();
        AuthMemoryDAO authDao = new AuthMemoryDAO();
        GameMemoryDAO gameDao = new GameMemoryDAO();

        UserData user1 = new UserData("user1", "pw", "email");
        UserData user2 = new UserData("user2", "pw", "email");
        AuthData auth1 = new AuthData("authCode", "user1");
        AuthData auth2 = new AuthData("authCode2", "user2");
        GameData game =
                new GameData(0, "user1", "user2", "name", new ChessGame());

        userDao.addUser(user1);
        userDao.addUser(user2);
        authDao.addAuth(auth1);
        authDao.addAuth(auth2);
        gameDao.addGame(game);

        ClearService service = new ClearService(userDao, authDao, gameDao);
        service.clear();

        assertEquals(0, userDao.getUsers().size());
        assertEquals(0, authDao.getAuthTokens().size());
        assertEquals(0, gameDao.getListGames().size());

    }
}
