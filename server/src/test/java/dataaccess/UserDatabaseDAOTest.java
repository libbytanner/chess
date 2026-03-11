package dataaccess;

import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class UserDatabaseDAOTest {
    UserDAO dao = new UserDatabaseDAO();
    static Connection conn;

    @BeforeAll
    public static void createDatabaseDAO() throws DataAccessException {
        conn = DatabaseManager.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void tearDown() {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DROP table users")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getUsersPositiveTest() {
        UserData user1 = new UserData("1", "password", "email");
        UserData user2 = new UserData("2", "password", "email");
        UserData user3 = new UserData("3", "another one", "email");

        ArrayList<UserData> exp = new ArrayList<>();
        exp.add(user1);
        exp.add(user2);
        exp.add(user3);

        dao.addUser(user1);
        dao.addUser(user2);
        dao.addUser(user3);

        assertEquals(exp, dao.getUsers());

    }

    @Test
    public void getUsersNegativeTest() {

        ArrayList<UserData> exp = new ArrayList<>();
        assertEquals(exp, dao.getUsers());

    }

    @Test
    public void getUserPositiveTest() {
        UserData exp = new UserData("hello", "password", "email");
        dao.addUser(exp);
        UserData user = assertDoesNotThrow(() -> dao.getUser("hello"));
        assertEquals(exp, user);
    }

    @Test
    public void getUserNegativeTest() {
        assertNull(dao.getUser("not_a_username"));
    }

    @Test
    public void addUserPositiveTest() {
        UserData user = new UserData("me", "pw", "email");
        assertDoesNotThrow(() -> dao.addUser(user));
        assertEquals(user, dao.getUser(user.username()));
    }

    @Test
    public void clearTest() {
        UserData user1 = new UserData("1", "password", "email");
        UserData user2 = new UserData("2", "password", "email");
        UserData user3 = new UserData("3", "another one", "email");

        dao.addUser(user1);
        dao.addUser(user2);
        dao.addUser(user3);

        ArrayList<UserData> exp = new ArrayList<>();
        dao.clear();
        assertEquals(exp, dao.getUsers());


    }
}
