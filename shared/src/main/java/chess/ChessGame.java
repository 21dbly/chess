package chess;

import jdk.jshell.spi.ExecutionControl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor turn;
    private ChessBoard board;

    public ChessGame() {
        turn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     * taking into account checks but not team turn
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) return validMoves;

        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        for (ChessMove move : moves) {
            ChessBoard boardCopy = new ChessBoard(board);
            boardCopy.movePiece(move);
            if (!isInCheck(piece.getTeamColor(), boardCopy))
                validMoves.add(move);
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game (if move is valid and it's the correct team turn)
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null)
            throw new InvalidMoveException("No Piece");
        if (piece.getTeamColor() != turn)
            throw new InvalidMoveException("Out of turn");
        if (!validMoves(move.getStartPosition()).contains(move))
            throw new InvalidMoveException("Invalid move");
        board.movePiece(move);
        switchTurn();
    }

    private void switchTurn() {
        turn = turn == TeamColor.WHITE? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheck(teamColor, board);
    }

    private static boolean isInCheck(TeamColor teamColor, ChessBoard board) {
        ChessPosition kingPos = findKingPosition(teamColor, board);
        for (ChessPosition position : board) {
            ChessPiece piece = board.getPiece(position);
            if (piece == null) continue;
            if (piece.getTeamColor() == teamColor) continue;
            var moves = piece.pieceMoves(board, position);
            if (moves.stream().anyMatch(move -> move.getEndPosition().equals(kingPos)))
                return true;
        }
        return false;
    }

    private static ChessPosition findKingPosition(TeamColor teamColor, ChessBoard board) {
        for (ChessPosition position : board) {
            ChessPiece piece = board.getPiece(position);
            if (piece == null) continue;
            if (piece.getPieceType() == ChessPiece.PieceType.KING &&
                    piece.getTeamColor() == teamColor) {
                return position;
            }
        }
        throw new RuntimeException("No " + teamColor.name() + " King found");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (teamColor != turn) return false; // can't be in checkmate if not your turn
        return isInCheck(teamColor) && !hasAvailableMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (teamColor != turn) return false; // can't be in stalemate if not your turn?
        return !isInCheck(teamColor) && !hasAvailableMoves(teamColor);
    }

    /**
     * Checks if the active team has moves available
     *
     * @return True if the active team has moves, otherwise false
     */
    private boolean hasAvailableMoves(TeamColor teamColor) {
        for (ChessPosition position : board) {
            ChessPiece piece = board.getPiece(position);
            if (piece == null) continue;
            if (piece.getTeamColor() != teamColor) continue;
            if (!validMoves(position).isEmpty())
                return true;
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, board);
    }
}
