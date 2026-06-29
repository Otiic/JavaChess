package com.chess.engine.move;

import com.chess.engine.board.Board;
import com.chess.engine.board.Board.Builder;
import com.chess.engine.board.Tile;
import com.chess.engine.piece.Pawn;
import com.chess.engine.piece.Piece;
import com.chess.engine.piece.Queen;
import com.chess.engine.piece.Rook;
import com.chess.engine.util.Alliance;
import com.chess.engine.util.BoardUtils;
import java.util.Objects;

public abstract class Move {

    private final Piece movedPiece;
    private final Piece attackedPiece;
    private Piece promotionPiece;
    private final int originCoordinate;
    private final int destinationCoordinate;
    private final String moveType;

    private Move(Piece movedPiece, Piece attackedPiece, int originCoordinate, int destinationCoordinate, String moveType) {
        this.movedPiece = movedPiece;
        this.attackedPiece = attackedPiece;
        this.originCoordinate = originCoordinate;
        this.destinationCoordinate = destinationCoordinate;
        this.moveType = moveType;
    }

    public abstract Board executeMove(Board board, Move move);

    public abstract Board unmakeMove(Board board, Move move);

    public abstract boolean isAttackingMove();

    public abstract boolean isCastlingMove();

    public abstract boolean isPromotionMove();

    public static boolean leavesPlayerInCheck(Board board, Move move) {
        final Board testBoard = board.getCurrentPlayer().makeMove(move);

        return testBoard.getCurrentPlayer().getOpponent().isInCheck();
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    public Piece getAttackedPiece() {
        return attackedPiece;
    }

    public Piece getPromotionPiece() {
        return promotionPiece;
    }

    public void setPromotionPiece(Piece promotionPiece) {
        this.promotionPiece = promotionPiece;
    }

    public int getOriginCoordinate() {
        return originCoordinate;
    }

    public int getDestinationCoordinate() {
        return destinationCoordinate;
    }

    public String getMoveType() {
        return moveType;
    }

    @Override
    public String toString() {
        return "Move{" + "movedPiece=" + movedPiece + ", originCoordinate=" + originCoordinate + ", destinationCoordinate=" + destinationCoordinate + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Move other = (Move) obj;

        if (this.originCoordinate != other.originCoordinate) {
            return false;
        }
        if (this.destinationCoordinate != other.destinationCoordinate) {
            return false;
        }

        if (!Objects.equals(this.moveType, other.moveType)) {
            return false;
        }

        return Objects.equals(this.movedPiece, other.movedPiece);
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 29 * hash + Objects.hashCode(this.movedPiece);
        hash = 29 * hash + this.originCoordinate;
        hash = 29 * hash + this.destinationCoordinate;
        hash = 29 * hash + Objects.hashCode(this.moveType);

        return hash;
    }

    public static final class MajorMove extends Move {

        public MajorMove(Piece movedPiece, int originCoordinate, int destinationCoordinate) {
            super(movedPiece, null, originCoordinate, destinationCoordinate, "MajorMove");
        }

        @Override
        public Board executeMove(Board board, Move move) {
            final Builder builder = new Board.Builder();

            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                Tile tile = board.getTile(i);
                Piece pieceOnTile = tile.getPiece();
                Piece pieceToMove = move.movedPiece.movePiece(move);

                if (pieceOnTile != null) {
                    if (i != move.originCoordinate && i != move.destinationCoordinate) {
                        builder.setPiece(pieceOnTile);
                    }
                } else if (i == move.destinationCoordinate) {
                    builder.setPiece(pieceToMove);
                }
            }

            if (board.getCurrentPlayer().getAlliance().isWhite()) {
                builder.setMoveMaker(Alliance.BLACK);
            } else {
                builder.setMoveMaker(Alliance.WHITE);
            }

            return builder.build();
        }

        @Override
        public Board unmakeMove(Board board, Move move) {
            final Builder builder = new Board.Builder();

            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                Tile tile = board.getTile(i);
                Piece pieceOnTile = tile.getPiece();
                Piece pieceToMove = move.movedPiece.movePiece(move);

                if (pieceOnTile != null) {
                    if (i != move.destinationCoordinate) {
                        builder.setPiece(pieceOnTile);
                    }
                } else if (i == move.originCoordinate) {
                    builder.setPiece(pieceToMove);
                }
            }

            if (board.getCurrentPlayer().getAlliance().isWhite()) {
                builder.setMoveMaker(Alliance.BLACK);
            } else {
                builder.setMoveMaker(Alliance.WHITE);
            }

            return builder.build();
        }

        @Override
        public boolean isAttackingMove() {
            return false;
        }

        @Override
        public boolean isCastlingMove() {
            return false;
        }

