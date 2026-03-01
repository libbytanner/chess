package service;

import chess.ChessGame;
import dataaccess.*;
import io.javalin.http.UnauthorizedResponse;
import model.AuthData;
import model.CreateGameRequest;
import model.GameData;
import model.ListGamesRequest;
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
        assertThrows(UnauthorizedResponse.class, () -> service.createGame(request));
        assertEquals(0, gameDao.getListGames().size());
    }

    @Test
    @DisplayName("Game/ListGames - Positive")
    public void listGamesTestPositive() {
        AuthData authData = new AuthData("authToken", "username");
        authDao.addAuth(authData);
        ListGamesRequest request = new ListGamesRequest("authToken");
        GameData game1 =
                new GameData(0, null, null, "game1", new ChessGame());
        GameData game2 =
                new GameData(1, null, null, "game2", new ChessGame());
        gameDao.addGame(game1);
        gameDao.addGame(game2);
        ArrayList<GameData> gameList = new ArrayList<>();
        gameList.add(game1);
        gameList.add(game2);
        GameService service = new GameService(userDao, authDao, gameDao);
        assertDoesNotThrow(() -> service.listGames(request));
        assertEquals(gameList, gameDao.getListGames());
    }

    @Test
    @DisplayName("Game/ListGames - Negative")
    public void listGamesTestNegative() {
        AuthData authData = new AuthData("authToken", "username");
        authDao.addAuth(authData);
        ListGamesRequest request = new ListGamesRequest("differentAuth");
        GameData game1 =
                new GameData(0, null, null, "game1", new ChessGame());
        GameData game2 =
                new GameData(1, null, null, "game2", new ChessGame());
        gameDao.addGame(game1);
        gameDao.addGame(game2);
        GameService service = new GameService(userDao, authDao, gameDao);
        assertThrows(UnauthorizedResponse.class, () -> service.listGames(request));
    }
}
