package com.chess.engine.board;

import com.chess.engine.move.Move;
import com.chess.engine.piece.Bishop;
import com.chess.engine.piece.King;
import com.chess.engine.piece.Knight;
import com.chess.engine.piece.Pawn;
import com.chess.engine.piece.Piece;
import com.chess.engine.piece.Queen;
import com.chess.engine.piece.Rook;
import com.chess.engine.player.BlackPlayer;
import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;
import com.chess.engine.util.Alliance;
import com.chess.engine.util.BoardUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Board {

    public final List<Tile> gameBoard;
    private final List<Piece> whitePieces;
    private final List<Piece> blackPieces;
    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;
    private final Alliance moveMaker;
    private final Pawn enPassantPawn;

    private Board(final Builder builder) {
        gameBoard = createGameBoard(builder);
        whitePieces = calculatePlayerActivePieces(gameBoard, Alliance.WHITE);
        blackPieces = calculatePlayerActivePieces(gameBoard, Alliance.BLACK);
        enPassantPawn = builder.enPassantPawn;
        
        moveMaker = builder.moveMaker;

        final List<Move> whiteStandardLegalMoves = calculateLegalMoves(whitePieces);
        final List<Move> blackStandardLegalMoves = calculateLegalMoves(blackPieces);

        whitePlayer = new WhitePlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        blackPlayer = new BlackPlayer(this, blackStandardLegalMoves, whiteStandardLegalMoves);
        currentPlayer = builder.moveMaker.choosePlayer(whitePlayer, blackPlayer);
    }

    private static List<Tile> createGameBoard(final Builder builder) {
        final List<Tile> tiles = new ArrayList<>();

        for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
            tiles.add(Tile.createTile(i, builder.boardConfig.get(i)));
        }

        return Collections.unmodifiableList(tiles);
    }

    private static List<Piece> calculatePlayerActivePieces(final List<Tile> gameBoard, final Alliance alliance) {
        final List<Piece> activePieces = new ArrayList<>();

        for (final Tile tile : gameBoard) {
            if (tile.isTileOccupied()) {
                final Piece piece = tile.getPiece();

                if (piece.getPieceAlliance() == alliance) {
                    activePieces.add(piece);
                }
            }
        }

        return Collections.unmodifiableList(activePieces);
    }

    private List<Move> calculateLegalMoves(final List<Piece> pieces) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final Piece piece : pieces) {
            legalMoves.addAll(piece.calculateLegalMoves(this));
        }

        return legalMoves;
    }

    public static Board createStandardBoard() {
        final Builder builder = new Builder();
        final Board board;

        builder.setPiece(new Rook(0, Alliance.BLACK, true));
        builder.setPiece(new Knight(1, Alliance.BLACK, true));
        builder.setPiece(new Bishop(2, Alliance.BLACK, true));
        builder.setPiece(new Queen(3, Alliance.BLACK, true));
        builder.setPiece(new King(4, Alliance.BLACK, true));
        builder.setPiece(new Bishop(5, Alliance.BLACK, true));
        builder.setPiece(new Knight(6, Alliance.BLACK, true));
        builder.setPiece(new Rook(7, Alliance.BLACK, true));
        builder.setPiece(new Pawn(8, Alliance.BLACK, true));
        builder.setPiece(new Pawn(9, Alliance.BLACK, true));
        builder.setPiece(new Pawn(10, Alliance.BLACK, true));
        builder.setPiece(new Pawn(11, Alliance.BLACK, true));
        builder.setPiece(new Pawn(12, Alliance.BLACK, true));
        builder.setPiece(new Pawn(13, Alliance.BLACK, true));
        builder.setPiece(new Pawn(14, Alliance.BLACK, true));
        builder.setPiece(new Pawn(15, Alliance.BLACK, true));

        builder.setPiece(new Pawn(48, Alliance.WHITE, true));
        builder.setPiece(new Pawn(49, Alliance.WHITE, true));
        builder.setPiece(new Pawn(50, Alliance.WHITE, true));
        builder.setPiece(new Pawn(51, Alliance.WHITE, true));
        builder.setPiece(new Pawn(52, Alliance.WHITE, true));
        builder.setPiece(new Pawn(53, Alliance.WHITE, true));
        builder.setPiece(new Pawn(54, Alliance.WHITE, true));
        builder.setPiece(new Pawn(55, Alliance.WHITE, true));
        builder.setPiece(new Rook(56, Alliance.WHITE, true));
        builder.setPiece(new Knight(57, Alliance.WHITE, true));
        builder.setPiece(new Bishop(58, Alliance.WHITE, true));
        builder.setPiece(new Queen(59, Alliance.WHITE, true));
        builder.setPiece(new King(60, Alliance.WHITE, true));
        builder.setPiece(new Bishop(61, Alliance.WHITE, true));
        builder.setPiece(new Knight(62, Alliance.WHITE, true));
        builder.setPiece(new Rook(63, Alliance.WHITE, true));

        builder.setMoveMaker(Alliance.WHITE);

        board = builder.build();
        
        return board;
    }

    public List<Piece> getWhitePieces() {
        return whitePieces;
    }

    public List<Piece> getBlackPieces() {
        return blackPieces;
    }
    
    public Pawn getEnPassantPawn() {
        return enPassantPawn;
    }

    public WhitePlayer getWhitePlayer() {
        return whitePlayer;
    }

    public BlackPlayer getBlackPlayer() {
        return blackPlayer;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    public Alliance getMoveMaker() {
        return moveMaker;
    }

    public Tile getTile(final int tileCoordinate) {
        return gameBoard.get(tileCoordinate);
    }

    public List<Move> getAllLegalMoves() {
        return Collections.unmodifiableList(Stream.concat(whitePlayer.getLegalMoves().stream(), blackPlayer.getLegalMoves().stream()).toList());
    }

    public void print() {
        int cont = 0;

        for (Tile tile : gameBoard) {

            if (tile.getPiece() != null) {
                if (tile.getPiece().getPieceAlliance().isWhite()) {
                    System.out.print(tile.getPiece().toString() + " ");
                } else if (tile.getPiece().getPieceAlliance().isBlack()) {
                    System.out.print(tile.getPiece().toString().toLowerCase() + " ");
                }
            } else {
                System.out.print("- ");
            }

            if (cont == 7 || cont == 15 || cont == 23 || cont == 31 || cont == 39 || cont == 47 || cont == 55 || cont == 63) {
                System.out.println("");
            }

            cont++;
        }
    }

    public static class Builder {

        Map<Integer, Piece> boardConfig = new HashMap<>();
        Alliance moveMaker;
        Pawn enPassantPawn;

        public Builder() {
        }

        public void setPiece(final Piece piece) {
            boardConfig.put(piece.getPiecePosition(), piece);
        }

        public void setMoveMaker(final Alliance moveMaker) {
            this.moveMaker = moveMaker;
        }

        public void setEnPassantPawn(final Pawn enPassantPawn) {
            this.enPassantPawn = enPassantPawn;
            boardConfig.put(enPassantPawn.getPiecePosition(), enPassantPawn);
        }
        
        public Board build() {
            return new Board(this);
        }
    }
}
