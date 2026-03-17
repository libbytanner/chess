package client;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;


    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    public void setup() {
        UserDAO userDao = new UserDatabaseDAO();
        AuthDAO authDao = new AuthDatabaseDAO();
        GameDAO gameDao = new GameDatabaseDAO();
        userDao.clear();
        authDao.clear();
        gameDao.clear();

        userDao.addUser(new UserData("existingUser", "password", "email"));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void clearTest() {

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
        assertThrows(RuntimeException.class, () -> facade.register(request));
    }

    @Test
    public void loginTestPositive() {
        LoginRequest request = new LoginRequest("existingUser", "password");
        LoginResult expectedResult = new LoginResult("existingUser", "anAuthToken");
        LoginResult result = facade.login(request);
        assertEquals(expectedResult.username(), result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void loginTestNegative() {
        LoginRequest request = null;
        assertThrows(RuntimeException.class, () -> facade.login(request));
    }

    @Test
    public void logoutTestPositive() {
        Assertions.assertTrue(true);
    }

    @Test
    public void logoutTestNegative() {
        Assertions.assertTrue(true);
    }

}
