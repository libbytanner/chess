package service;

import dataaccess.ExistingUserException;
import model.RegisterRequest;
import model.UserData;
import org.junit.jupiter.api.*;
import server.UserService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    UserService service;
    @BeforeEach
    public void setUp() {
        service = new UserService();
    }

    @Test
    public void registerGoodRequest() {
        RegisterRequest request = new RegisterRequest("username", "password", "email");
        try {
            service.register(request);
        } catch (ExistingUserException _) {}
        ArrayList<UserData> obsUsers = service.getUserDao().getUsers();
        assertEquals(1, obsUsers.size());
        assertEquals("username", obsUsers.getFirst().username());
    }

    @Test
    public void registerBadRequest() {
        RegisterRequest initialRequest = new RegisterRequest("username", "password", "email");
        RegisterRequest secondRequest =
                new RegisterRequest("username", "hello", "another@email");
        try {
            service.register(initialRequest);
        } catch (ExistingUserException _) {}
        assertThrows(ExistingUserException.class, () -> service.register(secondRequest));
        ArrayList<UserData> obsUsers = service.getUserDao().getUsers();
        assertEquals(1, obsUsers.size());

    }

}
