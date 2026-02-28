package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.CreateGameRequest;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.service.GameService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    UserDAO userDao;
    AuthDAO authDao;
    GameDAO gameDao;

    @BeforeEach
    public void setup() {
        userDao = new UserMemoryDAO();
        authDao = new AuthMemoryDAO();
        gameDao = new GameMemoryDAO();
    }

    @Test
    @DisplayName("Game/CreateGame - Positive")
    public void createGameTestPositive() {
        AuthData authData = new AuthData("authToken", "username");
        authDao.addAuth(authData);
        CreateGameRequest request = new CreateGameRequest("authToken", "gameName");
        GameData expectedGame =
                new GameData(0, null, null, "gameName", new ChessGame());
        ArrayList<GameData> gameList = new ArrayList<>();
        gameList.add(expectedGame);
        GameService service = new GameService(userDao, authDao, gameDao);
        assertDoesNotThrow(() -> service.createGame(request));
        assertEquals(gameList, gameDao.getListGames());
    }

    @Test
    @DisplayName("Game/CreateGame - Negative")
    public void createGameTestNegative() {
        AuthData authData = new AuthData("differentAuth", "username");
        authDao.addAuth(authData);
        CreateGameRequest request = new CreateGameRequest("authToken", "gameName");
        GameService service = new GameService(userDao, authDao, gameDao);
        assertThrows(UnauthorizedException.class, () -> service.createGame(request));
        assertEquals(0, gameDao.getListGames().size());
    }
}
