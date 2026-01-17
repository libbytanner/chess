package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMoveCalculator implements PieceMoveCalculator {
    public PawnMoveCalculator() {
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        boolean blocked = false;
        boolean hasMoved = false;
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();
        if (color.equals(ChessGame.TeamColor.WHITE) && row != 2) {
            hasMoved = true;
        } else if (color.equals(ChessGame.TeamColor.BLACK) && row != 7) {
            hasMoved = true;
        }
        if (!hasMoved) {
            int newRow = row;
            if (color.equals(ChessGame.TeamColor.WHITE)) {
                newRow += 2;
            } else {
                newRow -= 2;
            }
            addMove(board, myPosition, moves, newRow, col, blocked);
        }
        return moves;
    }

}
