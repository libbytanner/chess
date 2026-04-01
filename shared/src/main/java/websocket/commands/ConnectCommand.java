package websocket.commands;

public class ConnectCommand extends UserGameCommand {

    ConnectCommand(String authToken, Integer gameID) {
        super(CommandType.CONNECT, authToken, gameID);

    }
}
