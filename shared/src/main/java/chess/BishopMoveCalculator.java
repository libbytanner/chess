package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMoveCalculator implements PieceMoveCalculator {

    public BishopMoveCalculator() {
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        for (int[] direction : directions) {
            boolean blocked = false;
            int newRow = row + direction[0];
            int newCol = col + direction[1];
            while (validPosition(newRow, newCol) && !blocked) {
                blocked = addMove(board, myPosition, moves, newRow, newCol, blocked, null);
                newRow += direction[0];
                newCol += direction[1];
            }
        }
        return moves;
    }
}
