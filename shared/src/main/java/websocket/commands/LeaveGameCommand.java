package websocket.commands;

public class LeaveGameCommand extends UserGameCommand {
    public LeaveGameCommand(String authToken, Integer gameID) {
        super(UserGameCommand.CommandType.LEAVE, authToken, gameID);
    }
}
