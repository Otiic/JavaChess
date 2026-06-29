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

public class Queen extends Piece {
    
    private final static int[] CANDIDATE_MOVE_COORDINATES = {-9, -8, -7, -1, 1, 7, 8, 9};
    
    public Queen(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.QUEEN, pieceAlliance, piecePosition, isFirstMove);
    }

    @Override
    public List<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (int candidateCoordinateOffset : CANDIDATE_MOVE_COORDINATES) {
            int candidateDestinationCoordinate = piecePosition;

            while (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                if (isFirstFileExclusion(candidateDestinationCoordinate, candidateCoordinateOffset) || isEighthFileExclusion(candidateDestinationCoordinate, candidateCoordinateOffset)) {
                    break;
                }

                candidateDestinationCoordinate += candidateCoordinateOffset;

                if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                    final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);

                    if (!candidateDestinationTile.isTileOccupied()) {
                        legalMoves.add(new MajorMove(this, piecePosition, candidateDestinationCoordinate));
                    } else {
                        final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                        final Alliance pieceAtDestinationAlliance = pieceAtDestination.getPieceAlliance();

                        if (pieceAlliance != pieceAtDestinationAlliance) {
                            legalMoves.add(new MajorAttackMove(this, pieceAtDestination, piecePosition, candidateDestinationCoordinate));
                        }
                        break;
                    }
                }
            }
        }

        return Collections.unmodifiableList(legalMoves);
    }
    
    private static boolean isFirstFileExclusion(final int currentCandidateDestinationCoordinate, final int currentCandidateOffset) {
        return BoardUtils.FIRST_FILE[currentCandidateDestinationCoordinate] && (currentCandidateOffset == -9 || currentCandidateOffset == -1 || currentCandidateOffset == 7);
    }

    private static boolean isEighthFileExclusion(final int currentCandidateDestinationCoordinate, final int currentCandidateOffset) {
        return BoardUtils.EIGHTH_FILE[currentCandidateDestinationCoordinate] && (currentCandidateOffset == -7 || currentCandidateOffset == 1 || currentCandidateOffset == 9);
    }

    @Override
    public Piece movePiece(Move move) {
        return new Queen(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance(), false);
    }
}
