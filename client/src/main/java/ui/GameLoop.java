package ui;

import chess.*;
import exceptions.ResponseException;
import model.GameData;
import serverfacade.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameLoop implements ServerMessageObserver {

    private final Scanner scanner;
    private ChessGame game;
    private int gameID;
    private ChessGame.TeamColor playerColor;
    private final WebSocketFacade ws;
    private String authToken;

    public GameLoop(Scanner scanner, String serverUrl) throws ResponseException {
        this.scanner = scanner;
        ws = new WebSocketFacade(serverUrl, this);
    }

    public void joinGame(GameData data, ChessGame.TeamColor color, String authToken) {
        this.authToken = authToken;
        ws.connect(data.gameID(), authToken);
        game = data.game();
        gameID = data.gameID();
        playerColor = color;
        loop();
    }

    public void observeGame(GameData data, String authToken) {
        this.authToken = authToken;
        ws.connect(data.gameID(), authToken);
        game = data.game();
        gameID = data.gameID();
        playerColor = ChessGame.TeamColor.WHITE;
        observeLoop();
    }

    private static String getInput(Scanner scanner, String state) {
        System.out.print(RESET_TEXT+"["+state+"] >>> " + INPUT_TEXT);
        return scanner.nextLine();
    }

    private void loop() {
        boolean exit = false;
        while (!exit) {
            String input = getInput(scanner, "IN_GAME");
            exit = executeCommand(input.toLowerCase());
        }
    }

    private void observeLoop() {
        boolean exit = false;
        while (!exit) {
            String input = getInput(scanner, "OBSERVING");
            exit = executeObserveCommand(input.toLowerCase());
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

    private boolean executeObserveCommand(String command) {
        switch (command) {
            case "help":
                observeHelp();
                break;
            case "redraw":
                redraw();
                break;
            case "leave":
                leave();
                return true;
            case "hint":
                hint();
                break;
            default:
                observeHelp();
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

    private void observeHelp() {
        System.out.println(HELP_TEXT+ """
                redraw - draw current state of chessboard
                leave - leave the game
                hint - highlight legal available moves
                help - show possible commands
                """);
    }

    private void redraw() {
        var board = game.getBoard();
        String boardString = BoardPrinter.getString(board, playerColor);
        System.out.println(boardString);
    }

    private void leave() {
        ws.leave(gameID, authToken);
    }

    private void move() {
        System.out.print(RESET_TEXT+"Enter a move (for example e2 e4): ");
        String input = scanner.nextLine();
        ChessMove move = ChessMove.parse(input);
        if (move == null) {
            System.out.println(ERROR_TEXT+"Please type a move of the form 'e2 e4' or like 'e7 e8 queen' for promotions");
            return;
        }

        ws.makeMove(gameID, authToken, move);
    }

    private void resign() {
        System.out.println(RESET_TEXT+ "Are you sure you want to resign? (y/n)");
        if (!scanner.nextLine().equalsIgnoreCase("y")) {
            return;
        }
        ws.resign(gameID, authToken);
    }

    private void hint() {
        System.out.print(RESET_TEXT+"Enter a piece position: ");
        String input = scanner.nextLine();
        ChessPosition pos = ChessPosition.parse(input);
        if (pos == null) {
            System.out.println(ERROR_TEXT+ "That is not a valid position");
            return;
        }
        ChessBoard board = game.getBoard();
        var moves = game.validMoves(pos);

        String boardString = BoardPrinter.getString(board, playerColor, pos, moves);
        System.out.println(boardString);
    }

    @Override
    public void notify(ServerMessage message) {
        String messageStr;
        switch (message.getServerMessageType()) {
            case NOTIFICATION:
                messageStr = ((NotificationMessage) message).getMessage();
                System.out.println(RESET_TEXT+ messageStr);
                break;
            case ERROR:
                messageStr = ((ErrorMessage) message).getMessage();
                System.out.println(ERROR_TEXT+ messageStr);
                break;
            case LOAD_GAME:
                ChessGame game = ((LoadGameMessage) message).getGame();
                this.game = game;
                ChessBoard board = game.getBoard();
                System.out.println('\n'+BoardPrinter.getString(board, playerColor));
                break;
        }
    }
}
