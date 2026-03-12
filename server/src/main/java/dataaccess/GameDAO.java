package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.sql.SQLException;
import java.util.List;

public interface GameDAO {
    List<GameData> getListGames();
    void addGame(GameData game);
    GameData getGame(int gameID);
    void clear();
    void updateGame(GameData game, ChessGame.TeamColor teamColor, String username, ChessGame newGame) throws SQLException;
}
