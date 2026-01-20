package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnightMoveCalculator implements PieceMoveCalculator{
    public KnightMoveCalculator() {
    }

    private void changeRowsAddMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int rowChange, int colChange) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        boolean blocked = false;
        int newRow = row + rowChange;
        int newCol = col - colChange;
        addMove(board, myPosition, moves, newRow, newCol, blocked, null);
        newCol = col + colChange;
        addMove(board, myPosition, moves, newRow, newCol, blocked, null);
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();

        changeRowsAddMoves(board, myPosition, moves, 1, 2);
        changeRowsAddMoves(board, myPosition, moves, -1, 2);
        changeRowsAddMoves(board, myPosition, moves, 2, 1);
        changeRowsAddMoves(board, myPosition, moves, -2, 1);

        return moves;
    }
}
