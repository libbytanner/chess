package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.List;

public class GameMemoryDAO implements GameDAO {
    List<GameData> games = new ArrayList<>();
    public List<GameData> getListGames() {
        if (games.isEmpty()) {
            return List.of();
        }
        return games;
    }

    public void addGame(GameData game) {
        games.add(game);
    }

    public GameData getGame(int gameID) {
        for (GameData game : games) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    public void clear() {
        games = new ArrayList<>();
    }

    public void updateGame(GameData game, ChessGame.TeamColor teamColor, String username) {
        String white = game.whiteUsername();
        String black = game.blackUsername();
        if (teamColor.equals(ChessGame.TeamColor.WHITE)) {
            white = username;
        } else if (teamColor.equals(ChessGame.TeamColor.BLACK)) {
            black = username;
        }
        games.remove(game);
        GameData updated_game = new GameData(game.gameID(), white, black, game.gameName(), game.game());
        games.add(updated_game.gameID() - 1, updated_game);
    }
}
