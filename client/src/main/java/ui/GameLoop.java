package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import exceptions.ResponseException;
import model.GameData;
import serverfacade.WebSocketFacade;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameLoop implements ServerMessageObserver {

    private final Scanner scanner;
    private GameData gameData;
    private ChessGame.TeamColor playerColor;
    private final WebSocketFacade ws;

    public GameLoop(Scanner scanner, String serverUrl) throws ResponseException {
        this.scanner = scanner;
        ws = new WebSocketFacade(serverUrl, this);
    }

    public void joinGame(GameData data, ChessGame.TeamColor color, String authToken) {
        ws.connect(data.gameID(), authToken);
        gameData = data;
        playerColor = color;
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
        var board = gameData.game().getBoard();
        String boardString = BoardPrinter.getString(board, playerColor);
        System.out.println(boardString);
    }

    private void leave() {
    }

    private void move() {
    }

    private void resign() {
    }

    private void hint() {
        System.out.print(RESET_TEXT+"Enter a piece position: ");
        String input = scanner.nextLine();
        ChessPosition pos = ChessPosition.parse(input);
        if (pos == null) {
            System.out.println(ERROR_TEXT+ "That is not a valid position");
            return;
        }
        ChessBoard board = gameData.game().getBoard();
        ChessPiece piece = board.getPiece(pos);
        if (piece == null) {
            System.out.println(ERROR_TEXT+ "There is no piece at that position");
            return;
        }
        var moves = piece.pieceMoves(board, pos);

        String boardString = BoardPrinter.getString(board, playerColor, pos, moves);
        System.out.println(boardString);
    }

    @Override
    public void notify(ServerMessage message) {

    }
}
