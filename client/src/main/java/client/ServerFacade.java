package client;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.websocket.ServerMessageObserver;
import client.websocket.WebSocketFacade;
import com.google.gson.Gson;
import model.ResponseException;
import model.model.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import java.net.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;


public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;
    private final WebSocketFacade ws;

    public ServerFacade(int port, ServerMessageObserver serverMessageObserver) {
        serverUrl = "http://localhost:" + port;
        ws = new WebSocketFacade(serverUrl, serverMessageObserver);
    }

    public RegisterResult register(RegisterRequest regRequest) {
        var request = buildRequest("POST", "/user", regRequest, null);
        var response = sendRequest(request);
        return handleResponse(response, RegisterResult.class);
    }

    public LoginResult login(LoginRequest loginRequest) {
        var request = buildRequest("POST", "/session", loginRequest, null);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public void logout(LogoutRequest logoutRequest) {
        var request = buildRequest("DELETE", "/session", null, logoutRequest.authToken());
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) {
        var request = buildRequest("GET", "/game", listGamesRequest, listGamesRequest.authToken());
        var response = sendRequest(request);
        return handleResponse(response, ListGamesResult.class);
    }

    public void joinGame(JoinGameRequest joinRequest) {
        var request = buildRequest("PUT", "/game", joinRequest, joinRequest.authToken());
        var response = sendRequest(request);
        ws.join(joinRequest.authToken(), joinRequest.gameID());
        handleResponse(response, null);
    }

    public CreateGameResult createGame(CreateGameRequest createRequest) {
        var request = buildRequest("POST", "/game", createRequest, createRequest.authToken());
        var response = sendRequest(request);
        return handleResponse(response, CreateGameResult.class);
    }

    private ChessPosition makePosition(String position) {
        int row;
        int col = Integer.parseInt(position.substring(position.length() - 1));
        switch (position.charAt(0)) {
            case 'a' -> row = 1;
            case 'b' -> row = 2;
            case 'c' -> row = 3;
            case 'd' -> row = 4;
            case 'e' -> row = 5;
            case 'f' -> row = 6;
            case 'g' -> row = 7;
            case 'h' -> row = 8;
            default -> row = 0;
        }
        if (row == 0 || col < 1 || col > 8) {
            throw new ResponseException("Invalid position. Ex: move e4 d5 [optional promotion piece]", 400);
        }
        return new ChessPosition(col, row);
    }

    public void makeMove(String authToken, String... params) {
        ChessPosition start = makePosition(params[0]);
        ChessPosition end = makePosition(params[1]);
        ChessPiece.PieceType type;
        if (params.length > 2) {
            switch (params[2]) {
                case "knight" -> type = ChessPiece.PieceType.KNIGHT;
                case "queen" -> type = ChessPiece.PieceType.QUEEN;
                case "rook" -> type = ChessPiece.PieceType.ROOK;
                case "bishop" -> type = ChessPiece.PieceType.BISHOP;
                default -> type = null;
            }
        } else {
            type = null;
        }
        ChessMove move = new ChessMove(start, end, type);
        ws.makeMove(authToken, move);
    }

    private HttpRequest buildRequest(String method, String path, Request body, String auth) {
        var request = HttpRequest.newBuilder()
            .uri(URI.create(serverUrl + path))
            .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (auth != null) {
            request.setHeader("authorization", auth);
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Request request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) {
        var status = response.statusCode();
        if (status != 200) {
            var body = response.body();
            throw new ResponseException(body, status);
        }
        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }
        return null;
    }

    public void clear() {
        var request = buildRequest("DELETE", "/db", null, null);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public void resign(String authToken) {
        ws.resign(authToken);
    }

    public void leave(String auth) {
        ws.leave(auth);
    }
}
