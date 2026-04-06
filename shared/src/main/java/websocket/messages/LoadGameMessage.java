package websocket.messages;

import chess.ChessGame;
import model.model.GameData;

public class LoadGameMessage extends ServerMessage {
    GameData game;
    public LoadGameMessage(ServerMessageType type, GameData game) {
        super(type);
        this.game = game;
    }

    public ChessGame getGame() {
        return game.game();
    }

    public String getWhite() {
        return game.whiteUsername();
    }

    public String getBlack() {
        return game.blackUsername();
    }
}
