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
        System.out.print("[IN_GAME] >>> " + INPUT_TEXT);
        return scanner.nextLine();
    }

    private void loop() {
        boolean exit = false;
        while (!exit) {
            String input = getInput(scanner);
            executeCommand(input.toLowerCase());
        }
    }

    private void executeCommand(String command) {
        switch (command) {
            case "help":
                help();
                break;
            case "redraw":
                redraw();
                break;
            case "leave":
                leave();
                break;
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
    }

    private void help() {
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
