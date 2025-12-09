package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = null;
    }

    public static ChessMove parse(String input) {
        String[] splinput = input.split(" ");
        if (splinput.length < 2) {
            return null;
        }
        var start = ChessPosition.parse(splinput[0]);
        var end = ChessPosition.parse(splinput[1]);
        if (start == null || end == null) {
            return null;
        }
        if (splinput.length == 2) {
            return new ChessMove(start, end);
        }
        ChessPiece.PieceType promotion;
        if (splinput[2].length() == 1) {
            promotion = ChessPiece.typeFromChar(splinput[2].charAt(0));
        } else {
            promotion = ChessPiece.typeFromString(splinput[2]);
        }
        return new ChessMove(start, end, promotion);
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    /**
     * Finds the color of the piece at the start position of this move
     * given a chess board
     *
     * @return color of piece or null if there is no piece at that position
     */
    public ChessGame.TeamColor getMoveColor(ChessBoard board) {
        var piece = board.getPiece(getStartPosition());
        if (piece == null) {
            return null;
        }
        return piece.getTeamColor();
    }

    @Override
    public String toString() {
        String promotionNote = "";
        if (promotionPiece != null) { promotionNote = String.format(" (%s)",promotionPiece); }
        return String.format("%s to %s%s", startPosition, endPosition, promotionNote);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(startPosition, chessMove.startPosition) &&
                Objects.equals(endPosition, chessMove.endPosition) &&
                promotionPiece == chessMove.promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }
}
