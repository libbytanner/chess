package ui;

import chess.*;
import model.ResponseException;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_MAGENTA;

public class BoardPrinter {
    private static final int CHESS_BOARD_SIZE = 8;
    public void printBoard(PrintStream out, ChessGame game, ChessGame.TeamColor color,
                           Collection<ChessMove> validMoves, ChessPosition currentPosition) {
        ChessBoard board = game.getBoard();
//        out.print(ERASE_SCREEN);
//        out.print(moveCursorToLocation(100, 100) + SET_BG_COLOR_DARK_GREY + "\n");
        out.print(SET_BG_COLOR_DARK_GREY + "\n");
        out.print(SET_TEXT_BOLD);
        printLetterHeaders(out, color);
        setBlack(out);
        if (color.equals(ChessGame.TeamColor.WHITE)) {
            printForWhite(out, board, validMoves, currentPosition);
        } else {
            printForBlack(out, board, validMoves, currentPosition);
        }
        out.print(SET_BG_COLOR_DARK_GREY);
        printLetterHeaders(out, color);
        out.print("\n");
        out.print(RESET_BG_COLOR + RESET_TEXT_BOLD_FAINT);
    }

    private ChessGame.TeamColor printSquare(PrintStream out, ChessGame.TeamColor current,
                                            ChessPiece piece, boolean valid) {
        if (current.equals(ChessGame.TeamColor.WHITE)) {
            current = ChessGame.TeamColor.BLACK;
            if (valid) {
                setHighlightedBlack(out);
            } else {
                setBlack(out);
            }
        } else {
            current = ChessGame.TeamColor.WHITE;
            if (valid) {
                setHighlightedWhite(out);
            } else {
                setWhite(out);
            }
        }
        out.print(getPieceCharacter(piece));
        return current;
    }

    private void printForWhite(PrintStream out, ChessBoard board, Collection<ChessMove> validPositions, ChessPosition currentPosition) {
        ChessGame.TeamColor current = ChessGame.TeamColor.BLACK;
        for (int i = CHESS_BOARD_SIZE; i > 0; i--) {
            printSideNumber(out, i);
            for (int j = 1; j <= CHESS_BOARD_SIZE; j++) {
                current = evalSquare(out, board, validPositions, currentPosition, current, i, j);
            }
            printSideNumber(out, i);
            out.print("\n");
            current = switchColor(current, out);
        }
    }

    private ChessGame.TeamColor evalSquare(PrintStream out, ChessBoard board, Collection<ChessMove> validPositions, ChessPosition currentPosition, ChessGame.TeamColor current, int i, int j) {
        ChessPosition position = new ChessPosition(i, j);
        ChessPiece piece = board.getPiece(position);
        if (currentPosition != null) {
            ChessMove move = new ChessMove(currentPosition, position, null);
            if (position.equals(currentPosition)) {
                if (current.equals(ChessGame.TeamColor.WHITE)) {
                    current = ChessGame.TeamColor.BLACK;
                } else {
                    current = ChessGame.TeamColor.WHITE;
                }
                out.print(SET_BG_COLOR_YELLOW);
                out.print(getPieceCharacter(piece));
            } else if (validPositions.contains(move)) {
                current = printSquare(out, current, piece, true);
            } else {
                current = printSquare(out, current, piece, false);
            }
        } else {
            current = printSquare(out, current, piece, false);
        }
        return current;
    }

    private void printForBlack(PrintStream out, ChessBoard board, Collection<ChessMove> validPositions, ChessPosition currentPosition) {
        ChessGame.TeamColor current = ChessGame.TeamColor.BLACK;
        for (int i = 1; i <= CHESS_BOARD_SIZE; i++) {
            printSideNumber(out, i);
            for (int j = CHESS_BOARD_SIZE; j > 0; j--) {
                current = evalSquare(out, board, validPositions, currentPosition, current, i, j);
            }
            printSideNumber(out, i);
            out.print("\n");
            current = switchColor(current, out);
        }
    }

    private String getPieceCharacter(ChessPiece piece) {
        if (piece == null) {
            return (EMPTY);
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.PAWN) &&
                piece.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            return (BLACK_PAWN);
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.PAWN) &&
                piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            return (WHITE_PAWN);
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.KNIGHT) &&
                piece.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            return (BLACK_KNIGHT);
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.KNIGHT) &&
                piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            return (WHITE_KNIGHT);
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.ROOK) &&
                piece.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            return (BLACK_ROOK);
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.ROOK) &&
                piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            return (WHITE_ROOK);
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.BISHOP) &&
                piece.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            return (BLACK_BISHOP);
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.BISHOP) &&
                piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            return (WHITE_BISHOP);
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.QUEEN) &&
                piece.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            return (BLACK_QUEEN);
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.QUEEN) &&
                piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            return (WHITE_QUEEN);
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.KING) &&
                piece.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
            return (BLACK_KING);
        } else if (piece.getPieceType().equals(ChessPiece.PieceType.KING) &&
                piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
            return (WHITE_KING);
        }
        throw new ResponseException("Server Error", 500);
    }

    private void printLetterHeaders(PrintStream out, ChessGame.TeamColor color) {
        List<String> cols = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h");
        out.print(SET_TEXT_COLOR_BLUE + "   ");
        if (color.equals(ChessGame.TeamColor.BLACK)) {
            cols = cols.reversed();
        }
        for (String col : cols) {
            out.print(" " + col + " ");
        }
        out.print("\n");
    }

    private void printSideNumber(PrintStream out, int i) {
        out.print(SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLUE);
        out.print(" " + i + " ");
    }

    private ChessGame.TeamColor switchColor(ChessGame.TeamColor current, PrintStream out) {
        if (current.equals(ChessGame.TeamColor.WHITE)) {
            setBlack(out);
            return ChessGame.TeamColor.BLACK;
        } else {
            setWhite(out);
            return ChessGame.TeamColor.WHITE;
        }
    }

    private void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_MAGENTA);
    }

    private void setHighlightedBlack(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREEN);
        out.print(SET_TEXT_COLOR_MAGENTA);
    }

    private void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_MAGENTA);
    }

    private void setHighlightedWhite(PrintStream out) {
        out.print(SET_BG_COLOR_GREEN);
        out.print(SET_TEXT_COLOR_MAGENTA);
    }

}
