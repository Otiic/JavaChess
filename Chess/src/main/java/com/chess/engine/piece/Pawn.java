package com.chess.engine.piece;

import com.chess.engine.board.Board;
import com.chess.engine.board.Tile;
import com.chess.engine.move.Move;
import com.chess.engine.move.Move.EnPassantMove;
import com.chess.engine.move.Move.MajorAttackMove;
import com.chess.engine.move.Move.MajorMove;
import com.chess.engine.move.Move.PawnJumpMove;
import com.chess.engine.move.Move.PawnPromotionAttackMove;
import com.chess.engine.move.Move.PawnPromotionMove;
import com.chess.engine.util.Alliance;
import com.chess.engine.util.BoardUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pawn extends Piece {

    private final static int[] CANDIDATE_MOVE_COORDINATES = {7, 8, 9, 16};
    private final int pawnDirection = pieceAlliance.getDirection();

    public Pawn(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.PAWN, pieceAlliance, piecePosition, isFirstMove);
    }

    @Override
    public List<Move> calculateLegalMoves(Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
            final int candidateDestinationCoordinate = piecePosition + (currentCandidateOffset * pawnDirection);

            if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                continue;
            }

            if (isFirstFileExclusion(piecePosition, currentCandidateOffset) || isEighthFileExclusion(piecePosition, currentCandidateOffset)) {
                continue;
            }

            if (!isFirstMove && currentCandidateOffset == 16) {
                continue;
            }

            final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
            final Piece pieceAtDestination = candidateDestinationTile.getPiece();
            final Pawn enPassantPawn = board.getEnPassantPawn();

            if (!candidateDestinationTile.isTileOccupied() && (currentCandidateOffset == 8)) {
                if (pieceAlliance == Alliance.WHITE && BoardUtils.EIGHTH_RANK[candidateDestinationCoordinate]) {
                    legalMoves.add(new PawnPromotionMove(this, piecePosition, candidateDestinationCoordinate));
                } else if (pieceAlliance == Alliance.BLACK && BoardUtils.FIRST_RANK[candidateDestinationCoordinate]) {
                    legalMoves.add(new PawnPromotionMove(this, piecePosition, candidateDestinationCoordinate));
                } else {
                    legalMoves.add(new MajorMove(this, piecePosition, candidateDestinationCoordinate));
                }
            }

            if (!candidateDestinationTile.isTileOccupied() && !board.getTile(piecePosition + (pawnDirection * 8)).isTileOccupied() && (currentCandidateOffset == 16)) {
                legalMoves.add(new PawnJumpMove(this, piecePosition, candidateDestinationCoordinate));
            }

            if (candidateDestinationTile.isTileOccupied() && (currentCandidateOffset == 7 || currentCandidateOffset == 9)) {
                final Alliance pieceAtDestinationAlliance = pieceAtDestination.getPieceAlliance();

                if (pieceAlliance != pieceAtDestinationAlliance) {
                    if (pieceAlliance == Alliance.WHITE && BoardUtils.EIGHTH_RANK[candidateDestinationCoordinate]) {
                        legalMoves.add(new PawnPromotionAttackMove(this, pieceAtDestination, piecePosition, candidateDestinationCoordinate));
                    } else if (pieceAlliance == Alliance.BLACK && BoardUtils.FIRST_RANK[candidateDestinationCoordinate]) {
                        legalMoves.add(new PawnPromotionAttackMove(this, pieceAtDestination, piecePosition, candidateDestinationCoordinate));
                    } else {
                        legalMoves.add(new MajorAttackMove(this, pieceAtDestination, piecePosition, candidateDestinationCoordinate));
                    }
                }
            }

            if (!candidateDestinationTile.isTileOccupied() && (currentCandidateOffset == 7 || currentCandidateOffset == 9)) {
                if (enPassantPawn != null) {
                    if (enPassantPawn.getPieceAlliance() != pieceAlliance) {
                        if (pawnDirection == -1) {
                            if (currentCandidateOffset == 7 && enPassantPawn.getPiecePosition() == piecePosition + 1) {
                                legalMoves.add(new EnPassantMove(this, board.getEnPassantPawn(), piecePosition, candidateDestinationCoordinate));
                            } else if (currentCandidateOffset == 9 && enPassantPawn.getPiecePosition() == piecePosition - 1) {
                                legalMoves.add(new EnPassantMove(this, board.getEnPassantPawn(), piecePosition, candidateDestinationCoordinate));
                            }
                        } else {
                            if (currentCandidateOffset == 7 && enPassantPawn.getPiecePosition() == piecePosition - 1) {
                                legalMoves.add(new EnPassantMove(this, board.getEnPassantPawn(), piecePosition, candidateDestinationCoordinate));
                            } else if (currentCandidateOffset == 9 && enPassantPawn.getPiecePosition() == piecePosition + 1) {
                                legalMoves.add(new EnPassantMove(this, board.getEnPassantPawn(), piecePosition, candidateDestinationCoordinate));
                            }
                        }
                    }
                }
            }
        }

        return Collections.unmodifiableList(legalMoves);
    }

    private boolean isFirstFileExclusion(final int currentPiecePosition, final int currentCandidateOffset) {
        if (pawnDirection == -1) {
            return BoardUtils.FIRST_FILE[currentPiecePosition] && currentCandidateOffset == 9;
        } else {
            return BoardUtils.FIRST_FILE[currentPiecePosition] && currentCandidateOffset == 7;
        }
    }

    private boolean isEighthFileExclusion(final int currentPiecePosition, final int currentCandidateOffset) {
        if (pawnDirection == -1) {
            return BoardUtils.EIGHTH_FILE[currentPiecePosition] && currentCandidateOffset == 7;
        } else {
            return BoardUtils.EIGHTH_FILE[currentPiecePosition] && currentCandidateOffset == 9;
        }
    }

    @Override
    public Piece movePiece(Move move) {
        return new Pawn(move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance(), false);
    }
}
