package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.ResponseException;
import io.javalin.websocket.*;
import model.model.AuthData;
import model.model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;
import java.sql.SQLException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
//    private final UserService userService;
    private final AuthDAO authDao;
    private final GameDAO gameDao;
    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(AuthDAO authDao, GameDAO gameDao) {
        this.authDao = authDao;
        this.gameDao = gameDao;
    }

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
            String username = getUsername(session, command.getAuthToken());
            switch (command.getCommandType()) {
                case CONNECT -> {
                    ConnectCommand newCommand = new ConnectCommand(command.getAuthToken(), gameID);
                    connect(session, username, newCommand);
                }
                case MAKE_MOVE -> {
                    MakeMoveCommand newCommand = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(session, username, newCommand, gameID);
                }
                case LEAVE -> {
                    LeaveGameCommand newCommand = new Gson().fromJson(ctx.message(), LeaveGameCommand.class);
                    leave(session, username, newCommand);
                }
                case RESIGN -> {
                    ResignCommand newCommand = new Gson().fromJson(ctx.message(), ResignCommand.class);
                    resign(session, username, newCommand);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private String getUsername(Session session, String authToken) throws IOException {
        AuthData auth = authDao.findAuth(authToken);
        if (auth == null) {
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Unauthorized");
            connections.send(session, error);
            return null;
        }
        return auth.username();
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private LoadGameMessage createLoadGameMessage(int gameID) {
        GameData game = gameDao.getGame(gameID);
        return new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
    }

    private void connect(Session session, String username, ConnectCommand command) throws IOException {
        if (gameDao.getGame(command.getGameID()) == null) {
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Invalid game");
            connections.send(session, error);
        } else if (username != null) {
            connections.add(command.getGameID(), session);
            var message = String.format("%s joined the game", username);
            var loadGameMessage = createLoadGameMessage(command.getGameID());
            connections.send(session, loadGameMessage);
            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(session, command.getGameID(), notification);
        }
    }

    private boolean checkCheckmate(GameData game) throws IOException {
        if (game.game().isInCheckmate(ChessGame.TeamColor.WHITE)) {
            var checkmateNotification = new NotificationMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    "white in checkmate!");
            connections.broadcast(null, game.gameID(), checkmateNotification);
            game.game().setTeamTurn(null);
            return true;
        } if (game.game().isInCheckmate(ChessGame.TeamColor.BLACK)) {
            var checkmateNotification = new NotificationMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    "black in checkmate!");
            connections.broadcast(null, game.gameID(), checkmateNotification);
            game.game().setTeamTurn(null);
            return true;
        }
        return false;
    }

    private void checkCheck(GameData game) throws IOException {
        if (game.game().isInCheck(ChessGame.TeamColor.WHITE)) {
            var checkmateNotification = new NotificationMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    "white in check!");
            connections.broadcast(null, game.gameID(), checkmateNotification);
        } if (game.game().isInCheck(ChessGame.TeamColor.BLACK)) {
            var checkmateNotification = new NotificationMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    "black in check!");
            connections.broadcast(null, game.gameID(), checkmateNotification);
        }
    }

    private void makeMove(Session session, String username, MakeMoveCommand command, int gameID) throws IOException {
        if (authDao.findAuth(command.getAuthToken()) != null) {
            GameData game = gameDao.getGame(gameID);
            ChessGame.TeamColor color = game.game().getTeamTurn();
            boolean outOfTurn = false;

            if (color != null && color.equals(ChessGame.TeamColor.WHITE) && !game.whiteUsername().equals(username)) {
                outOfTurn = true;
            } else if (color != null && color.equals(ChessGame.TeamColor.BLACK) && !game.blackUsername().equals(username)) {
                outOfTurn = true;
            }

            if (outOfTurn) {
                var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Out of turn");
                connections.send(session, error);
            } else {
                if (color == null) {
                    var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Game has ended");
                    connections.send(session, error);
                } else {
                    try {
                        game.game().makeMove(command.getMove());
                        gameDao.updateGame(game, null, username, game.game());
                        var loadGameMessage = createLoadGameMessage(gameID);
                        connections.broadcast(null, gameID, loadGameMessage);
                        var message = String.format("%s made move: %s", username, command.getMove().toString());
                        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                        connections.broadcast(session, gameID, notification);
                        gameDao.updateGame(game, null, username, game.game());

                        if (!checkCheckmate(game)) {
                            checkCheck(game);
                        }
                        checkStalemate(game);

                        gameDao.updateGame(game, null, username, game.game());
                    } catch (InvalidMoveException e) {
                        var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: Invalid move");
                        connections.send(session, error);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private void checkStalemate(GameData game) throws IOException {
        if (game.game().isInStalemate(ChessGame.TeamColor.WHITE)) {
            var checkmateNotification = new NotificationMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    "white in stalemate");
            connections.broadcast(null, game.gameID(), checkmateNotification);
        } if (game.game().isInStalemate(ChessGame.TeamColor.BLACK)) {
            var checkmateNotification = new NotificationMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    "black in stalemate");
            connections.broadcast(null, game.gameID(), checkmateNotification);
        }
    }

    public void leave(Session session, String username, LeaveGameCommand command) throws ResponseException {
        try {
            GameData game = gameDao.getGame(command.getGameID());
            if (game.whiteUsername() != null && game.whiteUsername().equals(username)) {
                gameDao.updateGame(game, ChessGame.TeamColor.WHITE, null, game.game());
            } else if (game.blackUsername().equals(username)) {
                gameDao.updateGame(game, ChessGame.TeamColor.BLACK, null, game.game());
            }
            gameDao.updateGame(game, null, username, game.game());
            connections.remove(command.getGameID(), session);
            var message = String.format("%s left the game", username);
            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(session, command.getGameID(), notification);
        } catch (Exception ex) {
            throw new ResponseException(ex.getMessage(), 500);
        }
    }

    public void resign(Session session, String username, ResignCommand command) throws ResponseException, IOException {
        GameData game = gameDao.getGame(command.getGameID());
        if (!game.whiteUsername().equals(username) &&
                !game.blackUsername().equals(username)) {
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: Can't resign as an observer");
            connections.send(session, error);
        } else if (game.game().getTeamTurn() == null) {
            var error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: Can't resign once game has ended");
            connections.send(session, error);
        } else {
            String message = String.format("resign %s", username);

            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(null, command.getGameID(), notification);

            game.game().setTeamTurn(null);
            try {
                gameDao.updateGame(game, null, username, game.game());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}