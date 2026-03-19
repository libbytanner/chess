package ui;

import client.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

public class ChessClient {
    private final ServerFacade server;
    private State state;

    private enum State {LOGGED_IN, LOGGED_OUT}

    public ChessClient(int port) {
        server = new ServerFacade(port);
        state = State.LOGGED_OUT;
    }

    public void run() {
        System.out.println("Welcome to Chess. For options, type help");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            result = eval(line);
            System.out.println(result);
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
            default -> help();
        };
    }

    private String register(String... params) {
        return """
                not implemented
                """;
    }

    private String help() {
        if (state.equals(State.LOGGED_OUT)) {
            return """
                    >>> register <USERNAME> <PASSWORD> <EMAIL>
                    >>> login <USERNAME> <PASSWORD>
                    >>> quit
                    >>> help
                    """;
        } else {
            return """
                    
                    """;
        }
    }

}
