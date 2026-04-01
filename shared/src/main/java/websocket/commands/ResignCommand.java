package websocket.commands;

public class ResignCommand extends UserGameCommand {
    public ResignCommand(String authToken, Integer gameID) {
        super(UserGameCommand.CommandType.RESIGN, authToken, gameID);
    }
}
