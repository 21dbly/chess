import chess.*;
import exceptions.ResponseException;
import model.UserData;
import serverfacade.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Main {
    public static void main(String[] args) {
        run();
    }

    private static final String INPUT_TEXT = SET_TEXT_COLOR_GREEN;
    private static final String HELP_TEXT = SET_TEXT_COLOR_MAGENTA;
    private static final String ERROR_TEXT = SET_TEXT_COLOR_RED;

    private static Boolean loggedIn;
    private static ServerFacade serverFacade;
    private static String authToken;

    private static void init() {
        loggedIn = false;
        serverFacade = new ServerFacade("http://localhost:8080");
    }

    public static void run() {
        System.out.println("♕ Welcome to 240 chess. Type Help to see the options. ♕");
        init();
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {
            String input = getInput(scanner);
            String[] splinput = input.split(" ");
            String command = "";
            if (splinput.length > 0) {
                command = splinput[0];
            }
            command = command.toLowerCase();

            if (command.equals("help") || command.equals("h")) {
                help();
            } else if (command.equals("quit") || command.equals("q")) {
                exit = true;
                System.out.println(RESET_TEXT+"Goodbye!");
            } else {
                if (!loggedIn) {
                    evaluateLoggedOut(command, splinput);
                } else {
                    evaluateLoggedIn(command, splinput);
                }
            }
        }
    }

    private static String getInput(Scanner scanner) {
        System.out.print(getPrompt() + " >>> " + INPUT_TEXT);
        return scanner.nextLine();
    }

    private static String getPrompt () {
        return RESET_TEXT + (loggedIn ? "[LOGGED_IN]" : "[LOGGED_OUT]");
    }

    private static void evaluateLoggedOut(String command, String[] args) {
        switch (command) {
            case "login":
            case "l":
                login(args);
                break;
            case "register":
            case "r":
                register(args);
                break;
            default:
                System.out.println(ERROR_TEXT+"Invalid input. Here are your options:");
                help();
                break;
        }
    }

    private static void evaluateLoggedIn(String command, String[] args) {
        switch (command) {
            case "login":
            case "l":
                login(args);
                break;
            case "register":
            case "r":
                register(args);
                break;
            default:
                System.out.println(ERROR_TEXT+"Invalid input. Here are your options:");
                help();
                break;
        }
    }

    private static final String LOGGED_OUT_HELP = HELP_TEXT + """
            register <USERNAME> <PASSWORD> <EMAIL> - create an account
            login <USERNAME> <PASSWORD> - to play
            quit
            help - show possible commands
            """;

    private static final String LOGGED_IN_HELP = HELP_TEXT + """
            create <Name> - create a game
            list - list games
            join <NUMBER> [WHITE|BLACK] - join a game
            observe <NUMBER> - observe a game
            logout
            quit
            help - show possible commands
            """;

    private static void help() {
        System.out.print(loggedIn ? LOGGED_IN_HELP : LOGGED_OUT_HELP);
    }

    private static boolean loginVerify(String[] args) {
        if (args.length < 3) {
            System.out.println(ERROR_TEXT + """
                    Usage:
                    login <USERNAME> <PASSWORD>
                    """);
            return false;
        }
        return true;
    }

    private static void login(String[] args) {
        if (!loginVerify(args)) {
            return;
        }
        String username = args[1];
        String password = args[2];

        try {
            var authData = serverFacade.login(username, password);
            loggedIn = true;
            authToken = authData.authToken();
            System.out.println(RESET_TEXT+"Success! You are now logged in.");
        } catch (ResponseException e) {
            switch (e.code()) {
                case 500:
                    serverError();
                    break;
                case 401:
                    invalidAuthenticationError();
                    break;
                default:
                    unknownError(e.code());
            }
        }
    }

    private static void serverError() {
        System.out.println(ERROR_TEXT+ """
                There was a problem with the server or database.""");
    }

    private static void invalidAuthenticationError() {
        System.out.println(ERROR_TEXT+ """
                Your username or password was incorrect.""");
    }

    private static void unknownError(int statusCode) {
        System.out.println(ERROR_TEXT+ "An error occurred (" + statusCode + ").");
    }

    private static void alreadyTakenError() {
        System.out.println(ERROR_TEXT+ """
                That username is already taken.""");
    }

    private static boolean registerVerify(String[] args) {
        if (args.length < 4) {
            System.out.println(ERROR_TEXT + """
                    Usage:
                    register <USERNAME> <PASSWORD> <EMAIL>
                    """);
            return false;
        }
        return true;
    }

    private static void register(String[] args) {
        if (!registerVerify(args)) {
            return;
        }
        String username = args[1];
        String password = args[2];
        String email = args[3];

        try {
            var authData = serverFacade.register(new UserData(username, password, email));
            loggedIn = true;
            authToken = authData.authToken();
            System.out.println(RESET_TEXT+"Success! You are registered and logged in.");
        } catch (ResponseException e) {
            switch (e.code()) {
                case 500:
                    serverError();
                    break;
                case 403:
                    alreadyTakenError();
                    break;
                default:
                    unknownError(e.code());
            }
        }
    }
}