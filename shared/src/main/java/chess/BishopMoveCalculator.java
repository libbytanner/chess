package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMoveCalculator implements PieceMoveCalculator {

    public BishopMoveCalculator() {
    }

    private void addWhileOpen(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int rowChange, int colChange) {
        boolean blocked = false;
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        while (!blocked) {
            row += rowChange;
            col += colChange;
            blocked = addMove(board, myPosition, moves, row, col, blocked, null);
        }
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        addWhileOpen(board, myPosition, moves, 1, 1);
        addWhileOpen(board, myPosition, moves, 1, -1);
        addWhileOpen(board, myPosition, moves, -1, 1);
        addWhileOpen(board, myPosition, moves, -1, -1);
        return moves;
    }
}
