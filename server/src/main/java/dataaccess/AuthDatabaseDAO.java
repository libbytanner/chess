package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class AuthDatabaseDAO implements AuthDAO {

    public AuthDatabaseDAO() {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public ArrayList<AuthData> getAuthTokens() {
        ArrayList<AuthData> authTokens = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM authTokens")) {
                var rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    AuthData auth = new AuthData(
                            rs.getString("authToken"),
                            rs.getString("username"));
                    authTokens.add(auth);
                }
                return authTokens;
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }    }

    @Override
    public void addAuth(AuthData auth) {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(
                    "INSERT INTO authTokens (authToken, username) VALUES(?, ?)")) {
                preparedStatement.setString(1, auth.authToken());
                preparedStatement.setString(2, auth.username());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthData findAuth(String token) {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT authToken, username FROM authTokens WHERE authToken=?")) {
                preparedStatement.setString(1, token);
                var rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    return new AuthData(
                            rs.getString("authToken"),
                            rs.getString("username")
                    );
                }
            }
        } catch (SQLException | DataAccessException e) {
            return null;
        }
        return null;
    }

    @Override
    public void deleteAuth(AuthData auth) {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DELETE FROM authTokens WHERE authToken=?")) {
                preparedStatement.setString(1, auth.authToken());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DELETE FROM authTokens")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    String createStatement =
            """
            CREATE TABLE IF NOT EXISTS authTokens (
            authToken VARCHAR(255) NOT NULL,
            username VARCHAR(255) NOT NULL
        )
        """;

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(createStatement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("could not execute command");
        }
    }

}
