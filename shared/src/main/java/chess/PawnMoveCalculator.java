package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMoveCalculator implements PieceMoveCalculator {
    public PawnMoveCalculator() {
    }


    private void promotePiece(ChessBoard board, List<ChessMove> moves, ChessPosition myPosition, int row, int col) {
        ChessPiece.PieceType[] promotions = {ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT};
        for (ChessPiece.PieceType promotion : promotions) {
            boolean blocked = false;
            if (!checkSpace(board, row, col)) {
                blocked = addMove(board, myPosition, moves, row, col, false, promotion);
            }
            if (!blocked) {
                if (checkSpace(board, row, col - 1)) {
                    addMove(board, myPosition, moves, row, col - 1, false, promotion);
                }
                if (checkSpace(board, row, col + 1)) {
                    addMove(board, myPosition, moves, row, col + 1, false, promotion);
                }
            }
        }
    }
    /*
        Checks if a space is in bounds, and occupied.
     */
    private boolean checkSpace(ChessBoard board, int row, int col) {
        if (validPosition(row, col)) {
            ChessPosition space = new ChessPosition(row, col);
            return board.getPiece(space) != null;
        }
        return false;
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        boolean blocked = false;
        boolean hasMoved = false;
        boolean promote = false;
        int rowChange;
        ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();
        if (color.equals(ChessGame.TeamColor.WHITE)) {
            rowChange = 1;
            if (row == 7) {
                promote = true;
            } else if (row != 2) {
                hasMoved = true;
            }
        } else {
            rowChange = -1;
            if (row == 2) {
                promote = true;
            } else if (row != 7) {
                hasMoved = true;
            }
        }
        if (promote) {
            promotePiece(board, moves, myPosition, row + rowChange, col);
        } else {
            if (!checkSpace(board, row + rowChange, col)) {
                addMove(board, myPosition, moves, row + rowChange, col, false, null);
            } else {
                blocked = true;
            }
            if (checkSpace(board, row + rowChange, col + 1)) {
                addMove(board, myPosition, moves, row + rowChange, col + 1, false, null);
            }
            if (checkSpace(board, row + rowChange, col - 1)) {
                addMove(board, myPosition, moves, row + rowChange, col - 1, false, null);
            }
        }
        if (!hasMoved && !blocked) {
            if (!checkSpace(board, row + rowChange, col)) {
                if (color.equals(ChessGame.TeamColor.WHITE)) {
                    rowChange = 2;
                } else {
                    rowChange = -2;
                }
                if (!checkSpace(board, row + rowChange, col)) {
                    addMove(board, myPosition, moves, row + rowChange, col, false, null);
                }
            }
        }

        return moves;
    }

}
