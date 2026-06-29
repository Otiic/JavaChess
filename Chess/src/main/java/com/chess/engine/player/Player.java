package com.chess.engine.player;

import com.chess.engine.board.Board;
import com.chess.engine.move.Move;
import com.chess.engine.piece.King;
import com.chess.engine.piece.Piece;
import com.chess.engine.util.Alliance;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Player {

    protected final Board board;
    protected final King playerKing;
    protected final List<Move> legalMoves;
    protected final boolean isInCheck;

    public Player(final Board board, final List<Move> legalMoves, final List<Move> opponentLegalMoves) {
        this.board = board;
        playerKing = establishKing();
        legalMoves.addAll(calculateKingCastleMoves(opponentLegalMoves));
        this.legalMoves = legalMoves;
        isInCheck = !calculateAttacksOnTile(playerKing.getPiecePosition(), opponentLegalMoves).isEmpty();
    }

    private King establishKing() {
        for (final Piece piece : getActivePieces()) {
            if (piece.getPieceType().isKing()) {
                return (King) piece;
            }
        }

        throw new RuntimeException("How tf did u get here, why are there no kings in your board?");
    }

    public static List<Move> calculateAttacksOnTile(int tilePosition, List<Move> opponentLegalMoves) {
        final List<Move> attackMoves = new ArrayList<>();

        for (final Move move : opponentLegalMoves) {
            if (tilePosition == move.getDestinationCoordinate()) {
                attackMoves.add(move);
            }
        }

        return Collections.unmodifiableList(attackMoves);
    }

    public King getPlayerKing() {
        return playerKing;
    }

    public List<Move> getLegalMoves() {
        return legalMoves;
    }

    public boolean isInCheck() {
        return isInCheck;
    }
    
    public boolean hasEscapeMoves() {
        boolean hasEscapeMoves = false;

        for (Move escapeMove : legalMoves) {
            if (!Move.leavesPlayerInCheck(board, escapeMove)) {
                hasEscapeMoves = true;
            }
        }

        return hasEscapeMoves;
    }

    public boolean isInCheckMate() {
        return isInCheck && !hasEscapeMoves();
    }

    public boolean isInStaleMate() {
        return !isInCheck && !hasEscapeMoves();
    }

    public abstract List<Move> calculateKingCastleMoves(List<Move> opponentLegalMoves);

    public abstract List<Piece> getActivePieces();

    public abstract Alliance getAlliance();

    public abstract Player getOpponent();

    public abstract Board makeMove(Move move);
}
