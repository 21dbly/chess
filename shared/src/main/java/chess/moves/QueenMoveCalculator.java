package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMoveCalculator extends PieceMoveCalculator{
    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition)
    {
        var list = new ArrayList<ChessMove>();
        list.addAll(GetAllMovesInDirection(board, myPosition, new ChessPosition(1, 0)));
        list.addAll(GetAllMovesInDirection(board, myPosition, new ChessPosition(0, 1)));
        list.addAll(GetAllMovesInDirection(board, myPosition, new ChessPosition(-1, 0)));
        list.addAll(GetAllMovesInDirection(board, myPosition, new ChessPosition(0, -1)));

        list.addAll(GetAllMovesInDirection(board, myPosition, new ChessPosition(1, 1)));
        list.addAll(GetAllMovesInDirection(board, myPosition, new ChessPosition(-1, 1)));
        list.addAll(GetAllMovesInDirection(board, myPosition, new ChessPosition(-1, -1)));
        list.addAll(GetAllMovesInDirection(board, myPosition, new ChessPosition(1, -1)));
        return list;
    }
}
