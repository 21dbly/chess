package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.*;

public class PawnMoveCalculator extends PieceMoveCalculator{
    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition)
    {
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();
        int forwards = color == ChessGame.TeamColor.WHITE ? 1 : -1;

        var list = new ArrayList<ChessMove>(GetMovesFromDirections(board, myPosition,
                new int[][]{{forwards, 1}, {forwards, -1}}, 1,
                new MoveStatus[]{MoveStatus.CAN_CAPTURE}));

        int forwardDistance = 1;
        if ((color == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2) ||
                (color == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7)) {
            forwardDistance = 2;
        }
        list.addAll(GetMovesFromDirections(board, myPosition,
                new int[][] {{forwards, 0}}, forwardDistance,
                new MoveStatus[] {MoveStatus.CAN_MOVE}));

        // also add promotion
        return list;
    }

}
