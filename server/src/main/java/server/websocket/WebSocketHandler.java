package server.websocket;

import com.google.gson.Gson;
import model.ResponseException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.*;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        int gameID;
        Session session = ctx.session;
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            gameID = command.getGameID();
            String username = getUsername(command.getAuthToken());
            saveSession(gameID, session);
            switch (command.getCommandType()) {
                case CONNECT -> {
                    ConnectCommand newCommand = new ConnectCommand(command.getAuthToken(), gameID);
                    connect(session, username, newCommand, gameID);
                }
                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command, gameID);
                case LEAVE -> leave(session, username, (LeaveGameCommand) command, gameID);
                case RESIGN -> resign(session, username, (ResignCommand) command);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void saveSession(int gameID, Session session) {
    }

    private String getUsername(String authToken) {
        return "username";
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(Session session, String username, ConnectCommand command, int gameID) throws IOException {
        connections.add(gameID, session);
        var message = String.format("%s joined the game", username);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(session, gameID, notification);
    }

    private void makeMove(Session session, String username, MakeMoveCommand command, int gameID) throws IOException {
        String moveString = "";
        var message = String.format("%s made move: %s", username, moveString);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(session, gameID, notification);
        connections.remove(0, session);
    }

    public void leave(Session session, String username, LeaveGameCommand command, int gameID) throws ResponseException {
        try {
            var message = String.format("%s left the game", username);
            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(null, gameID, notification);
        } catch (Exception ex) {
            throw new ResponseException(ex.getMessage(), 500);
        }
    }

    public void resign(Session session, String username, ResignCommand command) throws ResponseException {

    }
}