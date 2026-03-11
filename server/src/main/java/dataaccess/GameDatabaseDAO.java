package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class GameDatabaseDAO implements GameDAO {

    public GameDatabaseDAO() {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<GameData> getListGames() {
        return List.of();
    }

    @Override
    public void addGame(GameData game) {

    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public void updateGame(GameData game, ChessGame.TeamColor teamColor, String s) {

    }

    String createStatement =
            """
            CREATE TABLE IF NOT EXISTS games (
                game INT NOT NULL,
                whiteUsername VARCHAR(255) NOT NULL,
                blackUsername VARCHAR(255) NOT NULL,
                gameName VARCHAR(255) NOT NULL,
                ChessGame VARCHAR(255) NOT NULL, 
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
