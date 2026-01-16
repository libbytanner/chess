package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnightMoveCalculator implements PieceMoveCalculator{
    public KnightMoveCalculator() {
    }

    private void addMove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int row, int col) {
        if (validPosition(row, col)) {
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessPiece myPiece = board.getPiece(myPosition);
            ChessPiece otherPiece = board.getPiece(newPosition);
            if (otherPiece != null) {
                if (!otherPiece.getTeamColor().equals(myPiece.getTeamColor())) {
                    ChessMove move = new ChessMove(myPosition, newPosition, null);
                    moves.add(move);
                }
            } else {
                ChessMove move = new ChessMove(myPosition, newPosition, null);
                moves.add(move);
            }
        }
    }

    private void changeRowsAddMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int rowChange, int colChange) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int newRow = row + rowChange;
        int newCol = col - colChange;
        addMove(board, myPosition, moves, newRow, newCol);
        newCol = col + colChange;
        addMove(board, myPosition, moves, newRow, newCol);
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
