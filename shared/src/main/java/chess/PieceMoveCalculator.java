package chess;

import java.util.Collection;


interface PieceMoveCalculator {
     Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition);
}
