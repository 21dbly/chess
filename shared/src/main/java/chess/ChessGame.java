package chess;

import chess.moves.KingMoveCalculator;
import com.google.gson.Gson;

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
    private ChessMove prevMove;
    private TeamColor winner;
    private boolean gameOver;

    public ChessGame() {
        turn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
        gameOver = false;
    }

    public TeamColor getWinner() {
        return winner;
    }

    public boolean isOver() {
        return gameOver;
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
        BLACK;

        public TeamColor opposite() {
            return this.equals(WHITE) ? BLACK : WHITE;
        }
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

        if (piece == null) { return validMoves; }

        ChessBoard enPassantReadyBoard = getEnPassantReadyBoard(startPosition);

        Collection<ChessMove> moves = piece.pieceMoves(enPassantReadyBoard, startPosition);
        for (ChessMove move : moves) {
            ChessBoard boardCopy = new ChessBoard(board);
            boardCopy.movePiece(move);
            if (!isInCheck(piece.getTeamColor(), boardCopy)) {
                validMoves.add(move);
            }
        }

        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            var castleMoves = KingMoveCalculator.calculateCastleMoves(board, startPosition);
            for (var move : castleMoves) {
                if (validCastle(move)) {
                    validMoves.add(move);
                }
            }
        }

        return validMoves;
    }

    /**
     * Checks if a castle would go through check, out of check, or into check
     *
     * @param move castling move to check
     * @return true if it's valid, false if there's a check
     */
    private boolean validCastle(ChessMove move)
    {
        ChessPosition start = move.getStartPosition();
        TeamColor color = board.getPiece(start).getTeamColor();
        ChessPosition end = move.getEndPosition();
        int direction = Integer.signum(end.getColumn() - start.getColumn());

        ChessPosition pos = start;
        while (true)
        {
            ChessBoard boardCopy = new ChessBoard(board);
            if (!pos.equals(start)) { boardCopy.movePiece(new ChessMove(start, pos)); }
            if (isInCheck(color, boardCopy)) {
                return false;
            }
            if (pos.equals(end)) { break; } // break after checking for check at end
            pos = pos.plus(0, direction);
        }
        return true;
    }

    /**
     * if startPosition has a pawn and prevMove was moving a pawn 2 in a different column,
     * this returns a new board with state as if the last pawn had only moved 1
     *
     * @param startPosition the position the piece is in
     * @return a new board copy if an enPassant could happen, or the old board
     */
    private ChessBoard getEnPassantReadyBoard(ChessPosition startPosition)
    {
        if (prevMove == null) {
            return board; // first move can't be en passant
        }
        if (board.getPiece(startPosition).getPieceType() != ChessPiece.PieceType.PAWN) {
            return board; // current piece has to be a pawn for this to matter
        }
        if (board.getPiece(prevMove.getEndPosition()).getPieceType() != ChessPiece.PieceType.PAWN) {
            return board; // previous moved piece too
        }

        int prevMoveStartRow = prevMove.getStartPosition().getRow();
        int prevMoveEndRow = prevMove.getEndPosition().getRow();
        if (Math.abs(prevMoveEndRow - prevMoveStartRow) != 2) {
            return board; // prev pawn has to have moved 2
        }

        int prevMoveCol = prevMove.getEndPosition().getColumn();
        if (prevMoveCol == startPosition.getColumn()) {
            return board; // wouldn't work if attacking pawn is in the same col
        }
        int inBetweenRow = (prevMoveStartRow + prevMoveEndRow) / 2;

        ChessBoard copy = new ChessBoard(board);
        // move pawn back 1
        copy.movePiece(new ChessMove(prevMove.getEndPosition(), new ChessPosition(inBetweenRow, prevMoveCol)));

        return copy;
    }

    /**
     * Makes a move in a chess game (if move is valid and it's the correct team turn)
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (isOver()) {
            throw new InvalidMoveException("Game is over");
        }
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException("No Piece");
        }
        if (piece.getTeamColor() != turn) {
            throw new InvalidMoveException("Out of turn");
        }
        if (!validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException("Invalid move");
        }
        board.movePiece(move);
        switchTurn();
        prevMove = move;

        if (isInCheckmate(turn)) {
            winner = turn.opposite();
            gameOver = true;
        }
        else if (isInStalemate(turn)) {
            winner = null;
            gameOver = true;
        }
    }

    public void resign(TeamColor team) {
        if (!isOver()) {
            gameOver = true;
            winner = team.opposite();
        }
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
        ChessPosition kingPos = board.findKingPosition(teamColor);
        for (ChessPosition position : board) {
            ChessPiece piece = board.getPiece(position);
            if (piece == null) { continue; }
            if (piece.getTeamColor() == teamColor) { continue; }
            var moves = piece.pieceMoves(board, position);
            if (moves.stream().anyMatch(move -> move.getEndPosition().equals(kingPos))) {
                return true;
            }
        }
        return false;
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (teamColor != turn) {return false;} // can't be in checkmate if not your turn
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
        if (teamColor != turn) {return false;} // can't be in stalemate if not your turn?
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
            if (piece == null) {continue;}
            if (piece.getTeamColor() != teamColor) {continue;}
            if (!validMoves(position).isEmpty()) {
                return true;
            }
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

    public String serialize() {
        return new Gson().toJson(this);
    }

    public static ChessGame deserialize(String str) {
        return new Gson().fromJson(str, ChessGame.class);
    }
}
