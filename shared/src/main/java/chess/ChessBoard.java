package chess;

import java.util.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Iterable<ChessPosition> {
    final private ChessPiece[][] board = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    public ChessBoard(ChessBoard other) {
        for (ChessPosition position : this) {
            ChessPiece otherPiece = other.getPiece(position);
            if (otherPiece != null)
                addPiece(position, new ChessPiece(otherPiece));
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Moves a piece according to move
     *
     * @param move includes the start and end positions
     */
    public void movePiece(ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece.PieceType promotion = move.getPromotionPiece();
        ChessPiece piece = getPiece(start);

        // move for en passant
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN &&
                start.getColumn() != end.getColumn() &&
                getPiece(end) == null)
        {
            // capture en passanted pawn
            addPiece(new ChessPosition(start.getRow(), end.getColumn()), null);
        }

        addPiece(end, piece);
        addPiece(start, null);

        piece.setMoved();

        if (promotion != null) piece.promote(promotion);

        // move for castling
        // R...K..R
        // ..KR.RK.
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            int horizMovement = end.getColumn() - start.getColumn();
            if (horizMovement == 2)
                movePiece(new ChessMove(start.plus(0,3), start.plus(0, 1)));
            else if (horizMovement == -2)
                movePiece(new ChessMove(start.plus(0,-4), start.plus(0, -1)));
        }
    }

    private void addPieceFromChar(ChessPosition position, Character letter) {
        ChessGame.TeamColor color = Character.isUpperCase(letter) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
        letter = Character.toUpperCase(letter);
        ChessPiece.PieceType type = switch (letter) {
            case 'P' -> ChessPiece.PieceType.PAWN;
            case 'R' -> ChessPiece.PieceType.ROOK;
            case 'N' -> ChessPiece.PieceType.KNIGHT;
            case 'B' -> ChessPiece.PieceType.BISHOP;
            case 'Q' -> ChessPiece.PieceType.QUEEN;
            case 'K' -> ChessPiece.PieceType.KING;
            default -> null;
        };
        ChessPiece piece = type == null ? null : new ChessPiece(color, type);
        addPiece(position, piece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    public boolean inBounds(ChessPosition position) {
        int col = position.getColumn();
        int row = position.getRow();
        return 0 < col && 0 < row && row <= 8 && col <= 8;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        setBoard("""
                rnbqkbnr
                pppppppp
                ........
                ........
                ........
                ........
                PPPPPPPP
                RNBQKBNR
                """);
    }

    private void setBoard(String boardString) {
        var rows = boardString.split("\n");
        if (rows.length != 8) throw new RuntimeException("boardString has wrong number of rows");
        int r = 8;
        for (String row : rows) {
            var splitRow = row.toCharArray();
            if (splitRow.length != 8) throw new RuntimeException("a boardString row has wrong number of columns");
            int c = 1;
            for (Character letter : splitRow) {
                addPieceFromChar(new ChessPosition(r, c), letter);
                c++;
            }
            r--;
        }
    }

    public ChessPosition findKingPosition(ChessGame.TeamColor teamColor) {
        var positions = findPieces(ChessPiece.PieceType.KING, teamColor);
        if (positions.isEmpty())
            throw new RuntimeException("No " + teamColor.name() + " King found");
        if (positions.size() > 1)
            throw new RuntimeException("Multiple " + teamColor.name() + " Kings found");
        return positions.iterator().next(); // get first (and only) position
    }

    private Collection<ChessPosition> findPieces(ChessPiece.PieceType pieceType, ChessGame.TeamColor color) {
        ArrayList<ChessPosition> list = new ArrayList<>();
        for (ChessPosition position : this) {
            ChessPiece piece = getPiece(position);
            if (piece == null) continue;
            if (piece.getPieceType() == pieceType && piece.getTeamColor() == color) {
                list.add(position);
            }
        }
        return list;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public Iterator<ChessPosition> iterator() {
        return new ChessBoardIterator();
    }

    private static class ChessBoardIterator implements Iterator<ChessPosition> {
        int row;
        int col;
        public ChessBoardIterator() {
            row = 1;
            col = 1;
        }

        @Override
        public boolean hasNext() {
            return row <= 8 && col <= 8;
        }

        @Override
        public ChessPosition next() {
            ChessPosition position = new ChessPosition(row, col);
            if (col >= 8) {
                col = 1;
                row++;
            } else {
                col++;
            }
            return position;
        }
    }

    @Override
    public String toString() {
        String[] rows = new String[8];
        Arrays.fill(rows, "");
        for (var position : this)
        {
            ChessPiece piece = getPiece(position);
            String pieceString = piece == null ? "." : piece.toString();
            rows[position.getRow() - 1] += pieceString;
        }
        StringBuilder retStr = new StringBuilder();
        for (int i = 7; i >= 0; i--)
        {
            retStr.append(rows[i]).append("\n");
        }
        return retStr.toString();
    }
}
