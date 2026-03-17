package client;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;

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
    public void registerTestPositive() {
        RegisterRequest request = new RegisterRequest("libby", "me", "helloEmail");
        RegisterResult expectedResult = new RegisterResult("libby", "anAuthToken");
        RegisterResult result = facade.register(request);
        assertEquals(expectedResult.username(), result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void registerTestNegative() {
        RegisterRequest request = null;
        assertThrows(ResponseException.class, () -> facade.register(request));
    }

    @Test
    public void loginTestPositive() {
        userDao.addUser(new UserData("existingUser", "password", "email"));
        LoginRequest request = new LoginRequest("existingUser", "password");
        LoginResult expectedResult = new LoginResult("existingUser", "anAuthToken");
        LoginResult result = facade.login(request);
        assertEquals(expectedResult.username(), result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void loginTestNegative() {
        LoginRequest request = new LoginRequest("NonExistingUser", "password");
        assertThrows(ResponseException.class, () -> facade.login(request));
    }

    @Test
    public void logoutTestPositive() {
        userDao.addUser(new UserData("existingUser", "password", "email"));
        authDao.addAuth(new AuthData("authToken", "existingUser"));
        LogoutRequest request = new LogoutRequest("authToken");
        assertDoesNotThrow(() -> facade.logout(request));
        assertEquals(0, authDao.getAuthTokens().size());
    }

    @Test
    public void logoutTestNegative() {
        LogoutRequest request = new LogoutRequest(null);
        assertThrows(ResponseException.class, () -> facade.logout(request));
    }

}
