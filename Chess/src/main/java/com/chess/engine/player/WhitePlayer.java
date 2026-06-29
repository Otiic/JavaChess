package com.chess.engine.player;

import com.chess.engine.board.Board;
import com.chess.engine.move.Move;
import com.chess.engine.move.Move.KingCastleMove;
import com.chess.engine.piece.Piece;
import com.chess.engine.util.Alliance;
import java.util.ArrayList;
import java.util.List;

public class WhitePlayer extends Player {

    public WhitePlayer(final Board board, final List<Move> whiteStandardLegalMoves, final List<Move> blackStandardLegalMoves) {
        super(board, whiteStandardLegalMoves, blackStandardLegalMoves);
    }

    @Override
    public Board makeMove(Move move) {
        final List<Move> whiteLegalMoves = legalMoves;

        if (whiteLegalMoves.contains(move)) {
            return move.executeMove(board, move);
        }

        return board;
    }
    
    @Override
    public List<Move> calculateKingCastleMoves(List<Move> opponentLegalMoves) {
        final List<Move> kingCastleMoves = new ArrayList<>();
        
        if (playerKing.isFirstMove() && !isInCheck) {
            if (board.getTile(63).isTileOccupied()) {
                if (board.getTile(63).getPiece().getPieceType().isRook() && board.getTile(63).getPiece().getPieceAlliance() == board.getMoveMaker()) {
                    if (!board.getTile(61).isTileOccupied() && !board.getTile(62).isTileOccupied()) {
                        if (calculateAttacksOnTile(61, opponentLegalMoves).isEmpty() && calculateAttacksOnTile(62, opponentLegalMoves).isEmpty()) {
                            kingCastleMoves.add(new KingCastleMove(playerKing, 60, 63, 62, 61));
                        }
                    }
                }
            }
            
            if (board.getTile(56).isTileOccupied()) {
                if (board.getTile(56).getPiece().getPieceType().isRook() && board.getTile(56).getPiece().getPieceAlliance() == board.getMoveMaker()) {
                    if (!board.getTile(57).isTileOccupied() && !board.getTile(58).isTileOccupied() && !board.getTile(59).isTileOccupied()) {
                        if (calculateAttacksOnTile(58, opponentLegalMoves).isEmpty() && calculateAttacksOnTile(59, opponentLegalMoves).isEmpty()) {
                            kingCastleMoves.add(new KingCastleMove(playerKing, 60, 56, 58, 59));
                        }
                    }
                }
            }
        }
        
        return kingCastleMoves;
    }

    @Override
    public List<Piece> getActivePieces() {
        return board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return board.getBlackPlayer();
    }
}
