package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor teamTurn = TeamColor.WHITE;
    ChessBoard board;
    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> moves = board.getPiece(startPosition).pieceMoves(board, startPosition);
        Collection<ChessMove> movesCopy = board.getPiece(startPosition).pieceMoves(board, startPosition);

        ChessPiece piece = board.getPiece(startPosition);
        for (ChessMove move : movesCopy) {
            ChessBoard boardCopy = board.clone();
            ChessPiece newPiece = board.getPiece(move.getEndPosition());
            boardCopy.addPiece(move.getEndPosition(), piece);
            boardCopy.addPiece(move.getStartPosition(), null);
            if (piece == null) {
                moves.remove(move);
            } else if (!piece.pieceMoves(board, move.getStartPosition()).contains(move)) {
                moves.remove(move);
            } else if (checkCheck(boardCopy, piece.getTeamColor())) {
                moves.remove(move);
            } else if (newPiece != null && piece.getTeamColor() == newPiece.getTeamColor()) {
                moves.remove(move);
            }
        }
        return moves;
    }
    private void switchTeamColor() {
        if (teamTurn.equals(TeamColor.WHITE)) {
            teamTurn = TeamColor.BLACK;
        } else {
            teamTurn = TeamColor.WHITE;
        }
    }
    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece != null && validMoves(move.getStartPosition()).contains(move) && piece.getTeamColor().equals(teamTurn)){
            if (move.getPromotionPiece() != null) {
                ChessPiece promotionPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
                board.addPiece(move.getEndPosition(), promotionPiece);
            } else {
                board.addPiece(move.getEndPosition(), piece);
            }
            board.addPiece(move.getStartPosition(), null);
            switchTeamColor();
        } else {
            throw new InvalidMoveException("Invalid Move");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return checkCheck(board, teamColor);
    }

    public boolean checkCheck(ChessBoard board, TeamColor teamColor) {
        ChessPosition kingPosition = null;
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <=8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null) {
                    if (piece.getPieceType().equals(ChessPiece.PieceType.KING) && piece.getTeamColor().equals(teamColor)) {
                        kingPosition = position;
                    }
                }
            }
        }

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <=8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && !piece.getTeamColor().equals(teamColor)) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
                    ChessMove newMove = new ChessMove(position, kingPosition, null);
                    if (moves.contains(newMove)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }
}
