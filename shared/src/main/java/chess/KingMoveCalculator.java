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
        boolean blocked = false;
        for (int i = -1; i <=1; i++) {
            for (int j = -1; j <=1; j++) {
                addMove(board, myPosition, moves, row + i, col + j, blocked);
            }
        }
        return moves;
    }
}
