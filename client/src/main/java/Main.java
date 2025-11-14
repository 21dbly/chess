import chess.*;

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

    public static void run() {
        System.out.println("♕ Welcome to 240 chess. Type Help to see the options. ♕");
        loggedIn = false;
        Scanner scanner = new Scanner(System.in);
        String input = getInput(scanner);
        switch (input.toLowerCase()) {
            case "help":
            case "h":
                help();
                break;
            default:
                System.out.println(ERROR_TEXT+"Invalid input. Here are your options:");
                help();
                break;
        }
    }

    private static String getInput(Scanner scanner) {
        System.out.print(getPrompt() + " >>> " + INPUT_TEXT);
        return scanner.nextLine();
    }

    private static String getPrompt () {
        return "\n" + RESET_TEXT + (loggedIn ? "[LOGGED_IN]" : "[LOGGED_OUT]");
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
        System.out.println(loggedIn ? LOGGED_IN_HELP : LOGGED_OUT_HELP);
    }
}