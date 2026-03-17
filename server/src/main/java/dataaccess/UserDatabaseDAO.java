package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserDatabaseDAO implements UserDAO {

    public UserDatabaseDAO() {
        DatabaseDAOInitializer init = new DatabaseDAOInitializer();
        init.initialize(createStatement);
    }


    @Override
    public ArrayList<UserData> getUsers() {
        ArrayList<UserData> users = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM users")) {
                var rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    UserData user = new UserData(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"));
                    users.add(user);
                }
                return users;
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserData getUser(String username) {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(
                    "SELECT username, password, email FROM users WHERE username=?")) {
                preparedStatement.setString(1, username);
                var rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    return new UserData(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"));
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException();
        }
        return null;
    }

    @Override
    public void addUser(UserData user) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
            try (var preparedStatement = conn.prepareStatement(
                    "INSERT INTO users (username, password, email) VALUES(?, ?, ?)")) {
                preparedStatement.setString(1, user.username());
                preparedStatement.setString(2, hashedPassword);
                preparedStatement.setString(3, user.email());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DELETE FROM users")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
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
}
