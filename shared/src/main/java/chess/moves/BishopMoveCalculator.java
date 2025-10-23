package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.*;

public class BishopMoveCalculator extends PieceMoveCalculator{
    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition)
    {
        return getMovesFromDirections(board, myPosition, new int[][] {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}}, 8);
    }
}
