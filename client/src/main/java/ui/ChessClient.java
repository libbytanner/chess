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
import java.util.List;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient implements ServerMessageObserver {
    private final ServerFacade server;
    private String auth;
    private State state;
    private PrintStream out;

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> notify_message((NotificationMessage) message);
            case LOAD_GAME -> load_game((LoadGameMessage) message);
            case ERROR -> error((ErrorMessage) message);
        }
        printPrompt(out);
    }

    public void notify_message(NotificationMessage message) {
        out.print(SET_TEXT_COLOR_MAGENTA + message.getMessage() + "\n");
    }

    public void load_game(LoadGameMessage message) {
        printBoard(out, message.getGame(), ChessGame.TeamColor.WHITE);
    }

    public void error(ErrorMessage message) {
        out.print(SET_TEXT_COLOR_RED + message.getMessage() + "\n");
    }

    private enum State {LOGGED_IN, LOGGED_OUT, GAME}

    private static final int CHESS_BOARD_SIZE = 8;

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
                yield joinGame(out, params);
            case "list":
                yield listGames(out);
            case "logout":
                yield logout(out);
            case "observe":
                yield(observe(out, params));
            case "help":
                yield help(out);
            case "move":
                yield makeMove(params);
            case "resign":
                yield resign();
            default:
                out.print(command + " is not a valid command. Possible commands:\n");
                yield help(out);
        };
    }

    private String resign() {
        server.resign(auth);
        return "resign";
    }

    private String observe(PrintStream out, String[] params) {
        if (state.equals(State.LOGGED_IN)) {
            if (params.length == 1) {
                int gameID;
                try {
                    gameID = Integer.parseInt(params[0]);
                } catch (Exception ex) {
                    throw new ResponseException("usage: observe <GAME_ID>", 403);
                }
                ChessGame game = getGame(gameID);
                printBoard(out, game, ChessGame.TeamColor.WHITE);
                return "observe";
            }
            throw new ResponseException("usage: observe <GAME_ID>", 400);
        }
        throw new ResponseException("please log in :)\n for options, type help", 400);
    }

    private String joinGame(PrintStream out, String[] params) {
        if (state.equals(State.LOGGED_IN)) {
            if (params.length == 2) {
                int gameID;
                try {
                    gameID = Integer.parseInt(params[0]);
                } catch (Exception ex) {
                    throw new ResponseException("usage: join <GAME_ID> <WHITE|BLACK>", 403);
                }
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
                    } else if (ex.getMessage().equals("forbidden")) {
                        throw new ResponseException("color taken. please join as another color, or join a different game.", 403);
                    }
                } catch (Exception ex) {
                    throw new ResponseException("server error.", 500);
                }
                ChessGame game = getGame(gameID);
                printBoard(out, game, color);
                state = State.GAME;
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
                    \thighlight <ID> - highlight legal moves
                    \tleave - leave the game
                    \tresign - forfeit
                    """);
            return "game options";
        }
    }

    private ChessGame getGame(int gameID) {
        ListGamesResult result = server.listGames(new ListGamesRequest(auth));
        List<GameData> lst = result.games();
        if (gameID < 1 || gameID > lst.size()) {
            throw new ResponseException("game does not exist. please join one of the available games.", 403);
        }
        return lst.get(gameID - 1).game();
    }

    private void printBoard(PrintStream out, ChessGame game, ChessGame.TeamColor color) {
        ChessBoard board = game.getBoard();
//        out.print(ERASE_SCREEN);
//        out.print(moveCursorToLocation(100, 100) + SET_BG_COLOR_DARK_GREY + "\n");
        out.print(SET_BG_COLOR_DARK_GREY + "\n");
        out.print(SET_TEXT_BOLD);
        printLetterHeaders(out, color);
        setBlack(out);
        if (color.equals(ChessGame.TeamColor.WHITE)) {
            printForWhite(out, board);
        } else {
            printForBlack(out, board);
        }
        out.print(SET_BG_COLOR_DARK_GREY);
        printLetterHeaders(out, color);
        out.print("\n");
        out.print(RESET_BG_COLOR + RESET_TEXT_BOLD_FAINT);
    }

    private ChessGame.TeamColor printSquare(PrintStream out, ChessGame.TeamColor current, ChessPiece piece) {
        if (current.equals(ChessGame.TeamColor.WHITE)) {
            current = ChessGame.TeamColor.BLACK;
            setBlack(out);
        } else {
            current = ChessGame.TeamColor.WHITE;
            setWhite(out);
        }
        out.print(getPieceCharacter(piece));
        return current;
    }

    private void printForWhite(PrintStream out, ChessBoard board) {
        ChessGame.TeamColor current = ChessGame.TeamColor.BLACK;
        for (int i = CHESS_BOARD_SIZE; i > 0; i--) {
            printSideNumber(out, i);
            for (int j = 1; j <= CHESS_BOARD_SIZE; j++) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                current = printSquare(out, current, piece);
            }
            printSideNumber(out, i);
            out.print("\n");
            current = switchColor(current, out);
        }
    }

    private void printForBlack(PrintStream out, ChessBoard board) {
        ChessGame.TeamColor current = ChessGame.TeamColor.BLACK;
        for (int i = 1; i <= CHESS_BOARD_SIZE; i++) {
            printSideNumber(out, i);
            for (int j = CHESS_BOARD_SIZE; j > 0; j--) {
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                current = printSquare(out, current, piece);
            }
            printSideNumber(out, i);
            out.print("\n");
            current = switchColor(current, out);
        }
    }

    private String getPieceCharacter(ChessPiece piece) {
        if (piece == null) {
            return (EMPTY);
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.PAWN) &&
                piece.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            return (BLACK_PAWN);
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.PAWN) &&
                piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            return (WHITE_PAWN);
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.KNIGHT) &&
                piece.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            return (BLACK_KNIGHT);
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.KNIGHT) &&
                piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            return (WHITE_KNIGHT);
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.ROOK) &&
                piece.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            return (BLACK_ROOK);
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.ROOK) &&
                piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            return (WHITE_ROOK);
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.BISHOP) &&
                piece.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            return (BLACK_BISHOP);
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.BISHOP) &&
                piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            return (WHITE_BISHOP);
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.QUEEN) &&
                piece.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            return (BLACK_QUEEN);
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.QUEEN) &&
                piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            return (WHITE_QUEEN);
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.KING) &&
                piece.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            return (BLACK_KING);
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.KING) &&
                piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            return (WHITE_KING);
        }
        throw new ResponseException("Server Error", 500);
    }

    private void printLetterHeaders(PrintStream out, ChessGame.TeamColor color) {
        List<String> cols = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h");
        out.print(SET_TEXT_COLOR_BLUE + "   ");
        if (color.equals(ChessGame.TeamColor.BLACK)) {
            cols = cols.reversed();
        }
        for (String col : cols) {
            out.print(" " + col + " ");
        }
        out.print("\n");
    }

    private void printSideNumber(PrintStream out, int i) {
        out.print(SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLUE);
        out.print(" " + i + " ");
    }

    private ChessGame.TeamColor switchColor(ChessGame.TeamColor current, PrintStream out) {
        if (current.equals(ChessGame.TeamColor.WHITE)) {
            setBlack(out);
            return ChessGame.TeamColor.BLACK;
        } else {
            setWhite(out);
            return ChessGame.TeamColor.WHITE;
        }
    }

    private void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_MAGENTA);
    }

    private void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_MAGENTA);
    }

    private String makeMove(String... params) {
        if (params.length != 2) {
            throw new ResponseException("usage: move <start> <end>", 400);
        }
        server.makeMove(auth, params);
        return "move";
    }

}
