package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int row = 0; row < squares.length; row++) {
            ChessGame.TeamColor color = null;
            ChessPiece.PieceType type = null;
            for (int col = 0; col < squares.length; col++) {
                if (row == 1 || row == 6) {
                    type = ChessPiece.PieceType.PAWN;
                } else if (row == 0 || row == 7){
                    if (col == 0 || col == 7) {
                        type = ChessPiece.PieceType.ROOK;
                    } else if (col == 1 || col == 6) {
                        type = ChessPiece.PieceType.KNIGHT;
                    } else if (col == 2 || col == 5) {
                        type = ChessPiece.PieceType.BISHOP;
                    } else if (col == 3) {
                        type = ChessPiece.PieceType.QUEEN;
                    } else {
                        type = ChessPiece.PieceType.KING;
                    }
                }
                if (row == 0 || row == 1) {
                    color = ChessGame.TeamColor.WHITE;
                } else if (row == 6 || row == 7) {
                    color = ChessGame.TeamColor.BLACK;
                }
                if (color != null) {
                    ChessPiece piece = new ChessPiece(color, type);
                    squares[row][col] = piece;
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        for (ChessPiece[] row : squares) {
            for (ChessPiece piece : row) {
                if (piece != null) {
                    string.append(piece);
                } else {
                    string.append("empty space");
                }
                string.append(" | ");
            }
            string.append("\n");
        }
        return "ChessBoard{" +
                "squares=" + string +
                '}';
    }
}
