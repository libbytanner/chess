package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMoveCalculator implements PieceMoveCalculator {

    public BishopMoveCalculator() {
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{1, 1}, {1, -1}, {-1, -1}, {-1, 1}};
        for (int[] direction : directions) {
            addWhileOpen(board, myPosition, moves, direction[0], direction[1]);
        }
        return moves;
    }
}
