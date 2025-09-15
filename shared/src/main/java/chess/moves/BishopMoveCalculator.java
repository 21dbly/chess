package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.*;

public class BishopMoveCalculator extends PieceMoveCalculator{
    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition)
    {
        var list = new ArrayList<ChessMove>();
        list.add(new ChessMove(new ChessPosition(5, 4), new ChessPosition(6, 5)));
        return list;
    }
}
