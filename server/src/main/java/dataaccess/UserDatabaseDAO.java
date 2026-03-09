package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserDatabaseDAO implements UserDAO {

    public UserDatabaseDAO() {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public ArrayList<UserData> getUsers() {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT username, password, email FROM users)")) {

            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void addUser(UserData user) {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO users (username, password, email) VALUES(?, ?, ?)")) {
                preparedStatement.setString(1, user.username());
                preparedStatement.setString(2, user.password());
                preparedStatement.setString(3, user.email());

            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {

    }

    String createStatement =
        """
        CREATE TABLE IF NOT EXISTS users (
            username VARCHAR(255) NOT NULL,
            password VARCHAR(255) NOT NULL,
            email VARCHAR(255) NOT NULL,
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
            ex.printStackTrace();   // <-- this shows exactly why it failed
            throw new DataAccessException("could not execute command");
        }
    }
}
