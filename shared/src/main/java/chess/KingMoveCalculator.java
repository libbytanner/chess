package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMoveCalculator implements PieceMoveCalculator {
    public KingMoveCalculator() {}

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int[][] directions = {{-1, 1}, {-1, 0}, {-1, -1}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {1, 0}};
        boolean blocked = false;
        for (int[] direction : directions) {
            addMove(board, myPosition, moves, row + direction[0], col + direction[1], blocked, null);
        }
        return moves;
    }
}
