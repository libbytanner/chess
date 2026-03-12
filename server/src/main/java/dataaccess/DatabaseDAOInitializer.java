package dataaccess;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseDAOInitializer {
    public DatabaseDAOInitializer() {

    }

    public void initialize(String createStatement) {
        try {
            configureDatabase(createStatement);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void configureDatabase(String createStatement) throws DataAccessException {
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
