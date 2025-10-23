package chess.moves;

import chess.*;

import java.util.*;

public class PawnMoveCalculator extends PieceMoveCalculator{
    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition)
    {
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();
        int forwards = color == ChessGame.TeamColor.WHITE ? 1 : -1;

        var list = new ArrayList<ChessMove>(getMovesFromDirections(board, myPosition,
                new int[][]{{forwards, 1}, {forwards, -1}}, 1,
                new MoveStatus[]{MoveStatus.CAN_CAPTURE}));

        int forwardDistance = 1;
        if ((color == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2) ||
                (color == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7)) {
            forwardDistance = 2;
        }
        list.addAll(getMovesFromDirections(board, myPosition,
                new int[][] {{forwards, 0}}, forwardDistance,
                new MoveStatus[] {MoveStatus.CAN_MOVE}));

        return withPromotions(list, color);
    }

    private static Collection<ChessMove> withPromotions(Collection<ChessMove> list, ChessGame.TeamColor color) {
        int topRow = color == ChessGame.TeamColor.WHITE ? 8 : 1;
        var newList = new ArrayList<ChessMove>();
        for (var move : list) {
            if (move.getEndPosition().getRow() == topRow) {
                newList.addAll(getPromotionsForMove(move));
            } else {
                newList.add(move);
            }
        }
        return newList;
    }

    private static Collection<ChessMove> getPromotionsForMove(ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        return new ArrayList<>(List.of(new ChessMove(start, end, ChessPiece.PieceType.QUEEN),
                                       new ChessMove(start, end, ChessPiece.PieceType.KNIGHT),
                                       new ChessMove(start, end, ChessPiece.PieceType.BISHOP),
                                       new ChessMove(start, end, ChessPiece.PieceType.ROOK)));
    }

}
