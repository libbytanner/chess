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
        for (int newRow = 1; newRow <= 8; newRow++) {
            if (newRow != row) {
                int diff = row - newRow;
                int newCol = col - diff;
                if (newCol >= 1) {
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    ChessMove move = new ChessMove(myPosition, newPosition, null);
                    moves.add(move);
                }
                newCol = col + diff;
                if (newCol <= 8) {
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    ChessMove move = new ChessMove(myPosition, newPosition, null);
                    moves.add(move);
                }
            }
        }
        return moves;
    }
}
