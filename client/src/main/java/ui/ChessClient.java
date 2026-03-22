package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ResponseException;
import client.ServerFacade;
import model.model.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient {
    private final ServerFacade server;
    private String auth;
    private State state;

    private enum State {LOGGED_IN, LOGGED_OUT}

    private final int CHESS_BOARD_SIZE = 8;

    public ChessClient(int port) {
        server = new ServerFacade(port);
        state = State.LOGGED_OUT;
    }

    public void run() {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
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
            default:
                out.print(command + " is not a valid command. Possible commands:\n");
                yield help(out);
        };
    }

    private String observe(PrintStream out, String[] params) {
        if (state.equals(State.LOGGED_IN)) {
            if (params.length == 1) {
                int gameID = Integer.parseInt(params[0]);
                printBoard(out, gameID, ChessGame.TeamColor.WHITE);
                return "observe";
            }
            throw new ResponseException("usage: observe <GAME_ID>\n", 400);
        }
        throw new ResponseException("Please log in :)\n for options, type help\n", 400);
    }

    private String joinGame(PrintStream out, String[] params) {
        if (state.equals(State.LOGGED_IN)) {
            if (params.length == 2) {
                int gameID = Integer.parseInt(params[0]);
                ChessGame.TeamColor color;
                switch (params[1]) {
                    case "black" -> color = ChessGame.TeamColor.BLACK;
                    case "white" -> color = ChessGame.TeamColor.WHITE;
                    default -> throw new ResponseException("invalid color\n", 400);
                }
                try {
                    server.joinGame(new JoinGameRequest(auth, color, gameID));
                } catch (ResponseException ex) {
                    throw new ResponseException("color taken. please join as another color, or join a different game.\n",
                            400);
                } catch (Exception ex) {
                    throw new ResponseException("Server error.\n",
                            500);
                }
                printBoard(out, gameID, color);
                return "join";
            }
            throw new ResponseException("usage: join <GAME_ID> <WHITE|BLACK>\n", 400);
        }
        throw new ResponseException("Please log in :)\n for options, type help\n", 400);
    }

    private String createGame(PrintStream out, String... params) {
        if (state.equals(State.LOGGED_IN)) {
            if (params.length == 1) {
                CreateGameResult result = server.createGame(new CreateGameRequest(auth, params[0]));
                out.printf("Success! %s created with id %d.\n", params[0], result.gameID());
                return "create";
            }
            throw new ResponseException("usage: create <GAME_NAME>\n", 400);
        }
        throw new ResponseException("Please log in :)\nfor options, type help\n", 400);
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
        }
        throw new ResponseException("Please log in :)\nfor options, type help\n", 400);
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
                    out.print("Username taken. Please choose a new username\n");
                    return "fail";
                }
            }
            throw new ResponseException("usage: register <USERNAME> <PASSWORD> <EMAIL>\n", 400);
        }
        throw new ResponseException("You are already logged in :)\n for options, type help\n", 400);
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
                    throw new ResponseException("Invalid username and/or password. Please try again.\n", 401);
                }
            }
            throw new ResponseException("usage: <USERNAME> <PASSWORD>\n", 400);
        }
        throw new ResponseException("You are already logged in :)\nFor options, type help\n", 400);
    }

    private String logout(PrintStream out) {
        if (state.equals(State.LOGGED_IN)) {
            server.logout(new LogoutRequest(auth));
                state = State.LOGGED_OUT;
                out.print("logged out\n");
                return "logged in";
        }
        throw new ResponseException("Server Error :(\n", 500);
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
        } else {
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
        }
    }

    private ChessGame getGame(int gameID) {
        ListGamesResult result = server.listGames(new ListGamesRequest(auth));
        List<GameData> lst = result.games();
        return lst.get(gameID - 1).game();
    }

    private void printBoard(PrintStream out, int gameID, ChessGame.TeamColor color) {
        ChessGame game = getGame(gameID);
        ChessBoard board = game.getBoard();
        out.print(ERASE_SCREEN);
        out.print(moveCursorToLocation(100, 100) + SET_BG_COLOR_DARK_GREY + "\n");
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
            for (int j = CHESS_BOARD_SIZE; j > 0; j--) {
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
            for (int j = 1; j <= CHESS_BOARD_SIZE; j++) {
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
        out.print(SET_TEXT_COLOR_RED);
    }

    private void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_RED);
    }

}
