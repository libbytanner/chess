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
        return null;
    }

    @Override
    public void addAuth(AuthData auth) {

    }

    @Override
    public String generateToken() {
        return "";
    }

    @Override
    public AuthData findAuth(String token) {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT authToken, username FROM authTokens WHERE token=?")) {
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

    }

    @Override
    public void clear() {

    }

    String createStatement =
            """
            CREATE TABLE IF NOT EXISTS authTokens (
                authToken VARCHAR(255) NOT NULL,
                username VARCHAR(255) NOT NULL,
                PRIMARY KEY (username)
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
