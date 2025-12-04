package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    /**
     * @return a new ChessPosition moved by row, col
     */
    public ChessPosition plus(int row, int col) {
        return new ChessPosition(getRow() + row, getColumn() + col);
    }

    /**
     * @return a ChessPosition parsed from input or null if invalid form
     */
    public static ChessPosition parse(String input) {
        if (input.length() < 2) {
            return null;
        }
        input = input.toLowerCase();
        char c = input.charAt(0); // column is letter, letter comes first
        char r = input.charAt(1);
        if (r == ' ' && input.length() >= 3) { // account for possible space in-between
            r = input.charAt(2);
        }
        if (Character.isDigit(c)) { // allow switched order also
            char temp = c;
            c = r;
            r = temp;
        }
        if (c < 'a' || c > 'h' || r < '1' || r > '8') {
            return null;
        }
        int cInt = c - 'a' + 1;
        int rInt = r - '1' + 1;
        return new ChessPosition(rInt, cInt);
    }

    @Override
    public String toString() {
        return String.format("%c%d", col-1+'a', row);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
