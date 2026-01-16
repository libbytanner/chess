package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RookMoveCalculator implements PieceMoveCalculator{
    public RookMoveCalculator() {
    }

    private boolean validPosition(int row, int col) {
        if (row >= 1 && row <= 8) {
            return col >= 1 && col <= 8;
        }
        return false;
    }

    private boolean addMove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int row, int col, boolean blocked) {
        if (!blocked) {
            if (validPosition(row, col)) {
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
        return blocked;
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        boolean blocked = false;
        for (int newRow = row - 1; newRow >= 1; newRow--) {
            blocked = addMove(board, myPosition, moves, newRow, col, blocked);
        }
        blocked = false;
        for (int newCol = col - 1; newCol >= 1; newCol--) {
            blocked = addMove(board, myPosition, moves, row, newCol, blocked);
        }
        blocked = false;
        for (int newRow = row + 1; newRow <= 8; newRow++) {
            blocked = addMove(board, myPosition, moves, newRow, col, blocked);
        }
        blocked = false;
        for (int newCol = col + 1; newCol <= 8; newCol++) {
            blocked = addMove(board, myPosition, moves, row, newCol, blocked);
        }

        return moves;
    }

}
