package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameLoop {

    private final Scanner scanner;

    public GameLoop(Scanner scanner) {
        this.scanner = scanner;
    }

    public void joinGame(Scanner scanner) {
        // websocket connection
        loop();
    }

    public void observeGame() {
        // websocket connection
    }

    private static String getInput(Scanner scanner) {
        System.out.print(RESET_TEXT+"[IN_GAME] >>> " + INPUT_TEXT);
        return scanner.nextLine();
    }

    private void loop() {
        boolean exit = false;
        while (!exit) {
            String input = getInput(scanner);
            exit = executeCommand(input.toLowerCase());
        }
    }

    private boolean executeCommand(String command) {
        switch (command) {
            case "help":
                help();
                break;
            case "redraw":
                redraw();
                break;
            case "leave":
                leave();
                return true;
            case "move":
                move();
                break;
            case "resign":
                resign();
                break;
            case "hint":
                hint();
                break;
            default:
                help();
                break;
        }
        return false;
    }

    private void help() {
        System.out.println(HELP_TEXT+ """
                redraw - draw current state of chessboard
                leave - leave the game
                move - make a move
                resign - forfeit the game
                hint - highlight legal available moves
                help - show possible commands
                """);
    }

    private void redraw() {
    }

    private void leave() {
    }

    private void move() {
    }

    private void resign() {
    }

    private void hint() {
    }
}
