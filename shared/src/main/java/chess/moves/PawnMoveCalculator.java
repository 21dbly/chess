package chess.moves;

import chess.*;

import java.util.*;

public class PawnMoveCalculator extends PieceMoveCalculator{
    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessMove prevMove)
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

        list.addAll(enPassant(board, myPosition, prevMove));

        return withPromotions(list, color);
    }

    private static Collection<ChessMove> enPassant(ChessBoard board, ChessPosition myPosition, ChessMove prevMove) {
        var list = new ArrayList<ChessMove>();
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();
        int forwards = color == ChessGame.TeamColor.WHITE ? 1 : -1;

        if (prevMove == null) {
            return list; // enPassant can't happen on first move
        }

        ChessPosition left = myPosition.plus(0, -1);
        ChessPosition right = myPosition.plus(0, 1);
        for( ChessPosition pos : new ChessPosition[]{left, right}) {
            if (!board.inBounds(pos)) {
                continue;
            }
            var piece = board.getPiece(pos);
            if (piece == null) {
                continue;
            }
            if (piece.getPieceType() != ChessPiece.PieceType.PAWN) {
                continue; // has to be pawn next to it
            }
            if (prevMove.getEndPosition().equals(pos) // neighbor pawn was just moved
                    && prevMove.getStartPosition().getRow() == pos.getRow() + 2 * forwards) { // neighbor pawn moved 2
                list.add(new ChessMove(myPosition, pos.plus(forwards, 0)));
                return list;
            }
        }
        return list;
    }

    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        return calculateMoves(board, myPosition, null);
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
