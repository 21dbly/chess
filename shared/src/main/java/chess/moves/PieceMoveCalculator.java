package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMoveCalculator {
    protected static Collection<ChessMove> GetMoveInDirection(ChessBoard board, ChessPosition startPosition, ChessPosition direction) {
        return new ArrayList<ChessMove>();
    }

    protected static Collection<ChessMove> GetAllMovesInDirection(ChessBoard board, ChessPosition startPosition, ChessPosition direction) {
        return new ArrayList<ChessMove>();
    }
}