package websocket.commands;

public class ConnectCommand extends UserGameCommand {
    Type type;
    public enum Type {
        PLAYER,
        OBSERVER
    }

    public ConnectCommand(String authToken, Integer gameID, Type type) {
        super(CommandType.CONNECT, authToken, gameID);
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
