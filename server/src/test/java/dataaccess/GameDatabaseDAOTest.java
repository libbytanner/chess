package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
class GameDatabaseDAOTest {

    GameDAO dao = new GameDatabaseDAO();

    @AfterEach
    public void tearDown() {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DROP table games")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

    }


    @Test
    void getListGamesPositiveTest() {
        GameData game1 = new GameData(
                1, "username", "black", "whitevblack", new ChessGame());
        GameData game2 = new GameData(
                2, "username", "black", "whitevblack", new ChessGame());
        GameData game3 = new GameData(
                3, "username", "black", "whitevblack", new ChessGame());

        dao.addGame(game1);
        dao.addGame(game2);
        dao.addGame(game3);

        ArrayList<GameData> exp = new ArrayList<>();
        exp.add(game1);
        exp.add(game2);
        exp.add(game3);

        assertEquals(exp, dao.getListGames());
    }

    @Test
    void getListGamesEmptyTest() {
        ArrayList<GameData> exp = new ArrayList<>();
        assertEquals(exp, dao.getListGames());

    }

    @Test
    void addGamePositiveTest() {
        GameData game = new GameData(
                1, "username", "black", "whitevblack", new ChessGame());
        assertDoesNotThrow(() -> dao.addGame(game));
        assertEquals(game, dao.getGame(game.gameID()));
    }

    @Test
    void getGamePositiveTest() {
        GameData game = new GameData(
                1, "username", "black", "whitevblack", new ChessGame());
        dao.addGame(game);
        assertEquals(game, dao.getGame(game.gameID()));
    }

    @Test
    void getGameNegativeTest() {
        GameData game = new GameData(
                1, "username", "black", "whitevblack", new ChessGame());
        dao.addGame(game);
        assertNull(dao.getGame(5));
    }

    @Test
    void clearTest() {
        GameData game1 = new GameData(
                1, "username", "black", "whitevblack", new ChessGame());
        GameData game2 = new GameData(
                2, "username", "black", "whitevblack", new ChessGame());
        GameData game3 = new GameData(
                3, "username", "black", "whitevblack", new ChessGame());

        dao.addGame(game1);
        dao.addGame(game2);
        dao.addGame(game3);

        ArrayList<GameData> exp = new ArrayList<>();
        dao.clear();
        assertEquals(exp, dao.getListGames());
    }

    @Test
    void updateGameJoinPositiveTest() throws SQLException {
        GameData game = new GameData(
                1, null, "black", "whitevblack", new ChessGame());
        GameData updatedGame = new GameData(
                1, "hello", "black", "whitevblack", game.game());
        dao.addGame(game);
        dao.updateGame(game, ChessGame.TeamColor.WHITE, "hello", game.game());
        assertEquals(updatedGame, dao.getGame(game.gameID()));
        assertEquals(updatedGame.whiteUsername(), dao.getGame(game.gameID()).whiteUsername());
    }

    @Test
    void updateGameNegativeTest() throws SQLException {
        GameData game = new GameData(
                1, null, "black", "whitevblack", new ChessGame());
        GameData newGame = new GameData(
                2, "hello", "black", "whitevblack", game.game());
        dao.addGame(game);
        dao.updateGame(newGame, ChessGame.TeamColor.WHITE, "hello", game.game());
        assertNull(dao.getGame(2));
    }
}