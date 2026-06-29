package com.chess.engine.piece;

import com.chess.engine.board.Board;
import com.chess.engine.board.Tile;
import com.chess.engine.move.Move;
import com.chess.engine.move.Move.MajorAttackMove;
import com.chess.engine.move.Move.MajorMove;
import com.chess.engine.util.Alliance;
import com.chess.engine.util.BoardUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class King extends Piece {

    private final static int[] CANDIDATE_MOVE_COORDINATES = {-9, -8, -7, -1, 1, 7, 8, 9};

    public King(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.KING, pieceAlliance, piecePosition, isFirstMove);
    }

    @Override
    public List<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
            final int candidateDestinationCoordinate = piecePosition + currentCandidateOffset;

            if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                continue;
            }

            if (isFirstFileExclusion(piecePosition, currentCandidateOffset) || isEighthFileExclusion(piecePosition, currentCandidateOffset)) {
                continue;
            }

            final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
            final Piece pieceAtDestination = candidateDestinationTile.getPiece();

            if (!candidateDestinationTile.isTileOccupied()) {
                legalMoves.add(new MajorMove(this, piecePosition, candidateDestinationCoordinate));
            } else {
                final Alliance pieceAtDestinationAlliance = pieceAtDestination.getPieceAlliance();
                
                if (pieceAlliance != pieceAtDestinationAlliance) {
                    legalMoves.add(new MajorAttackMove(this, pieceAtDestination, piecePosition, candidateDestinationCoordinate));
                }
            }
        }

        return Collections.unmodifiableList(legalMoves);
    }

    private static boolean isFirstFileExclusion(final int currentPiecePosition, final int currentCandidateOffset) {
        return BoardUtils.FIRST_FILE[currentPiecePosition] && (currentCandidateOffset == -9 || currentCandidateOffset == -1 || currentCandidateOffset == 7);
    }

    private static boolean isEighthFileExclusion(final int currentPiecePosition, final int currentCandidateOffset) {
        return BoardUtils.EIGHTH_FILE[currentPiecePosition] && (currentCandidateOffset == -7 || currentCandidateOffset == 1 || currentCandidateOffset == 9);
    }

    @Override
    public Piece movePiece(Move move) {
        return new King(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance(), false);
    }
}
