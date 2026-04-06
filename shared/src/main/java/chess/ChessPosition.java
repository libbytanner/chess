package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }


    @Override
    public String toString() {
        String rowString = null;
        switch (row) {
            case 1 -> rowString = "a";
            case 2 -> rowString = "b";
            case 3 -> rowString = "c";
            case 4 -> rowString = "d";
            case 5 -> rowString = "e";
            case 6 -> rowString = "f";
            case 7 -> rowString = "g";
            case 8 -> rowString = "h";
        }
        return String.format("%s%d", rowString, col);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
