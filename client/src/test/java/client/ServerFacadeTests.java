package client;

import org.junit.jupiter.api.*;
import server.Server;


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

        Assertions.assertTrue(true);
    }

    @Test
    public void registerTestNegative() {
        Assertions.assertTrue(true);
    }

    @Test
    public void loginTestPositive() {
        Assertions.assertTrue(true);
    }

    @Test
    public void loginTestNegative() {
        Assertions.assertTrue(true);
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
