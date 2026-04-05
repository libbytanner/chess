package client.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;
import model.ResponseException;
import websocket.commands.ConnectCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.messages.NotificationMessage;

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
                    NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
                    serverMessageObserver.notify(notification);
                }
            });
        } catch (DeploymentException | IOException ex) {
            throw new ResponseException(ex.getMessage(), 500);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void join(String authToken, int gameID) {
        try {
            ConnectCommand connectCommand = new ConnectCommand(authToken, gameID);
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
}
