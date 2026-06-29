package com.chess.engine.player;

import com.chess.engine.board.Board;
import com.chess.engine.move.Move;
import com.chess.engine.move.Move.KingCastleMove;
import com.chess.engine.piece.Piece;
import com.chess.engine.util.Alliance;
import java.util.ArrayList;
import java.util.List;

public class BlackPlayer extends Player {

    public BlackPlayer(final Board board, final List<Move> whiteStandardLegalMoves, final List<Move> blackStandardLegalMoves) {
        super(board, whiteStandardLegalMoves, blackStandardLegalMoves);
    }

    @Override
    public Board makeMove(Move move) {
        final List<Move> blackLegalMoves = legalMoves;

        if (blackLegalMoves.contains(move)) {
            return move.executeMove(board, move);
        }

        return board;
    }

    @Override
    public List<Move> calculateKingCastleMoves(List<Move> opponentLegalMoves) {
        final List<Move> kingCastleMoves = new ArrayList<>();

        if (playerKing.isFirstMove() && !isInCheck) {
            if (board.getTile(7).isTileOccupied()) {
                if (board.getTile(7).getPiece().getPieceType().isRook() && board.getTile(7).getPiece().getPieceAlliance() == board.getMoveMaker()) {
                    if (!board.getTile(5).isTileOccupied() && !board.getTile(6).isTileOccupied()) {
                        if (calculateAttacksOnTile(5, opponentLegalMoves).isEmpty() && calculateAttacksOnTile(6, opponentLegalMoves).isEmpty()) {
                            kingCastleMoves.add(new KingCastleMove(playerKing, 4, 7, 6, 5));
                        }
                    }
                }
            }

            if (board.getTile(0).isTileOccupied()) {
                if (board.getTile(0).getPiece().getPieceType().isRook() && board.getTile(0).getPiece().getPieceAlliance() == board.getMoveMaker()) {
                    if (!board.getTile(1).isTileOccupied() && !board.getTile(2).isTileOccupied() && !board.getTile(3).isTileOccupied()) {
                        if (calculateAttacksOnTile(2, opponentLegalMoves).isEmpty() && calculateAttacksOnTile(3, opponentLegalMoves).isEmpty()) {
                            kingCastleMoves.add(new KingCastleMove(playerKing, 4, 0, 2, 3));
                        }
                    }
                }
            }
        }

        return kingCastleMoves;
    }

    @Override
    public List<Piece> getActivePieces() {
        return board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return board.getWhitePlayer();
    }
}
