package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.*;

public class BishopMoveCalculator extends PieceMoveCalculator{
    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition)
    {
        var list = new ArrayList<ChessMove>();
        list.addAll(GetMoveInDirection(board, myPosition, new ChessPosition(1, 1)));
        return list;
    }
}
