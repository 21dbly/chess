package chess;

import chess.moves.*;
import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING('K'),
        QUEEN('Q'),
        BISHOP('B'),
        KNIGHT('N'),
        ROOK('R'),
        PAWN('P');

        private final char character;

        PieceType(char character)
        {
            this.character = character;
        }
    }


    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (getPieceType()) {
            case KING -> KingMoveCalculator.calculateMoves(board, myPosition);
            case QUEEN -> QueenMoveCalculator.calculateMoves(board, myPosition);
            case BISHOP -> BishopMoveCalculator.calculateMoves(board, myPosition);
            case KNIGHT -> KnightMoveCalculator.calculateMoves(board, myPosition);
            case ROOK -> RookMoveCalculator.calculateMoves(board, myPosition);
            case PAWN -> PawnMoveCalculator.calculateMoves(board, myPosition);
        };
    }

    @Override
    public String toString() {
        if (pieceColor == ChessGame.TeamColor.WHITE)
            return String.valueOf(Character.toUpperCase(type.character));
        else
            return String.valueOf(Character.toLowerCase(type.character));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
