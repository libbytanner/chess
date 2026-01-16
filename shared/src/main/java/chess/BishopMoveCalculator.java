package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMoveCalculator implements PieceMoveCalculator {

    public BishopMoveCalculator() {
    }

    private boolean addMove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int row, int col, boolean blocked) {
        if (!blocked) {
            if (row >= 1 && row <= 8) {
                if (col >= 1 && col <= 8) {
                    ChessPosition newPosition = new ChessPosition(row, col);
                    ChessPiece myPiece = board.getPiece(myPosition);
                    ChessPiece otherPiece = board.getPiece(newPosition);
                    if (otherPiece != null) {
                        if (otherPiece.getTeamColor().equals(myPiece.getTeamColor())) {
                            blocked = true;
                        } else {
                            ChessMove move = new ChessMove(myPosition, newPosition, null);
                            moves.add(move);
                            blocked = true;
                        }
                    } else {
                        ChessMove move = new ChessMove(myPosition, newPosition, null);
                        moves.add(move);
                    }
                }
            }
        }
        return blocked;
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        boolean blocked = false;
        for (int newRow = row - 1; newRow >= 1; newRow--) {
            int diff = Math.abs(row - newRow);
            int newCol = col - diff;
            blocked = addMove(board, myPosition, moves, newRow, newCol, blocked);
        }
        blocked = false;
        for (int i = row - 1; i >= 1; i--) {
            int diff = Math.abs(row - i);
            int newCol = col + diff;
            blocked = addMove(board, myPosition, moves, i, newCol, blocked);
        }
        blocked = false;
        for (int i = row + 1; i <= 8; i++) {
            int diff = Math.abs(row - i);
            int newCol = col - diff;
            blocked = addMove(board, myPosition, moves, i, newCol, blocked);

        }
        blocked = false;
        for (int i = row + 1; i <= 8; i++) {
            int diff = Math.abs(row - i);
            int newCol = col + diff;
            blocked = addMove(board, myPosition, moves, i, newCol, blocked);
        }

        return moves;
    }
}
