package chess;

import java.util.Collection;


interface PieceMoveCalculator {
     default void addWhileOpen(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves,
                               int rowChange, int colChange) {
          boolean blocked = false;
          int row = myPosition.getRow();
          int col = myPosition.getColumn();
          while (!blocked) {
               row += rowChange;
               col += colChange;
               blocked = addMove(board, myPosition, moves, row, col, blocked, null);
          }
     }

     default boolean validPosition(int row, int col) {
          if (row >= 1 && row <= 8) {
               return col >= 1 && col <= 8;
          }
          return false;
     }

     default boolean addMove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, int row, int col,
                             boolean blocked, ChessPiece.PieceType promotion) {
          if (!blocked) {
               if (validPosition(row, col)) {
                    ChessPosition newPosition = new ChessPosition(row, col);
                    ChessPiece myPiece = board.getPiece(myPosition);
                    ChessPiece otherPiece = board.getPiece(newPosition);
                    if (otherPiece != null) {
                         if (!otherPiece.getTeamColor().equals(myPiece.getTeamColor())) {
                              ChessMove move = new ChessMove(myPosition, newPosition, promotion);
                              moves.add(move);
                         }
                         blocked = true;
                    } else {
                         ChessMove move = new ChessMove(myPosition, newPosition, promotion);
                         moves.add(move);
                    }
               } else {
                    blocked = true;
               }
          }
          return blocked;
     }

     Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition);
}
