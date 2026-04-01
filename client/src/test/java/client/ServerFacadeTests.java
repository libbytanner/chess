package client;

import chess.ChessGame;
import dataaccess.*;
import model.ResponseException;
import model.model.*;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    UserDAO userDao = new UserDatabaseDAO();
    AuthDAO authDao = new AuthDatabaseDAO();
    GameDAO gameDao = new GameDatabaseDAO();



    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    public void setup() {
        userDao.clear();
        authDao.clear();
        gameDao.clear();
    }

    @AfterEach
    public void teardown() {
        userDao.clear();
        authDao.clear();
        gameDao.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void clearTest() {
        userDao.addUser(new UserData("existingUser", "password", "email"));
        userDao.addUser(new UserData("another", "password", "email"));
        userDao.addUser(new UserData("2", "password", "email"));
        facade.clear();
        assertDoesNotThrow(() -> userDao.addUser(new UserData("existingUser", "password", "email")));
    }

    @Test
    public void registerPositiveTest() {
        RegisterRequest request = new RegisterRequest("libby", "me", "helloEmail");
        RegisterResult expectedResult = new RegisterResult("libby", "anAuthToken");
        RegisterResult result = facade.register(request);
        assertEquals(expectedResult.username(), result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void registerNegativeTest() {
        RegisterRequest request = null;
        assertThrows(ResponseException.class, () -> facade.register(request));
    }

    @Test
    public void loginPositiveTest() {
        userDao.addUser(new UserData("existingUser", "password", "email"));
        LoginRequest request = new LoginRequest("existingUser", "password");
        LoginResult expectedResult = new LoginResult("existingUser", "anAuthToken");
        LoginResult result = facade.login(request);
        assertEquals(expectedResult.username(), result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void loginNegativeTest() {
        LoginRequest request = new LoginRequest("NonExistingUser", "password");
        assertThrows(ResponseException.class, () -> facade.login(request));
    }

    @Test
    public void logoutPositiveTest() {
        userDao.addUser(new UserData("existingUser", "password", "email"));
        authDao.addAuth(new AuthData("authToken", "existingUser"));
        LogoutRequest request = new LogoutRequest("authToken");
        assertDoesNotThrow(() -> facade.logout(request));
        assertEquals(0, authDao.getAuthTokens().size());
    }

    @Test
    public void logoutNegativeTest() {
        LogoutRequest request = new LogoutRequest(null);
        assertThrows(ResponseException.class, () -> facade.logout(request));
    }

    @Test
    public void listGamesPositiveTest() {
        userDao.addUser(new UserData("existingUser", "password", "email"));
        authDao.addAuth(new AuthData("authToken", "existingUser"));
        ListGamesRequest request = new ListGamesRequest("authToken");
        GameData game = new GameData(1, "white", "black", "existingGame",
                new ChessGame());
        gameDao.addGame(game);
        List<GameData> expectedList = new ArrayList<>();
        expectedList.add(game);

        ListGamesResult expectedResult = new ListGamesResult(expectedList);

        assertEquals(expectedResult, facade.listGames(request));
    }

    @Test
    public void listGamesNegativeTest() {
        userDao.addUser(new UserData("existingUser", "password", "email"));
        ListGamesRequest request = new ListGamesRequest(null);

        assertThrows(ResponseException.class, () -> facade.listGames(request));
    }

    @Test
    public void joinGamePositiveTest() {
        userDao.addUser(new UserData("existingUser", "password", "email"));
        authDao.addAuth(new AuthData("authToken", "existingUser"));
        JoinGameRequest request = new JoinGameRequest("authToken", ChessGame.TeamColor.BLACK, 1);
        GameData game = new GameData(1, "white", null, "existingGame",
                new ChessGame());
        gameDao.addGame(game);
        assertDoesNotThrow(() -> facade.joinGame(request));
    }

    @Test
    public void joinGameNegativeTest() {
        userDao.addUser(new UserData("existingUser", "password", "email"));
        authDao.addAuth(new AuthData("authToken", "existingUser"));
        JoinGameRequest request = new JoinGameRequest("authToken", ChessGame.TeamColor.BLACK, 1);
        GameData game = new GameData(1, "white", "black", "existingGame",
                new ChessGame());
        gameDao.addGame(game);
        assertThrows(ResponseException.class, () -> facade.joinGame(request));
    }

    @Test
    public void createGamePositiveTest() {
        userDao.addUser(new UserData("existingUser", "password", "email"));
        authDao.addAuth(new AuthData("authToken", "existingUser"));
        CreateGameRequest request = new CreateGameRequest("authToken", "new game");
        GameData game = new GameData(1, "white", "black", "existingGame",
                new ChessGame());
        gameDao.addGame(game);
        CreateGameResult expectedResult = new CreateGameResult(2);
        assertEquals(expectedResult, facade.createGame(request));
    }

    @Test
    public void createGameNegativeTest() {
        userDao.addUser(new UserData("existingUser", "password", "email"));
        CreateGameRequest request = new CreateGameRequest("authToken", null);
        assertThrows(ResponseException.class, () -> facade.createGame(request));
    }

}
