package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConnectionManager {
    public final HashMap<Integer, List<Session>> connections = new HashMap<>();

    public void add(int gameID, Session session) {
        List<Session> lst = connections.get(gameID);
        if (lst != null) {
            lst.add(session);
        } else {
            lst = new ArrayList<>();
            lst.add(session);
        }
        connections.put(gameID, lst);
    }

    public void remove(int gameID, Session session) {
        List<Session> lst = connections.get(gameID);
        lst.remove(session);
        connections.put(gameID, lst);
    }

    public void broadcast(Session excludeSession, int gameID, ServerMessage message) throws IOException {
        String messageString = new Gson().toJson(message);
        List<Session> sessions = connections.get(gameID);
        for (Session session : sessions) {
            if (session.isOpen()) {
                if (!session.equals(excludeSession)) {
                    session.getRemote().sendString(messageString);
                }
            }
        }
    }

    public void send(Session session, ServerMessage message) throws IOException {
        String messageString = new Gson().toJson(message);
        session.getRemote().sendString(messageString);
    }
}