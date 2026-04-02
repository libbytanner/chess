package client.websocket;

import com.google.gson.Gson;
import jakarta.websocket.*;
import model.ResponseException;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;

public class WebSocketFacade extends Endpoint {

    private final String socketUrl;
    private final Session session;
    ServerMessageObserver serverMessageObserver;
    public WebSocketFacade(String url, ServerMessageObserver serverMessageObserver) {
        try {
            socketUrl = url.replace("http", "ws") + "/ws";
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, URI.create(socketUrl));
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
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
}
