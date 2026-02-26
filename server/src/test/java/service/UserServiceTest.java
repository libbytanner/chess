package service;

import dataaccess.ExistingUserException;
import model.RegisterRequest;
import model.UserData;
import org.junit.jupiter.api.*;
import server.UserService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

}