        @Override
        public boolean isPromotionMove() {
            return false;
        }
    }

    public static final class MajorAttackMove extends Move {

        public MajorAttackMove(Piece movedPiece, Piece attackedPiece, int originCoordinate, int destinationCoordinate) {
            super(movedPiece, attackedPiece, originCoordinate, destinationCoordinate, "MajorAttackMove");
        }

        @Override
        public Board executeMove(Board board, Move move) {
            final Builder builder = new Board.Builder();

            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                Tile tile = board.getTile(i);
                Piece pieceOnTile = tile.getPiece();
                Piece pieceToMove = move.movedPiece.movePiece(move);

                if (pieceOnTile != null) {
                    if (i != move.originCoordinate && i != move.destinationCoordinate) {
                        builder.setPiece(pieceOnTile);
                    } else if (i == move.destinationCoordinate) {
                        builder.setPiece(pieceToMove);
                    }
                }
            }

            if (board.getCurrentPlayer().getAlliance().isWhite()) {
                builder.setMoveMaker(Alliance.BLACK);
            } else {
                builder.setMoveMaker(Alliance.WHITE);
            }

            return builder.build();
        }

        @Override
        public Board unmakeMove(Board board, Move move) {
            final Builder builder = new Board.Builder();

            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                Tile tile = board.getTile(i);
                Piece pieceOnTile = tile.getPiece();
                Piece pieceToMove = move.movedPiece.movePiece(move);

                if (pieceOnTile != null) {
                    if (i != move.destinationCoordinate) {
                        builder.setPiece(pieceOnTile);
                    } else {
                        builder.setPiece(move.attackedPiece);
                    }
                } else if (i == move.originCoordinate) {
                    builder.setPiece(pieceToMove);
                }
            }

            if (board.getCurrentPlayer().getAlliance().isWhite()) {
                builder.setMoveMaker(Alliance.BLACK);
            } else {
                builder.setMoveMaker(Alliance.WHITE);
            }

            return builder.build();
        }

        @Override
        public boolean isAttackingMove() {
            return true;
        }

        @Override
        public boolean isCastlingMove() {
            return false;
        }
        
        @Override
        public boolean isPromotionMove() {
            return false;
        }
    }

    public static final class PawnJumpMove extends Move {

        public PawnJumpMove(Piece movedPiece, int originCoordinate, int destinationCoordinate) {
            super(movedPiece, null, originCoordinate, destinationCoordinate, "PawnJumpMove");
        }

        @Override
        public Board executeMove(Board board, Move move) {
            final Builder builder = new Board.Builder();

            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                Tile tile = board.getTile(i);
                Piece pieceOnTile = tile.getPiece();
                Piece pieceToMove = move.movedPiece.movePiece(move);

                if (pieceOnTile != null) {
                    if (i != move.originCoordinate && i != move.destinationCoordinate) {
                        builder.setPiece(pieceOnTile);
                    }
                } else if (i == move.destinationCoordinate) {
                    builder.setEnPassantPawn((Pawn) pieceToMove);
                }
            }

            if (board.getCurrentPlayer().getAlliance().isWhite()) {
                builder.setMoveMaker(Alliance.BLACK);
            } else {
                builder.setMoveMaker(Alliance.WHITE);
            }

            return builder.build();
        }

        @Override
        public Board unmakeMove(Board board, Move move) {
            final Builder builder = new Board.Builder();

            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                Tile tile = board.getTile(i);
                Piece pieceOnTile = tile.getPiece();
                Piece pieceToMove = move.movedPiece.movePiece(move);

                if (pieceOnTile != null) {
                    if (i != move.destinationCoordinate) {
                        builder.setPiece(pieceOnTile);
                    }
                } else if (i == move.originCoordinate) {
                    builder.setPiece(pieceToMove);
                }
            }

            if (board.getCurrentPlayer().getAlliance().isWhite()) {
                builder.setMoveMaker(Alliance.BLACK);
            } else {
                builder.setMoveMaker(Alliance.WHITE);
            }

            return builder.build();
        }

        @Override
        public boolean isAttackingMove() {
            return false;
        }

        @Override
        public boolean isCastlingMove() {
            return false;
        }
        
        @Override
        public boolean isPromotionMove() {
            return false;
        }
    }

    public static final class EnPassantMove extends Move {

        public EnPassantMove(Piece movedPiece, Piece attackedPiece, int originCoordinate, int destinationCoordinate) {
            super(movedPiece, attackedPiece, originCoordinate, destinationCoordinate, "EnPassantMove");
        }

        @Override
        public Board executeMove(Board board, Move move) {
            final Builder builder = new Board.Builder();

            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                Tile tile = board.getTile(i);
                Piece pieceOnTile = tile.getPiece();
                Piece pawnToMove = move.movedPiece.movePiece(move);

                if (pieceOnTile != null) {
                    if (i != move.originCoordinate && i != move.destinationCoordinate && i != move.attackedPiece.getPiecePosition()) {
                        builder.setPiece(pieceOnTile);
                    }
                } else if (i == move.destinationCoordinate) {
                    builder.setPiece(pawnToMove);
                }
            }

            if (board.getCurrentPlayer().getAlliance().isWhite()) {
                builder.setMoveMaker(Alliance.BLACK);
            } else {
                builder.setMoveMaker(Alliance.WHITE);
            }

            return builder.build();
        }

        @Override
        public Board unmakeMove(Board board, Move move) {
            final Builder builder = new Board.Builder();

            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                Tile tile = board.getTile(i);
                Piece pieceOnTile = tile.getPiece();
                Piece pieceToMove = move.movedPiece.movePiece(move);

                if (pieceOnTile != null) {
                    if (i != move.destinationCoordinate && i != move.attackedPiece.getPiecePosition()) {
                        builder.setPiece(pieceOnTile);
                    } else if (i == move.attackedPiece.getPiecePosition()) {
                        builder.setPiece(move.attackedPiece);
                    }
                } else if (i == move.originCoordinate) {
                    builder.setPiece(pieceToMove);
                }
            }

            if (board.getCurrentPlayer().getAlliance().isWhite()) {
                builder.setMoveMaker(Alliance.BLACK);
            } else {
                builder.setMoveMaker(Alliance.WHITE);
            }

            return builder.build();
        }

        @Override
        public boolean isAttackingMove() {
            return true;
        }

        @Override
        public boolean isCastlingMove() {
            return false;
        }
        
        @Override
        public boolean isPromotionMove() {
            return false;
        }
    }

    public static final class PawnPromotionMove extends Move {

        public PawnPromotionMove(Piece pawnMovedPiece, int originCoordinate, int destinationCoordinate) {
            super(pawnMovedPiece, null, originCoordinate, destinationCoordinate, "PawnPromotionMove");
        }

        @Override
        public Board executeMove(Board board, Move move) {
            final Builder builder = new Board.Builder();

            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                Tile tile = board.getTile(i);
                Piece pieceOnTile = tile.getPiece();

                if (pieceOnTile != null) {
                    if (i != move.originCoordinate && i != move.destinationCoordinate) {
                        builder.setPiece(pieceOnTile);
                    }
                } else if (i == move.destinationCoordinate) {
                    if (move.promotionPiece != null) {
                        builder.setPiece(move.promotionPiece);
                    } else {
                        builder.setPiece(new Queen(move.destinationCoordinate, move.movedPiece.getPieceAlliance(), false));
                    }
                }
            }

            if (board.getCurrentPlayer().getAlliance().isWhite()) {
                builder.setMoveMaker(Alliance.BLACK);
            } else {
                builder.setMoveMaker(Alliance.WHITE);
            }

            return builder.build();
        }

        @Override
        public Board unmakeMove(Board board, Move move) {
            final Builder builder = new Board.Builder();

            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                Tile tile = board.getTile(i);
                Piece pieceOnTile = tile.getPiece();
                Piece pieceToMove = move.movedPiece.movePiece(move);

                if (pieceOnTile != null) {
                    if (i != move.destinationCoordinate) {
                        builder.setPiece(pieceOnTile);
                    }
                } else if (i == move.originCoordinate) {
                    builder.setPiece(pieceToMove);
                }
            }

            if (board.getCurrentPlayer().getAlliance().isWhite()) {
                builder.setMoveMaker(Alliance.BLACK);
            } else {
                builder.setMoveMaker(Alliance.WHITE);
            }

            return builder.build();
        }

        @Override
        public boolean isAttackingMove() {
            return false;
        }

        @Override
        public boolean isCastlingMove() {
            return false;
        }
        
        @Override
        public boolean isPromotionMove() {
            return true;
        }
    }

    public static final class PawnPromotionAttackMove extends Move {

        public PawnPromotionAttackMove(Piece pawnMovedPiece, Piece attackedPiece, int originCoordinate, int destinationCoordinate) {
            super(pawnMovedPiece, attackedPiece, originCoordinate, destinationCoordinate, "PawnPromotionAttackMove");
        }

        @Override
        public Board executeMove(Board board, Move move) {
            final Builder builder = new Board.Builder();

            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                Tile tile = board.getTile(i);
                Piece pieceOnTile = tile.getPiece();

                if (pieceOnTile != null) {
                    if (i != move.originCoordinate && i != move.destinationCoordinate) {
                        builder.setPiece(pieceOnTile);
                    } else if (i == move.destinationCoordinate) {
                        if (move.promotionPiece != null) {
                            builder.setPiece(move.promotionPiece);
                        } else {
                            builder.setPiece(new Queen(move.destinationCoordinate, move.movedPiece.getPieceAlliance(), false));
                        }
                    }
                }
            }

            if (board.getCurrentPlayer().getAlliance().isWhite()) {
                builder.setMoveMaker(Alliance.BLACK);
            } else {
                builder.setMoveMaker(Alliance.WHITE);
            }

            return builder.build();
        }

        @Override
        public Board unmakeMove(Board board, Move move) {
            final Builder builder = new Board.Builder();

            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                Tile tile = board.getTile(i);
                Piece pieceOnTile = tile.getPiece();
                Piece pieceToMove = move.movedPiece.movePiece(move);

                if (pieceOnTile != null) {
                    if (i != move.destinationCoordinate) {
                        builder.setPiece(pieceOnTile);
                    } else {
                        builder.setPiece(move.attackedPiece);
                    }
                } else if (i == move.originCoordinate) {
                    builder.setPiece(pieceToMove);
                }
            }

            if (board.getCurrentPlayer().getAlliance().isWhite()) {
                builder.setMoveMaker(Alliance.BLACK);
            } else {
                builder.setMoveMaker(Alliance.WHITE);
            }

            return builder.build();
        }

        @Override
        public boolean isAttackingMove() {
            return true;
        }

        @Override
        public boolean isCastlingMove() {
            return false;
        }
        
        @Override
        public boolean isPromotionMove() {
            return true;
        }
    }

    public static final class KingCastleMove extends Move {

        private final int kingsOriginCoordinate;
        private final int rooksOriginCoordinate;
        private final int kingsDestinationCoordinate;
        private final int rooksDestinationCoordinate;

        public KingCastleMove(Piece movedKing, int kingsOriginCoordinate, int rooksOriginCoordinate, int kingsDestinationCoordinate, int rooksDestinationCoordinate) {
            super(movedKing, null, kingsOriginCoordinate, kingsDestinationCoordinate, "KingCastleMove");

            this.kingsOriginCoordinate = kingsOriginCoordinate;
            this.kingsDestinationCoordinate = kingsDestinationCoordinate;
            this.rooksOriginCoordinate = rooksOriginCoordinate;
            this.rooksDestinationCoordinate = rooksDestinationCoordinate;
        }

        @Override
        public Board executeMove(Board board, Move move) {
            final Builder builder = new Board.Builder();

            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                Tile tile = board.getTile(i);
                Piece pieceOnTile = tile.getPiece();
                Piece kingToMove = move.movedPiece.movePiece(move);

                if (pieceOnTile != null) {
                    if (i != kingsOriginCoordinate && i != rooksOriginCoordinate) {
                        builder.setPiece(pieceOnTile);
                    }
                } else if (i == kingsDestinationCoordinate) {
                    builder.setPiece(kingToMove);
                } else if (i == rooksDestinationCoordinate) {
                    builder.setPiece(new Rook(i, board.getMoveMaker(), false));
                }
            }

            if (board.getCurrentPlayer().getAlliance().isWhite()) {
                builder.setMoveMaker(Alliance.BLACK);
            } else {
                builder.setMoveMaker(Alliance.WHITE);
            }

            return builder.build();
        }

        @Override
        public Board unmakeMove(Board board, Move move) {
            final Builder builder = new Board.Builder();

            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                Tile tile = board.getTile(i);
                Piece pieceOnTile = tile.getPiece();

                if (pieceOnTile != null) {
                    if (i != kingsDestinationCoordinate && i != rooksDestinationCoordinate) {
                        builder.setPiece(pieceOnTile);
                    }
                } else if (i == kingsOriginCoordinate) {
                    builder.setPiece(move.movedPiece);
                } else if (i == rooksOriginCoordinate) {
                    builder.setPiece(new Rook(i, board.getMoveMaker(), true));
                }
            }

            if (board.getCurrentPlayer().getAlliance().isWhite()) {
                builder.setMoveMaker(Alliance.BLACK);
            } else {
                builder.setMoveMaker(Alliance.WHITE);
            }

            return builder.build();
        }

        @Override
        public boolean isAttackingMove() {
            return false;
        }

        @Override
        public boolean isCastlingMove() {
            return true;
        }
        
        @Override
        public boolean isPromotionMove() {
            return false;
        }
    }

    public static class MoveFactory {

        private MoveFactory() {
            throw new RuntimeException("Not instantiatable!");
        }

        public static Move createMove(Board board, final int originCoordinate, final int destinationCoordinate) {
            for (final Move move : board.getAllLegalMoves()) {
                if (move.getOriginCoordinate() == originCoordinate && move.getDestinationCoordinate() == destinationCoordinate && move.movedPiece.getPieceAlliance() == board.getMoveMaker()) {
                    return move;
                }
            }

            return null;
        }
    }
}
