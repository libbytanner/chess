package client.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import jakarta.websocket.*;
import model.ResponseException;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveGameCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.messages.*;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;

public class WebSocketFacade extends Endpoint {

    private final String socketUrl;
    private final Session session;
    ServerMessageObserver serverMessageObserver;
    int gameID = 0;

    public WebSocketFacade(String url, ServerMessageObserver serverMessageObserver) {
        try {
            socketUrl = url.replace("http", "ws") + "/ws";
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, URI.create(socketUrl));
            this.serverMessageObserver = serverMessageObserver;
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    Gson gson = createMessageSerializer();
                    ServerMessage notification = gson.fromJson(message, ServerMessage.class);
                    serverMessageObserver.notify(notification);
                }
            });
        } catch (DeploymentException | IOException ex) {
            throw new ResponseException(ex.getMessage(), 500);
        }
    }

    public Gson createMessageSerializer() {
        GsonBuilder serializer = new GsonBuilder();
        serializer.registerTypeAdapter(ServerMessage.class,
                (JsonDeserializer<ServerMessage>) (el, serverMessageType, ctx) -> {
                    ServerMessage message = null;
                    if (el.isJsonObject()) {
                        String messageType = el.getAsJsonObject().get("serverMessageType").getAsString();
                        switch (ServerMessage.ServerMessageType.valueOf(messageType)) {
                            case ServerMessage.ServerMessageType.NOTIFICATION ->
                                    message = ctx.deserialize(el, NotificationMessage.class);
                            case ServerMessage.ServerMessageType.LOAD_GAME ->
                                    message = ctx.deserialize(el, LoadGameMessage.class);
                            case ServerMessage.ServerMessageType.ERROR ->
                                    message = ctx.deserialize(el, ErrorMessage.class);
                        }
                    }
                    return message;
                });
        return serializer.create();
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void join(String authToken, int gameID, ConnectCommand.Type type) {
        try {
            ConnectCommand connectCommand = new ConnectCommand(authToken, gameID, type);
            this.session.getBasicRemote().sendText(new Gson().toJson(connectCommand));
            this.gameID = gameID;
        } catch (IOException e) {
            throw new ResponseException(e.getMessage(), 500);
        }
    }



    public void makeMove(String authToken, ChessMove move) {
        try {
            MakeMoveCommand moveCommand = new MakeMoveCommand(authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(moveCommand));
        } catch (IOException e) {
            throw new ResponseException(e.getMessage(), 500);
        }
    }

    public void resign(String authToken) {
        try {
            ResignCommand resignCommand = new ResignCommand(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(resignCommand));
        } catch (IOException e) {
            throw new ResponseException(e.getMessage(), 500);
        }
    }

    public void leave(String authToken) {
        try {
            LeaveGameCommand leaveCommand = new LeaveGameCommand(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(leaveCommand));
        } catch (IOException e) {
            throw new ResponseException(e.getMessage(), 500);
        }
    }
}
