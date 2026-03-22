package dataaccess;

import model.model.AuthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AuthDatabaseDAOTest {

    AuthDAO dao = new AuthDatabaseDAO();

    @AfterEach
    public void tearDown() {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DROP table authTokens")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void getAuthTokensPositiveTest() {
        AuthData auth1 = new AuthData("authToken1", "1");
        AuthData auth2 = new AuthData("authToken2", "2");
        AuthData auth3 = new AuthData("authToken3", "3");

        ArrayList<AuthData> exp = new ArrayList<>();
        exp.add(auth1);
        exp.add(auth2);
        exp.add(auth3);

        dao.addAuth(auth1);
        dao.addAuth(auth2);
        dao.addAuth(auth3);

        assertEquals(exp, dao.getAuthTokens());
    }

    @Test
    void getAuthTokensEmptyTest() {
        ArrayList<AuthData> exp = new ArrayList<>();
        assertEquals(exp, dao.getAuthTokens());
    }

    @Test
    void addAuthPositiveTest() {
        AuthData auth = new AuthData("12345", "username");
        assertDoesNotThrow(() -> dao.addAuth(auth));
        assertEquals(auth, dao.findAuth(auth.authToken()));
    }

    @Test
    void findAuthPositiveTest() {
        AuthData exp = new AuthData("12345", "username");
        dao.addAuth(exp);
        assertEquals(exp, dao.findAuth(exp.authToken()));
    }

    @Test
    void findAuthNegativeTest() {
        AuthData exp = new AuthData("12345", "username");
        dao.addAuth(exp);
        assertNull(dao.findAuth("anotherToken"));
    }

    @Test
    void deleteAuthPositiveTest() {
        AuthData auth = new AuthData("12345", "username");
        dao.addAuth(auth);
        assertEquals(auth, dao.findAuth(auth.authToken()));
        dao.deleteAuth(auth);
        assertNull(dao.findAuth(auth.authToken()));
    }

    @Test
    void clear() {
        AuthData auth1 = new AuthData("123", "name");
        AuthData auth2 = new AuthData("222", "username");
        AuthData auth3 = new AuthData("322222", "another one");

        dao.addAuth(auth1);
        dao.addAuth(auth2);
        dao.addAuth(auth3);

        ArrayList<AuthData> exp = new ArrayList<>();
        dao.clear();
        assertEquals(exp, dao.getAuthTokens());
    }
}