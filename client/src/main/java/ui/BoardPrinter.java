package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class BoardPrinter {

    private static final String BORDER_STYLE = SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK;
    private static final String WHITE_BACKGROUND = SET_BG_COLOR_WHITE;
    private static final String BLACK_BACKGROUND = SET_BG_COLOR_BLACK;
    private static final String WHITE_PIECE = SET_TEXT_COLOR_RED;
    private static final String BLACK_PIECE = SET_TEXT_COLOR_BLUE;

    public static String boardPrintString(ChessBoard board, String color) {
        if (!(color.equals("WHITE") || color.equals("BLACK"))) {
            throw new RuntimeException("color must be WHITE or BLACK");
        }
        ChessGame.TeamColor teamColor = color.equals("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
        return boardPrintString(board, teamColor);
    }

    public static String boardPrintString(ChessBoard board, ChessGame.TeamColor color) {
        StringBuilder retString = new StringBuilder();

        int startRow =  9;
        int incrementRow = -1;
        int startCol = 0;
        int incrementCol = 1;
        if (color.equals(ChessGame.TeamColor.BLACK)) {
            startRow = 0;
            incrementRow = 1;
            startCol = 9;
            incrementCol = -1;
        }

        for (int r = startRow; r >= 0 && r <= 9; r += incrementRow) {
            for (int c = startCol; c >= 0 && c <= 9; c += incrementCol) {
                retString.append(getStringFromCoords(r, c, board));
            }
            retString.append(RESET_TEXT+"\n");
        }
        return retString.toString();
    }

    private static String getStringFromCoords(int r, int c, ChessBoard board) {
        if (r == 0 || r == 9 || c == 0 || c == 9) {
            return getBorderText(r, c);
        } else {
            return getSquareText(r, c, board);
        }
    }

    private static String getBorderText(int r, int c) {
        if (r == 0 || r == 9) {
            return BORDER_STYLE + " "+numToLetter(c)+" ";
        } else {
            return BORDER_STYLE + " "+ r +" ";
        }
    }

    private static String getSquareText(int r, int c, ChessBoard board) {
        String background = ((r + c) % 2 == 1) ? WHITE_BACKGROUND : BLACK_BACKGROUND;
        ChessPiece piece = board.getPiece(new ChessPosition(r, c));
        if (piece == null) {
            return background + "   ";
        } else if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return background + WHITE_PIECE + " "+piece.toString().toUpperCase()+" ";
        } else {
            return background + BLACK_PIECE + " "+piece.toString().toUpperCase()+" ";
        }
    }

    private static String numToLetter(int n) {
        return switch (n) {
            case 1 -> "a";
            case 2 -> "b";
            case 3 -> "c";
            case 4 -> "d";
            case 5 -> "e";
            case 6 -> "f";
            case 7 -> "g";
            case 8 -> "h";
            default -> " ";
        };
    }


}
