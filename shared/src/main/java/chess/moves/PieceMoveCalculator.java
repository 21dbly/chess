package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PieceMoveCalculator {
    /**
     * Calculates all the positions a chess piece can move in all given directions
     * for a given distance (ex: 8 for rooks or bishops, 1 for kings).
     * Takes into account the board state and the current piece's color and position.
     *
     * @param directions an array of int pairs representing the directions to check
     * @param distance an int representing how far to keep checking (usually 1 or 8)
     * @return Collection of valid moves
     */
    protected static Collection<ChessMove> getMovesFromDirections(ChessBoard board, ChessPosition startPosition,
                                                                  int[][] directions, int distance) {
        return getMovesFromDirections(board, startPosition, directions, distance,
                new MoveStatus[] {MoveStatus.CAN_MOVE, MoveStatus.CAN_CAPTURE});
    }

    /**
     * Calculates all the positions a chess piece can move in all given directions
     * for a given distance (ex: 8 for rooks or bishops, 1 for kings).
     * Takes into account the board state and the current piece's color and position.
     *
     * @param directions an array of int pairs representing the directions to check
     * @param distance an int representing how far to keep checking (usually 1 or 8)
     * @param validStatuses normally CAN_MOVE and CAN_CAPTURE because those are the
     *                      cases for a valid move
     * @return Collection of valid moves
     */
    protected static Collection<ChessMove> getMovesFromDirections(ChessBoard board, ChessPosition startPosition, int[][] directions,
                                                                  int distance, MoveStatus[] validStatuses) {
        Collection<ChessMove> list = new ArrayList<ChessMove>();
        for (int[] direction : directions) {
            list.addAll(getMovesFromSingleDirection(board, startPosition, direction, distance, validStatuses));
        }
        return list;
    }

    /**
     * Calculates all the positions a chess piece can move in a direction
     * for a given distance (ex: 8 for rooks or bishops, 1 for kings).
     * Takes into account the board state and the current piece's color and position.
     *
     * @param direction an int pair representing the row and column offset to check
     * @param distance an int representing how far to keep checking (usually 1 or 8)
     * @param validStatuses normally CAN_MOVE and CAN_CAPTURE because those are the
     *                      cases for a valid move
     * @return Collection of valid moves
     */
    protected static Collection<ChessMove> getMovesFromSingleDirection(ChessBoard board, ChessPosition startPosition, int[] direction,
                                                                       int distance, MoveStatus[] validStatuses) {
        MoveStatus passThroughStatus = MoveStatus.CAN_MOVE;

        Collection<ChessMove> list = new ArrayList<ChessMove>();
        ChessPosition newPosition = startPosition;
        while (distance-- > 0) {
            if (direction.length != 2) {throw new RuntimeException("direction is not an int pair");}

            newPosition = newPosition.plus(direction[0], direction[1]);
            MoveStatus status = canMove(board, startPosition, newPosition);
            if (Arrays.asList(validStatuses).contains(status)) {
                list.add(new ChessMove(startPosition, newPosition));
            }

            if (status != passThroughStatus) {break;}
        }
        return list;
    }

    /**
     * The different options that canMove could return
     */
    protected enum MoveStatus {
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
    protected static MoveStatus canMove(ChessBoard board, ChessPosition startPosition, ChessPosition newPosition) {
        if (!board.inBounds(newPosition)) {
            return MoveStatus.CANNOT_MOVE;
        }
        ChessPiece otherPiece = board.getPiece(newPosition);
        if (otherPiece == null) {
            return MoveStatus.CAN_MOVE;
        }
        ChessPiece thisPiece = board.getPiece(startPosition);
        if (otherPiece.getTeamColor() == thisPiece.getTeamColor()) {
            return MoveStatus.CANNOT_MOVE;
        }
        else {
            return MoveStatus.CAN_CAPTURE;
        }
    }
}