package ui;

import chess.ChessGame;
import model.*;
import ui.EscapeSequences;
import client.ResponseException;
import client.ServerFacade;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient {
    private final ServerFacade server;
    private String auth;
    private State state;

    private enum State {LOGGED_IN, LOGGED_OUT}

    public ChessClient(int port) {
        server = new ServerFacade(port);
        state = State.LOGGED_OUT;
    }

    public void run() {
        System.out.println(WHITE_KING + "Welcome to Chess. For options, type help" + WHITE_KING);
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quitting chess...")) {
            System.out.print(SET_TEXT_COLOR_BLUE + ">>> " + RESET_TEXT_COLOR);
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.println(result);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void printPrompt() {
        if (state.equals(State.LOGGED_OUT)) {
            System.out.println();
        }
    }

    private String eval(String line) {
        String[] tokens = line.toLowerCase().split(" ");
        String command = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (command) {
            case "register" -> register(params);
            case "quit" -> "quitting chess...";
            case "login" -> login(params);
            case "create" -> createGame(params);
            case "join" -> joinGame(params);
            case "help" -> help();
            default -> command + " is not a valid command. Possible commands:\n" + help();
        };
    }

    private String joinGame(String[] params) {
        if (state.equals(State.LOGGED_IN)) {
            if (params.length == 2) {
                int gameID = Integer.parseInt(params[0]);
                ChessGame.TeamColor color;
                switch (params[1]) {
                    case "black" -> color = ChessGame.TeamColor.BLACK;
                    case "white" -> color = ChessGame.TeamColor.WHITE;
                    default -> throw new ResponseException("invalid color", 400);
                };
                server.joinGame(new JoinGameRequest(auth, color, gameID));
                return printBoard(gameID, color);
            }
            throw new ResponseException("usage: join <GAME_ID> <WHITE|BLACK>", 400);
        }
        throw new ResponseException("Please log in :)\n for options, type help", 400);
    }

    private String createGame(String... params) {
        if (state.equals(State.LOGGED_IN)) {
            if (params.length == 1) {
                CreateGameResult result = server.createGame(new CreateGameRequest(auth, params[0]));
                return String.format("Success! %s has id: %d", params[0], result.gameID());
            }
            throw new ResponseException("usage: create <GAME_NAME>", 400);
        }
        throw new ResponseException("Please log in :)\n for options, type help", 400);
    }

    private String register(String... params) {
        if (state.equals(State.LOGGED_OUT)) {
            if (params.length == 3) {
                try {
                    RegisterResult result = server.register(new RegisterRequest(params[0], params[1], params[2]));
                    state = State.LOGGED_IN;
                    auth = result.authToken();
                    return String.format("%s logged in", result.username());
                } catch (ResponseException ex) {
                    return "Username taken. Please choose a new username";
                }
            }
            throw new ResponseException("usage: register <USERNAME> <PASSWORD> <EMAIL>", 400);
        }
        throw new ResponseException("You are already logged in :)\n for options, type help", 400);
    }

    private String login(String... params) {
        if (state.equals(State.LOGGED_OUT)) {
            if (params.length == 2) {
                try {
                    LoginResult result = server.login(new LoginRequest(params[0], params[1]));
                    state = State.LOGGED_IN;
                    auth = result.authToken();
                    return String.format("%s logged in", result.username());
                } catch (ResponseException ex) {
                    throw new ResponseException("Invalid username and/or password. Please try again.", 401);
                }
            }
            throw new ResponseException("usage: <USERNAME> <PASSWORD>", 400);
        }
        throw new ResponseException("You are already logged in :)\nFor options, type help", 400);
    }

    private String help() {
        if (state.equals(State.LOGGED_OUT)) {
            return """
                    >>> register <USERNAME> <PASSWORD> <EMAIL>
                    >>> login <USERNAME> <PASSWORD>
                    >>> quit - quit chess
                    >>> help - possible commands
                    """;
        } else {
            return """
                    >>> create <GAME_NAME> - create game
                    >>> list - list all games
                    >>> join <ID> <WHITE|BLACK> - join game as player
                    >>> observe <ID> - join game as observer
                    >>> logout - logout user
                    >>> quit - quit chess
                    >>> help - possible commands
                    """;
        }
    }

    private StringBuilder printBoard(int gameID, ChessGame.TeamColor color) {
        StringBuilder boardString = new StringBuilder();

        boardString.append("a b c d e f g");
        return boardString;
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
