package ui;

import chess.*;
import client.websocket.ServerMessageObserver;
import model.ResponseException;
import client.ServerFacade;
import model.model.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient implements ServerMessageObserver {
    private final ServerFacade server;
    private String auth;
    private State state;
    private PrintStream out;
    private ChessGame.TeamColor color;
    private ChessGame currentGame;

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> notifyMessage((NotificationMessage) message);
            case LOAD_GAME -> loadGame((LoadGameMessage) message);
            case ERROR -> error((ErrorMessage) message);
        }
        printPrompt(out);
    }

    public void notifyMessage(NotificationMessage message) {
        out.print(SET_TEXT_COLOR_MAGENTA + message.getMessage() + "\n");
    }

    public void loadGame(LoadGameMessage message) {
        BoardPrinter printer = new BoardPrinter();
        printer.printBoard(out, message.getGame(), color, null, null);
        this.currentGame = message.getGame();
    }

    public void error(ErrorMessage message) {
        out.print(SET_TEXT_COLOR_RED + message.getMessage() + "\n");
    }

    private enum State {LOGGED_IN, LOGGED_OUT, GAME, OBSERVE}

    public ChessClient(int port) {
        server = new ServerFacade(port, this);
        state = State.LOGGED_OUT;
    }

    public void run() {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        this.out = out;
        out.println(WHITE_KING + "Welcome to Chess. For options, type help" + WHITE_KING);
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
//            out.print(ERASE_SCREEN);
            printPrompt(out);
            String line = scanner.nextLine();
            try {
                result = eval(line, out);
            } catch (Exception ex) {
                out.print(ex.getMessage() + "\n");
            }
        }
    }

    private void printPrompt(PrintStream out) {
        out.print(SET_TEXT_COLOR_BLUE + ">>> ");
    }

    private String eval(String line, PrintStream out) {
        String[] tokens = line.toLowerCase().split(" ");
        String command = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (command) {
            case "register":
                yield register(out, params);
            case "quit":
                out.print("quitting chess...");
                yield "quit";
            case "login":
                yield login(out, params);
            case "create":
                yield createGame(out, params);
            case "join":
                yield joinGame(params);
            case "list":
                yield listGames(out);
            case "logout":
                yield logout(out);
            case "observe":
                yield(observe(params));
            case "help":
                yield help(out);
            case "move":
                yield makeMove(params);
            case "resign":
                yield resign();
            case "leave":
                yield leave();
            case "redraw":
                yield redraw();
            case "highlight":
                yield highlight(params);
            default:
                out.print(command + " is not a valid command. Possible commands:\n");
                yield help(out);
        };
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

    private String highlight(String...params) {
        BoardPrinter printer = new BoardPrinter();
        ChessPosition position;
        try {
            position = makePosition(params[0]);
        } catch (Exception ex) {
            throw new ResponseException("usage: highlight <position>", 400);
        }
        if (currentGame.getBoard().getPiece(position) == null) {
            throw new ResponseException("There is no piece at that position", 400);
        }
        Collection<ChessMove> validMoves = currentGame.validMoves(position);
        printer.printBoard(out, currentGame, color, validMoves, position);
        return "highlight";
    }

    private String redraw() {
        BoardPrinter printer = new BoardPrinter();
        printer.printBoard(out, currentGame, color, null, null);
        return "redraw";
    }

    private String leave() {
        server.leave(auth);
        color = null;
        currentGame = null;
        state = State.LOGGED_IN;
        return "leave";
    }

    private String resign() {
        if (!state.equals(State.GAME)) {
            throw new ResponseException("you are not in a game", 400);
        }
        out.print("are you sure you want to resign? type yes or no\n");
        printPrompt(out);
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        line = line.toLowerCase();
        if (line.equals("yes")) {
            server.resign(auth);
        } if (line.equals("no")) {
            out.print("you are still in the game\n");
        }
        return "resign";
    }

    private String observe(String[] params) {
        if (state.equals(State.LOGGED_IN)) {
            if (params.length == 1) {
                int gameID;
                try {
                    gameID = Integer.parseInt(params[0]);
                    server.observe(new JoinGameRequest(auth, null, gameID));
                    this.color = null;
                    this.state = State.GAME;
                } catch (Exception ex) {
                    throw new ResponseException("another error", 403);
                }
                return "observe";
            }
            throw new ResponseException("usage: observe <GAME_ID>", 400);
        }
        throw new ResponseException("for valid commands, type help", 400);
    }

    private String joinGame(String[] params) {
        if (state.equals(State.LOGGED_IN)) {
            if (params.length == 2) {
                int gameID;
                try {
                    gameID = Integer.parseInt(params[0]);
                } catch (Exception ex) {
                    throw new ResponseException("usage: join <GAME_ID> <WHITE|BLACK>", 403);
                }

                getGame(gameID);

                ChessGame.TeamColor color;
                switch (params[1]) {
                    case "black" -> color = ChessGame.TeamColor.BLACK;
                    case "white" -> color = ChessGame.TeamColor.WHITE;
                    default -> throw new ResponseException("invalid color", 400);
                }

                try {
                    server.joinGame(new JoinGameRequest(auth, color, gameID));
                } catch (ResponseException ex) {
                    if (ex.getMessage().equals("bad request")) {
                        throw new ResponseException("game does not exist. please join an available game", 400);
                    } else if (ex.getCode() == 403) {
                        throw new ResponseException("color taken. please join as another color, or join a different game.", 403);
                    }
                } catch (Exception ex) {
                    throw new ResponseException("server error.", 500);
                }
                state = State.GAME;
                this.color = color;
                return "join";
            }
            throw new ResponseException("usage: join <GAME_ID> <WHITE|BLACK>", 400);
        }
        throw new ResponseException("please log in :)\n for options, type help", 400);
    }

    private String createGame(PrintStream out, String... params) {
        if (state.equals(State.LOGGED_IN)) {
            if (params.length == 1) {
                server.createGame(new CreateGameRequest(auth, params[0]));
                out.printf("success! %s created.\n", params[0]);
                return "create";
            }
            throw new ResponseException("usage: create <GAME_NAME>", 400);
        }
        throw new ResponseException("please log in :)\nfor options, type help", 400);
    }

    private String listGames(PrintStream out) {
        if (state.equals(State.LOGGED_IN)) {
            ListGamesResult result = server.listGames(new ListGamesRequest(auth));
            out.print(SET_TEXT_COLOR_MAGENTA);
            for (int i = 0; i < result.games().size(); i++) {
                GameData game = result.games().get(i);
                out.format("\t%d. %s   White Player: %s   Black Player: %s\n",
                        i + 1, game.gameName(), game.whiteUsername(), game.blackUsername());
            }
            return "list";
        } else if (state.equals(State.LOGGED_OUT)) {
            throw new ResponseException("please log in :)\nfor options, type help", 400);
        } else {
            out.print("game commands include: \n");
            return help(out);
        }
    }

    private String register(PrintStream out, String... params) {
        if (state.equals(State.LOGGED_OUT)) {
            if (params.length == 3) {
                try {
                    RegisterResult result = server.register(new RegisterRequest(params[0], params[1], params[2]));
                    state = State.LOGGED_IN;
                    auth = result.authToken();
                    out.printf("%s logged in\n", result.username());
                    return "success";
                } catch (ResponseException ex) {
                    out.print("username taken. please choose a new username\n");
                    return "fail";
                }
            }
            throw new ResponseException("usage: register <USERNAME> <PASSWORD> <EMAIL>", 400);
        }
        throw new ResponseException("You are already logged in :)\n for options, type help", 400);
    }

    private String login(PrintStream out, String... params) {
        if (state.equals(State.LOGGED_OUT)) {
            if (params.length == 2) {
                try {
                    LoginResult result = server.login(new LoginRequest(params[0], params[1]));
                    state = State.LOGGED_IN;
                    auth = result.authToken();
                    out.printf("%s logged in\n", result.username());
                    return "logged in";
                } catch (ResponseException ex) {
                    throw new ResponseException("invalid username and/or password. please try again.", 401);
                }
            }
            throw new ResponseException("usage: <USERNAME> <PASSWORD>", 400);
        }
        throw new ResponseException("you are already logged in :)\nfor options, type help", 400);
    }

    private String logout(PrintStream out) {
        if (state.equals(State.LOGGED_IN)) {
            server.logout(new LogoutRequest(auth));
                state = State.LOGGED_OUT;
                out.print("logged out\n");
                return "logged in";
        }
        throw new ResponseException("Server Error :(", 500);
    }

    private String help(PrintStream out) {
        if (state.equals(State.LOGGED_OUT)) {
            out.print(SET_TEXT_COLOR_MAGENTA + """
                    \tregister <USERNAME> <PASSWORD> <EMAIL> - register a new user
                    \tlogin <USERNAME> <PASSWORD> - log in to an existing account
                    \tquit - quit chess
                    \thelp - possible commands
                    """);
            return "logged_out options";
        } else if (state.equals(State.LOGGED_IN)) {
            out.print(SET_TEXT_COLOR_MAGENTA + """
                    \tcreate <GAME_NAME> - create game
                    \tlist - list all games
                    \tjoin <ID> <WHITE|BLACK> - join game as player
                    \tobserve <ID> - join game as observer
                    \tlogout - logout user
                    \tquit - quit chess
                    \thelp - possible commands
                    """);
            return "logged_in options";
        } else {
            out.print(SET_TEXT_COLOR_MAGENTA + """
                    \tmove <old_position> <new_position> - move
                    \tredraw - redraw the chess board
                    \thighlight <position> - highlight legal moves for a piece at given position
                    \tleave - leave the game
                    \tresign - forfeit
                    """);
            return "game options";
        }
    }

    private void getGame(int gameID) {
        ListGamesResult result = server.listGames(new ListGamesRequest(auth));
        List<GameData> lst = result.games();
        if (gameID < 1 || gameID > lst.size()) {
            throw new ResponseException("game does not exist. please join one of the available games.", 403);
        }
    }

    private String makeMove(String... params) {
        if (state.equals(State.GAME)) {
            if (params.length < 2 || params.length > 3) {
                throw new ResponseException("usage: move <start> <end>", 400);
            }
            ChessMove move = null;
            try {
                ChessPosition start = makePosition(params[0]);
                ChessPosition end = makePosition(params[1]);
                ChessPiece.PieceType type;
                if (params.length == 3) {
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
                move = new ChessMove(start, end, type);
            } catch (Exception e) {
                throw new ResponseException("usage: move <start> <end>", 400);
            }
            server.makeMove(auth, move);
            return "move";
        } else {
            throw new ResponseException("please join a game", 400);
        }
    }

}
