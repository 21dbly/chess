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
    private PieceType type;
    private boolean hasMoved;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        this.hasMoved = false;
    }

    public ChessPiece(ChessPiece other) {
        this.pieceColor = other.pieceColor;
        this.type = other.type;
        this.hasMoved = other.hasMoved;
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

    public static PieceType typeFromChar(char c) {
        return switch (Character.toUpperCase(c)) {
            case 'P' -> ChessPiece.PieceType.PAWN;
            case 'R' -> ChessPiece.PieceType.ROOK;
            case 'N' -> ChessPiece.PieceType.KNIGHT;
            case 'B' -> ChessPiece.PieceType.BISHOP;
            case 'Q' -> ChessPiece.PieceType.QUEEN;
            case 'K' -> ChessPiece.PieceType.KING;
            default -> null;
        };
    }

    public static PieceType typeFromString(String s) {
        return switch (s.toLowerCase()) {
            case "pawn" -> ChessPiece.PieceType.PAWN;
            case "rook" -> ChessPiece.PieceType.ROOK;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "queen" -> ChessPiece.PieceType.QUEEN;
            case "king" -> ChessPiece.PieceType.KING;
            default -> null;
        };
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
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessMove previousMove) {
        return switch (getPieceType()) {
            case KING -> KingMoveCalculator.calculateMoves(board, myPosition);
            case QUEEN -> QueenMoveCalculator.calculateMoves(board, myPosition);
            case BISHOP -> BishopMoveCalculator.calculateMoves(board, myPosition);
            case KNIGHT -> KnightMoveCalculator.calculateMoves(board, myPosition);
            case ROOK -> RookMoveCalculator.calculateMoves(board, myPosition);
            case PAWN -> PawnMoveCalculator.calculateMoves(board, myPosition, previousMove);
        };
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return pieceMoves(board, myPosition, null);
    }

    public void promote(PieceType type) {
        this.type = type;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setMoved() {
        hasMoved = true;
    }

    @Override
    public String toString() {
        if (pieceColor == ChessGame.TeamColor.WHITE) {
            return String.valueOf(Character.toUpperCase(type.character));
        } else {
            return String.valueOf(Character.toLowerCase(type.character));
        }
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
