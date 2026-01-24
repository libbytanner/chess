package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RookMoveCalculator implements PieceMoveCalculator{
    public RookMoveCalculator() {
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        addWhileOpen(board, myPosition, moves, 1, 0);
        addWhileOpen(board, myPosition, moves, -1, 0);
        addWhileOpen(board, myPosition, moves, 0, 1);
        addWhileOpen(board, myPosition, moves, 0, -1);

        return moves;
    }

}
