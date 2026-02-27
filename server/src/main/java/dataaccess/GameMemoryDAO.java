package dataaccess;

import chess.ChessGame;

import java.util.ArrayList;
import java.util.List;

public class GameMemoryDAO implements GameDAO {
    ArrayList<ChessGame> games = new ArrayList<>();
    public List<ChessGame> listGames() {
        return games;
    }

    public int createGame(String s) {
        return 0;
    }

    public void joinGame() {

    }

    public void clear() {

    }
}
