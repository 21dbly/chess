package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoveCalculator extends PieceMoveCalculator{
    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition)
    {
        var list = new ArrayList<ChessMove>();
        list.addAll(GetMoveInDirection(board, myPosition, new ChessPosition(2, 1)));
        list.addAll(GetMoveInDirection(board, myPosition, new ChessPosition(2, -1)));

        list.addAll(GetMoveInDirection(board, myPosition, new ChessPosition(1, 2)));
        list.addAll(GetMoveInDirection(board, myPosition, new ChessPosition(-1, 2)));

        list.addAll(GetMoveInDirection(board, myPosition, new ChessPosition(-2, 1)));
        list.addAll(GetMoveInDirection(board, myPosition, new ChessPosition(-2, -1)));

        list.addAll(GetMoveInDirection(board, myPosition, new ChessPosition(1, -2)));
        list.addAll(GetMoveInDirection(board, myPosition, new ChessPosition(-1, -2)));
        return list;
    }
}
