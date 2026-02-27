package service;

import dataaccess.*;
import model.RegisterRequest;
import model.UserData;
import org.junit.jupiter.api.*;
import server.service.UserService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    UserService service;
    AuthDAO authDao;
    UserDAO userDao;

    @BeforeEach
    public void setUp() {
        authDao = new AuthMemoryDAO();
        userDao = new UserMemoryDAO();
        service = new UserService(userDao, authDao);
    }

    @Test
    public void registerGoodRequest() {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        assertDoesNotThrow(() -> service.register(request));
        ArrayList<UserData> obsUsers = service.getUserDao().getUsers();
        assertEquals(1, obsUsers.size());
        assertEquals("username", obsUsers.getFirst().username());
        assertEquals(1, service.getAuthDao().getAuthTokens().size());
    }

    @Test
    public void registerBadRequest() {
        RegisterRequest initialRequest = new RegisterRequest("username", "password", "email");
        RegisterRequest secondRequest =
                new RegisterRequest("username", "hello", "another@email");
        try {
            service.register(initialRequest);
        } catch (ExistingUserException e) {
            throw new RuntimeException(e);
        }
        assertThrows(ExistingUserException.class, () -> service.register(secondRequest));
        ArrayList<UserData> obsUsers = service.getUserDao().getUsers();
        assertEquals(1, obsUsers.size());
        assertEquals(1, service.getAuthDao().getAuthTokens().size());

    }

}
