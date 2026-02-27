package dataaccess;

import chess.ChessGame;

import java.util.List;

public interface GameDAO {
    List<ChessGame> listGames();
    int createGame(String s);
    void joinGame();
    void clear();

}
