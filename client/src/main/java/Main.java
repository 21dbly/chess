import chess.ChessBoard;
import exceptions.ResponseException;
import model.GameData;
import model.UserData;
import serverfacade.ServerFacade;
import ui.BoardPrinter;

import java.util.List;
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
    private static List<GameData> gamesList;

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

        if (loggedIn) {
            try {serverFacade.logout(authToken);
            } catch (ResponseException e) {}
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
            case "logout":
            case "lo":
                logout();
                break;
            case "create":
            case "c":
                createGame(args);
                break;
            case "list":
            case "li":
                listGames();
                break;
            case "join":
            case "j":
                joinGame(args);
                break;
            case "observe":
            case "o":
                observeGame(args);
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

    private static void usernameTakenError() {
        System.out.println(ERROR_TEXT+ """
                That username is already taken.""");
    }

    private static void unauthorizedError() {
        System.out.println(ERROR_TEXT+ """
                Your authorization has expired, you will be logged out.""");
        loggedIn = false;
        authToken = null;
    }

    private static void gameDoesNotExistError() {
        System.out.println(ERROR_TEXT+ """
                That game does not exist.""");
    }

    private static void gameTakenError() {
        System.out.println(ERROR_TEXT+ """
                Someone is already playing that color in that game.""");
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
                    usernameTakenError();
                    break;
                default:
                    unknownError(e.code());
            }
        }
    }

    private static void logout() {
        try {
            serverFacade.logout(authToken);
            loggedIn = false;
            authToken = null;
            System.out.println(RESET_TEXT+"Success! You are now logged out.");
        } catch (ResponseException e) {
            switch (e.code()) {
                case 500:
                    serverError();
                    break;
                case 401:
                    System.out.println(RESET_TEXT+"Somehow you were not logged in.");
                    loggedIn = false;
                    authToken = null;
                    break;
                default:
                    unknownError(e.code());
            }
        }
    }

    private static boolean createGameVerify(String[] args) {
        if (args.length < 2) {
            System.out.println(ERROR_TEXT + """
                    Usage:
                    create <GAME_NAME>
                    """);
            return false;
        }
        return true;
    }

    private static void createGame(String[] args) {
        if (!createGameVerify(args)) {
            return;
        }
        String name = args[1];

        try {
            serverFacade.createGame(authToken, name);
            System.out.println(RESET_TEXT+"Success! Your game was created. Type 'list' to see all games.");
        } catch (ResponseException e) {
            switch (e.code()) {
                case 500:
                    serverError();
                    break;
                case 401:
                    unauthorizedError();
                    break;
                default:
                    unknownError(e.code());
            }
        }
    }

    private static void listGames() {
        try {
            gamesList = serverFacade.listGames(authToken).stream().toList();
            int iSize = String.valueOf(gamesList.size()).length();
            System.out.print(RESET_TEXT);
            for (int i = 0; i < gamesList.size(); i++) {
                System.out.println(getGameListString(i+1, gamesList.get(i), iSize, 20, 20, 20));
            }

        } catch (ResponseException e) {
            switch (e.code()) {
                case 500:
                    serverError();
                    break;
                case 401:
                    unauthorizedError();
                    break;
                default:
                    unknownError(e.code());
            }
        }
    }

    private static String getGameListString
            (int i, GameData game, int iSize, int nameSize, int whiteSize, int blackSize) {
        String iString = String.valueOf(i);
        if (iString.length() > iSize) {
            int diff = iString.length() - iSize;
            iString = iString.substring(diff);
        } else {
            iString = String.format("%"+iSize+"s", iString);
        }

        String nameString = shrinkOrPad(game.gameName(), nameSize);
        String whiteName = game.whiteUsername();
        if (whiteName == null) {whiteName = "";}
        String whiteString = SET_BG_COLOR_WHITE+SET_TEXT_COLOR_DARK_GREY+
                shrinkOrPad(whiteName, whiteSize)+RESET_TEXT;
        String blackName = game.blackUsername();
        if (blackName == null) {blackName = "";}
        String blackString = SET_BG_COLOR_BLACK+SET_TEXT_COLOR_LIGHT_GREY+
                shrinkOrPad(blackName, blackSize)+RESET_TEXT;

        return iString+": "+nameString+" - "+whiteString+" - "+blackString;
    }

    private static String shrinkOrPad(String string, int size) {
        if (string.length() > size) {
            return string.substring(0, size-3) + "...";
        } else {
            int padding = size - string.length();
            int leftPadding = padding / 2;
            int rightPadding = padding - leftPadding;
            return " ".repeat(leftPadding) + string + " ".repeat(rightPadding);
        }
    }

    private static void joinGameHelp() {
        System.out.println(ERROR_TEXT + """
                    Usage:
                    join <GAME_NUMBER> [WHITE|BLACK]
                    """);
    }

    private static boolean joinGameVerify(String[] args) {
        if (args.length < 3) {
            joinGameHelp();
            return false;
        }
        if (args[2].equalsIgnoreCase("WHITE") || (args[2].equalsIgnoreCase("W"))) {
            args[2] = "WHITE";
        } else if (args[2].equalsIgnoreCase("BLACK") || (args[2].equalsIgnoreCase("B"))) {
            args[2] = "BLACK";
        } else {
            joinGameHelp();
            return false;
        }

        try {
            Integer.parseInt(args[1]);
        } catch (Exception e) {
            joinGameHelp();
            return false;
        }

        return true;
    }

    private static void joinGame(String[] args) {
        if (!joinGameVerify(args)) {
            return;
        }
        int gameNumber = Integer.parseInt(args[1]);
        if (gamesList == null) {
            System.out.println(ERROR_TEXT+"You must list games before you can join one");
            return;
        }
        if (gameNumber > gamesList.size() || gameNumber < 0) {
            gameDoesNotExistError();
            return;
        }
        String playerColor = args[2];
        GameData game = gamesList.get(gameNumber-1);
        int gameID = game.gameID();
        String gameName = game.gameName();

        try {
            serverFacade.joinGame(authToken, playerColor, gameID);
            System.out.println(RESET_TEXT+"Success! You joined game '"+gameName+"'.");
            printBoard(game.game().getBoard(), playerColor);
        } catch (ResponseException e) {
            switch (e.code()) {
                case 500:
                    serverError();
                    break;
                case 400:
                    gameDoesNotExistError();
                    break;
                case 401:
                    unauthorizedError();
                    break;
                case 403:
                    gameTakenError();
                    break;
                default:
                    unknownError(e.code());
            }
        }
    }

    private static boolean observeGameVerify(String[] args) {
        if (args.length < 2) {
            System.out.println(ERROR_TEXT + """
                    Usage:
                    observe <GAME_NUMBER>
                    """);
            return false;
        }

        try {
            Integer.parseInt(args[1]);
        } catch (Exception e) {
            joinGameHelp();
            return false;
        }

        return true;
    }

    private static void observeGame(String[] args) {
        if (!observeGameVerify(args)) {
            return;
        }
        int gameNumber = Integer.parseInt(args[1]);
        if (gamesList == null) {
            System.out.println(ERROR_TEXT+"You must list games before you can join one");
            return;
        }
        if (gameNumber > gamesList.size() || gameNumber < 0) {
            gameDoesNotExistError();
            return;
        }
        GameData game = gamesList.get(gameNumber-1);
        int gameID = game.gameID();
        String gameName = game.gameName();

        System.out.println(RESET_TEXT+"Success! You're watching game '"+gameName+"'.");
        printBoard(game.game().getBoard(), "WHITE");
    }

    private static void printBoard(ChessBoard board, String playerColor) {
        System.out.println(BoardPrinter.boardPrintString(board, playerColor));
    }
}