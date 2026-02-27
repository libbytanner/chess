package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {
    List<GameData> getListGames();
    void addGame(GameData game);
    void joinGame();
    void clear();
}
