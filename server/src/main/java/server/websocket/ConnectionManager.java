package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.HashMap;

public class ConnectionManager {
    public final HashMap<Integer, Session> connections = new HashMap<>();

    public void add(int gameID, Session session) {
        connections.put(gameID, session);
    }

    public void remove(int gameID, Session session) {
        connections.remove(session);
    }

    public void broadcast(Session excludeSession, NotificationMessage notification) throws IOException {
        String msg = notification.toString();
        for (Session c : connections.values()) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}