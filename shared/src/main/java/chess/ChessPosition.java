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
        String colString = null;
        switch (col) {
            case 1 -> colString = "a";
            case 2 -> colString = "b";
            case 3 -> colString = "c";
            case 4 -> colString = "d";
            case 5 -> colString = "e";
            case 6 -> colString = "f";
            case 7 -> colString = "g";
            case 8 -> colString = "h";
        }
        return String.format("%s%d", colString, row);
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
