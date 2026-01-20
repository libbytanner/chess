package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RookMoveCalculator implements PieceMoveCalculator{
    public RookMoveCalculator() {
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        boolean blocked = false;
        for (int newRow = row - 1; newRow >= 1; newRow--) {
            blocked = addMove(board, myPosition, moves, newRow, col, blocked, null);
        }
        blocked = false;
        for (int newCol = col - 1; newCol >= 1; newCol--) {
            blocked = addMove(board, myPosition, moves, row, newCol, blocked, null);
        }
        blocked = false;
        for (int newRow = row + 1; newRow <= 8; newRow++) {
            blocked = addMove(board, myPosition, moves, newRow, col, blocked, null);
        }
        blocked = false;
        for (int newCol = col + 1; newCol <= 8; newCol++) {
            blocked = addMove(board, myPosition, moves, row, newCol, blocked, null);
        }

        return moves;
    }

}
