package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMoveCalculator {
    /**
     * Calculates whether the piece can move to this new position, or if it is blocked
     * by a piece or would be off the board
     *
     * @param direction a ChessPosition representing the offset for the new position
     * @return Collection containing one move if valid, or empty if not valid
     */
    protected static Collection<ChessMove> GetMoveInDirection(ChessBoard board, ChessPosition startPosition, ChessPosition direction) {
        Collection<ChessMove> list = new ArrayList<ChessMove>();
        ChessPosition newPosition = startPosition.plus(direction);
        if (canMove(board, startPosition, newPosition) != MoveStatus.CANNOT_MOVE)
            list.add(new ChessMove(startPosition, newPosition));
        return list;
    }

    /**
     * Calculates all the positions a chess piece can move in one direction.
     * Takes into account the board state and the current piece's color and position,
     * but not its type.
     *
     * @param direction a ChessPosition representing the offset for the new position
     * @return Collection of valid moves in one direction
     */
    protected static Collection<ChessMove> GetAllMovesInDirection(ChessBoard board, ChessPosition startPosition, ChessPosition direction) {
        Collection<ChessMove> list = new ArrayList<ChessMove>();
        ChessPosition newPosition = startPosition;
        while (true) {
            newPosition = newPosition.plus(direction);
            MoveStatus status = canMove(board, startPosition, newPosition);
            if (status == MoveStatus.CANNOT_MOVE) break;
            list.add(new ChessMove(startPosition, newPosition));
            if (status == MoveStatus.CAN_CAPTURE) break;
        }
        return list;
    }

    /**
     * The different options that canMove could return
     */
    private enum MoveStatus {
        CANNOT_MOVE,
        CAN_MOVE,
        CAN_CAPTURE
    }

    /**
     * Checks whether the new position is an empty spot, an opposing piece,
     * a same-team piece, or off the board
     *
     * @return MoveStatus enum representing a valid move, a valid capture,
     * or an invalid move
     */
    private static MoveStatus canMove(ChessBoard board, ChessPosition startPosition, ChessPosition newPosition) {
        if (!board.inBounds(newPosition))
            return MoveStatus.CANNOT_MOVE;

        ChessPiece otherPiece = board.getPiece(newPosition);
        if (otherPiece == null)
            return MoveStatus.CAN_MOVE;

        ChessPiece thisPiece = board.getPiece(startPosition);
        if (otherPiece.getTeamColor() == thisPiece.getTeamColor())
            return MoveStatus.CANNOT_MOVE;
        else
            return MoveStatus.CAN_CAPTURE;
    }
}