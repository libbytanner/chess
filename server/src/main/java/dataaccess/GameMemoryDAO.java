package dataaccess;

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

    public void joinGame() {

    }

    public void clear() {
        games = new ArrayList<>();
    }
}
