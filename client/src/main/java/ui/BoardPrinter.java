package ui;

import chess.ChessBoard;
import static ui.EscapeSequences.*;

public class BoardPrinter {

    private static final String BORDER_STYLE = SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK;

    public static String BoardPrintString(ChessBoard board, String color) {
        StringBuilder retString = new StringBuilder();

        if (!(color.equals("WHITE") || color.equals("BLACK"))) {
            throw new RuntimeException("color must be WHITE or BLACK");
        }

        int startRow = color.equals("WHITE") ? 9: 0;
        int incrementRow = color.equals("WHITE") ? -1: 1;
        int startCol = color.equals("WHITE") ? 0: 9;
        int incrementCol = color.equals("WHITE") ? 1: -1;

        for (int r = startRow; r >= 0 && r <= 9; r += incrementRow) {
            for (int c = startCol; c >= 0 && c <= 9; c += incrementCol) {
                retString.append(getStringFromCoords(r, c));
            }
            retString.append(RESET_TEXT+"\n");
        }
        return retString.toString();
    }

    private static String getStringFromCoords(int r, int c) {
        if (r == 0 || r == 9 || c == 0 || c == 9) {
            return getBorderText(r, c);
        } else {
            return RESET_TEXT + "   ";
        }
    }

    private static String getBorderText(int r, int c) {
        if (r == 0 || r == 9) {
            return BORDER_STYLE + " "+numToLetter(c)+" ";
        } else {
            return BORDER_STYLE + " "+String.valueOf(r)+" ";
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
