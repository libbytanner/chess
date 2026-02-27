package service;

import dataaccess.AuthMemoryDAO;
import dataaccess.GameMemoryDAO;
import dataaccess.UserMemoryDAO;
import org.junit.jupiter.api.Test;
import server.UserService;

public class ClearServiceTest {
    @Test
    public void clearTest() {
        UserMemoryDAO userDao = new UserMemoryDAO();
        AuthMemoryDAO authDao = new AuthMemoryDAO();
        GameMemoryDAO gameDao = new GameMemoryDAO();
        UserService userService = new UserService(userDao, authDao);
    }
}
