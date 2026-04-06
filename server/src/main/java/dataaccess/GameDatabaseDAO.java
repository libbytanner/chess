package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.model.GameData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GameDatabaseDAO implements GameDAO {

    public GameDatabaseDAO() {
        DatabaseDAOInitializer init = new DatabaseDAOInitializer();
        init.initialize(createStatement);
    }

    private String toJson(ChessGame game) {
        var serializer = new GsonBuilder().serializeNulls().create();
        return serializer.toJson(game);
    }

    public ChessGame fromJson(String string) {
        var serializer = new Gson();
        return serializer.fromJson(string, ChessGame.class);
    }


    @Override
    public List<GameData> getListGames() {
        ArrayList<GameData> games = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM games")) {
                var rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    ChessGame game = fromJson(rs.getString("game"));
                    GameData gameData =  new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            game
                    );
                    games.add(gameData);
                }
            }
        } catch (SQLException | DataAccessException e) {
            return null;
        }
        return games;
    }

    @Override
    public void addGame(GameData game) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String gameString = toJson(game.game());
            try (var preparedStatement = conn.prepareStatement(
                    """
                INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game) VALUES(?, ?, ?, ?, ?)
                """)) {
                preparedStatement.setInt(1, game.gameID());
                preparedStatement.setString(2, game.whiteUsername());
                preparedStatement.setString(3, game.blackUsername());
                preparedStatement.setString(4, game.gameName());
                preparedStatement.setString(5, gameString);

                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GameData getGame(int gameID) {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM games WHERE gameID=?")) {
                preparedStatement.setInt(1, gameID);
                var rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    ChessGame game = fromJson(rs.getString("game"));
                    return new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            game
                    );
                }
            }
        } catch (SQLException | DataAccessException e) {
            return null;
        }
        return null;
    }

    @Override
    public void clear() {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DELETE FROM games")) {
                preparedStatement.executeUpdate();

            }

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateGame(GameData game, ChessGame.TeamColor teamColor, String username, ChessGame newGame) throws SQLException {
        String newGameString = toJson(newGame);
        try (Connection conn = DatabaseManager.getConnection()) {
            if (teamColor == ChessGame.TeamColor.WHITE) {
                try (var preparedStatement = conn.prepareStatement("UPDATE games SET whiteUsername = ? WHERE gameID = ?")) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setInt(2, game.gameID());
                    preparedStatement.executeUpdate();
                }
            } else if (teamColor == ChessGame.TeamColor.BLACK) {
                try (var preparedStatement = conn.prepareStatement("UPDATE games SET blackUsername = ? WHERE gameID = ?")) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setInt(2, game.gameID());
                    preparedStatement.executeUpdate();
                }
            }
            try (var preparedStatement = conn.prepareStatement("UPDATE games SET game = ? WHERE gameID = ?")) {
                preparedStatement.setString(1, newGameString);
                preparedStatement.setInt(2, game.gameID());
                preparedStatement.executeUpdate();
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    String createStatement =
            """
            CREATE TABLE IF NOT EXISTS games (
                gameID INT NOT NULL,
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                gameName VARCHAR(255) NOT NULL,
                game VARCHAR(2000) NOT NULL,
                PRIMARY KEY (gameID)
            )
            """;

}
