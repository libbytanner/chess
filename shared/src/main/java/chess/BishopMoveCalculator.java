package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMoveCalculator implements PieceMoveCalculator {

    public BishopMoveCalculator() {
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        boolean blocked = false;
        for (int newRow = row - 1; newRow >= 1; newRow--) {
            int diff = Math.abs(row - newRow);
            int newCol = col - diff;
            blocked = addMove(board, myPosition, moves, newRow, newCol, blocked, null);
        }
        blocked = false;
        for (int i = row - 1; i >= 1; i--) {
            int diff = Math.abs(row - i);
            int newCol = col + diff;
            blocked = addMove(board, myPosition, moves, i, newCol, blocked, null);
        }
        blocked = false;
        for (int i = row + 1; i <= 8; i++) {
            int diff = Math.abs(row - i);
            int newCol = col - diff;
            blocked = addMove(board, myPosition, moves, i, newCol, blocked, null);

        }
        blocked = false;
        for (int i = row + 1; i <= 8; i++) {
            int diff = Math.abs(row - i);
            int newCol = col + diff;
            blocked = addMove(board, myPosition, moves, i, newCol, blocked, null);
        }

        return moves;
    }
}
