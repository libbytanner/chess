package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMoveCalculator implements PieceMoveCalculator {

    public BishopMoveCalculator() {
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
