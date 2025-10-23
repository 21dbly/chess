package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoveCalculator extends PieceMoveCalculator{
    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition)
    {
        return getMovesFromDirections(board, myPosition,
                new int[][] {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}}, 1);
    }

    public static Collection<ChessMove> calculateCastleMoves(ChessBoard board, ChessPosition myPosition)
    {
        ArrayList<ChessMove> list = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        if (piece == null)
            throw new RuntimeException("There is no King at " + myPosition);

        ChessGame.TeamColor color = piece.getTeamColor();
        int backRow = color == ChessGame.TeamColor.WHITE ? 1 : 8;

        if (piece.hasMoved() || !myPosition.equals(new ChessPosition(backRow, 5)))
            return list;

        // R...K..R
        // ..KR.RK.
        ChessPiece col1Rook = board.getPiece(new ChessPosition(backRow, 1));
        if (col1Rook != null &&
                col1Rook.getPieceType() == ChessPiece.PieceType.ROOK &&
                col1Rook.getTeamColor() == color &&
                !col1Rook.hasMoved() &&
                board.getPiece(myPosition.plus(0, -1)) == null &&
                board.getPiece(myPosition.plus(0, -2)) == null &&
                board.getPiece(myPosition.plus(0, -3)) == null)
            list.add(new ChessMove(myPosition, myPosition.plus(0, -2)));

        ChessPiece col8Rook = board.getPiece(new ChessPosition(backRow, 8));
        if (col8Rook != null &&
                col8Rook.getPieceType() == ChessPiece.PieceType.ROOK &&
                col8Rook.getTeamColor() == color &&
                !col8Rook.hasMoved() &&
                board.getPiece(myPosition.plus(0, 1)) == null &&
                board.getPiece(myPosition.plus(0, 2)) == null)
            list.add(new ChessMove(myPosition, myPosition.plus(0, 2)));

        return list;
    }
}
