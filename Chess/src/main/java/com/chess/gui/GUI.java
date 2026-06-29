package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.Tile;
import com.chess.engine.move.Move;
import com.chess.engine.move.Move.MoveFactory;
import com.chess.engine.piece.Bishop;
import com.chess.engine.piece.Knight;
import com.chess.engine.piece.Piece;
import com.chess.engine.piece.Queen;
import com.chess.engine.piece.Rook;
import com.chess.engine.util.Alliance;
import com.chess.engine.util.BoardUtils;
import com.chess.engine.util.RoundButton;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GUI {

    private final JFrame gameFrame;
    private Board chessBoard;
    private final BoardPanel boardPanel;
    private final PromotionPanel promotionPanel;
    private final JLabel whitePlayerLabel;
    private final JLabel blackPlayerLabel;
    private final WhiteCapturedPiecesPanel whiteCapturedPiecesPanel;
    private final BlackCapturedPiecesPanel blackCapturedPiecesPanel;
    private final PlayAgainPanel playAgainPanel;

    private Tile selectedTile;
    private Tile destinationTile;
    private Tile attackingTile;
    private Tile attackedTile;
    private Piece selectedPiece;
    private Move candidateMove;

    private boolean highlightLegalMoves;
    private boolean isBoardFlipped;
    private boolean isCurrentPlayerInCheckMate;
    private boolean isCurrentPlayerInStalemate;

    private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(816, 983);
    private final static String DEFAULT_CAPTURED_PIECES_IMAGES_PATH = "/resources/capturedPieces/";
    private final static String DEFAULT_MISC_IMAGES_PATH = "/resources/misc/";
    private final static String DEFAULT_PIECES_IMAGES_PATH = "/resources/pieces/marhelado/";
    private final static String DEFAULT_SOUNDS_IMAGES_PATH = "/resources/sounds/";

    private final Color frameBackgroundColor = Color.decode("#312f2a");
    private final Color menuBarBackgroundColor = Color.decode("#272522");
    private final Color lightTileColor = Color.decode("#f1d8b5");
    private final Color darkTileColor = Color.decode("#b48963");
    private final Color checkTileColor = Color.decode("#ec2219");
    private final Color lightSelectedTile = Color.decode("#f7ed5a");
    private final Color darkSelectedTile = Color.decode("#dac332");

    public GUI() {
        gameFrame = new JFrame("Chess");

        chessBoard = Board.createStandardBoard();

        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setLayout(null);

        gameFrame.setLocation(460, 40);
        gameFrame.setSize(OUTER_FRAME_DIMENSION);

        Container c = gameFrame.getContentPane();

        boardPanel = new BoardPanel();
        promotionPanel = new PromotionPanel();
        whitePlayerLabel = new JLabel("Blancas");
        blackPlayerLabel = new JLabel("Negras");
        whiteCapturedPiecesPanel = new WhiteCapturedPiecesPanel();
        blackCapturedPiecesPanel = new BlackCapturedPiecesPanel();
        playAgainPanel = new PlayAgainPanel();

        final JMenuBar tableMenuBar = createTableMenuBar();

        whitePlayerLabel.setFont(new Font("Arial", Font.BOLD, 15));
        whitePlayerLabel.setForeground(Color.decode("#b7b6b5"));
        whitePlayerLabel.setBounds(5, 860, 100, 25);

        blackPlayerLabel.setFont(new Font("Arial", Font.BOLD, 15));
        blackPlayerLabel.setForeground(Color.decode("#b7b6b5"));
        blackPlayerLabel.setBounds(5, 0, 100, 25);

        c.setBackground(frameBackgroundColor);
        c.add(promotionPanel);
        c.add(playAgainPanel);
        c.add(boardPanel);
        c.add(whitePlayerLabel);
        c.add(blackPlayerLabel);
        c.add(blackCapturedPiecesPanel);
        c.add(whiteCapturedPiecesPanel);
        gameFrame.setJMenuBar(tableMenuBar);

        gameFrame.setVisible(true);
        gameFrame.setResizable(false);
        gameFrame.setDefaultCloseOperation(2);

        highlightLegalMoves = true;
        isBoardFlipped = false;
        isCurrentPlayerInCheckMate = chessBoard.getCurrentPlayer().isInCheckMate();
        isCurrentPlayerInStalemate = chessBoard.getCurrentPlayer().isInStaleMate();
    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();

        tableMenuBar.setBackground(menuBarBackgroundColor);
        tableMenuBar.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        tableMenuBar.add(createPreferencesMenu());
        tableMenuBar.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                promotionPanel.setVisible(false);

                if (candidateMove != null) {
                    candidateMove.setPromotionPiece(null);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        return tableMenuBar;
    }

    private JMenu createPreferencesMenu() {
        final JMenu preferencesMenu = new JMenu("Preferences");

        preferencesMenu.setForeground(Color.decode("#b7b6b5"));
        preferencesMenu.getPopupMenu().setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        preferencesMenu.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                promotionPanel.setVisible(false);

                if (candidateMove != null) {
                    candidateMove.setPromotionPiece(null);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        final JMenuItem flipBoard = new JMenuItem("Flip board");

        flipBoard.setBorder(BorderFactory.createLineBorder(menuBarBackgroundColor, 2));
        flipBoard.setForeground(Color.decode("#b7b6b5"));
        flipBoard.setBackground(menuBarBackgroundColor);
        flipBoard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Collections.reverse(boardPanel.boardTiles);
                isBoardFlipped = !isBoardFlipped;
                if (isBoardFlipped) {
                    whitePlayerLabel.setLocation(5, 0);
                    blackPlayerLabel.setLocation(5, 860);
                    whiteCapturedPiecesPanel.setLocation(5, 25);
                    blackCapturedPiecesPanel.setLocation(5, 885);
                } else {
                    whitePlayerLabel.setLocation(5, 860);
                    blackPlayerLabel.setLocation(5, 0);
                    whiteCapturedPiecesPanel.setLocation(5, 885);
                    blackCapturedPiecesPanel.setLocation(5, 25);
                }

                boardPanel.drawBoard(chessBoard);
            }
        });

        final JCheckBoxMenuItem highlightLegalMovesCheckBox = new JCheckBoxMenuItem("Highlight legal moves", true);

        highlightLegalMovesCheckBox.setBorder(BorderFactory.createLineBorder(menuBarBackgroundColor, 2));
        highlightLegalMovesCheckBox.setForeground(Color.decode("#b7b6b5"));
        highlightLegalMovesCheckBox.setBackground(menuBarBackgroundColor);
        highlightLegalMovesCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightLegalMoves = highlightLegalMovesCheckBox.isSelected();
                boardPanel.drawBoard(chessBoard);
            }
        });

        preferencesMenu.add(flipBoard);
        preferencesMenu.add(highlightLegalMovesCheckBox);

        return preferencesMenu;
    }

    public Board getChessBoard() {
        return chessBoard;
    }

    private void playSound(Move move) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream audioStream;

        if (chessBoard.getCurrentPlayer().isInCheck()) {
            audioStream = AudioSystem.getAudioInputStream(getClass().getResource(DEFAULT_SOUNDS_IMAGES_PATH + "checkMoveSound.wav"));
        } else if (move.isCastlingMove()) {
            audioStream = AudioSystem.getAudioInputStream(getClass().getResource(DEFAULT_SOUNDS_IMAGES_PATH + "castlingMoveSound.wav"));
        } else if (move.isPromotionMove()) {
            audioStream = AudioSystem.getAudioInputStream(getClass().getResource(DEFAULT_SOUNDS_IMAGES_PATH + "promotionMoveSound.wav"));
        } else if (move.isAttackingMove()) {
            audioStream = AudioSystem.getAudioInputStream(getClass().getResource(DEFAULT_SOUNDS_IMAGES_PATH + "attackMoveSound.wav"));
        } else {
            audioStream = AudioSystem.getAudioInputStream(getClass().getResource(DEFAULT_SOUNDS_IMAGES_PATH + "majorMoveSound.wav"));
        }

        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        clip.start();
    }

    private class BoardPanel extends JPanel {

        private final List<TilePanel> boardTiles;

        BoardPanel() {
            setLayout(new GridLayout(8, 8));
            setBounds(0, 60, 800, 800);
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            boardTiles = new ArrayList<>();

            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final TilePanel tilePanel = new TilePanel(this, i);

                boardTiles.add(tilePanel);

                add(tilePanel);
            }
        }

        private void drawBoard(final Board board) {
            removeAll();

            for (final TilePanel tilePanel : boardTiles) {
                tilePanel.drawTile(board);
                add(tilePanel);
            }

            validate();
            repaint();
        }
    }

    private class TilePanel extends JPanel {

        private final int tileId;

        TilePanel(final BoardPanel boardPanel, final int tileId) {
            setLayout(null);

            this.tileId = tileId;

            assignTilePieceImage(chessBoard);

            assignTileBackground();

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        promotionPanel.setVisible(false);

                        if (!isCurrentPlayerInCheckMate && !isCurrentPlayerInStalemate) {
                            if (candidateMove != null) {
                                candidateMove.setPromotionPiece(null);
                            }

                            if (selectedTile == null) {
                                selectedTile = chessBoard.getTile(tileId);

                                if (selectedTile.isTileOccupied()) {
                                    selectedPiece = selectedTile.getPiece();
                                    boardPanel.drawBoard(chessBoard);
                                } else {
                                    selectedTile = null;
                                    selectedPiece = null;
                                    destinationTile = null;
                                }
                            } else {
                                destinationTile = chessBoard.getTile(tileId);

                                if (destinationTile.getTileCoordinate() != selectedTile.getTileCoordinate()) {
                                    candidateMove = MoveFactory.createMove(chessBoard, selectedTile.getTileCoordinate(), destinationTile.getTileCoordinate());

                                    if (candidateMove != null && !Move.leavesPlayerInCheck(chessBoard, candidateMove)) {
                                        if (candidateMove.getMoveType().equals("PawnPromotionMove") || candidateMove.getMoveType().equals("PawnPromotionAttackMove")) {
                                            if (highlightLegalMoves) {
                                                highlightLegalMoves = false;
                                                boardPanel.drawBoard(chessBoard);
                                                highlightLegalMoves = true;
                                            }

                                            if (!isBoardFlipped) {
                                                if (BoardUtils.EIGHTH_RANK[candidateMove.getDestinationCoordinate()]) {
                                                    promotionPanel.setLocation(candidateMove.getDestinationCoordinate() * 100, 60);
                                                } else if (BoardUtils.FIRST_RANK[candidateMove.getDestinationCoordinate()]) {
                                                    promotionPanel.setLocation((candidateMove.getDestinationCoordinate() - 56) * 100, 410);
                                                }
                                            } else {
                                                if (BoardUtils.EIGHTH_RANK[candidateMove.getDestinationCoordinate()]) {
                                                    switch (candidateMove.getDestinationCoordinate() * 100) {
                                                        case 0 ->
                                                            promotionPanel.setLocation(700, 410);
                                                        case 100 ->
                                                            promotionPanel.setLocation(600, 410);
                                                        case 200 ->
                                                            promotionPanel.setLocation(500, 410);
                                                        case 300 ->
                                                            promotionPanel.setLocation(400, 410);
                                                        case 400 ->
                                                            promotionPanel.setLocation(300, 410);
                                                        case 500 ->
                                                            promotionPanel.setLocation(200, 410);
                                                        case 600 ->
                                                            promotionPanel.setLocation(100, 410);
                                                        case 700 ->
                                                            promotionPanel.setLocation(0, 410);
                                                    }
                                                } else if (BoardUtils.FIRST_RANK[candidateMove.getDestinationCoordinate()]) {
                                                    switch ((candidateMove.getDestinationCoordinate() - 56) * 100) {
                                                        case 0 ->
                                                            promotionPanel.setLocation(700, 60);
                                                        case 100 ->
                                                            promotionPanel.setLocation(600, 60);
                                                        case 200 ->
                                                            promotionPanel.setLocation(500, 60);
                                                        case 300 ->
                                                            promotionPanel.setLocation(400, 60);
                                                        case 400 ->
                                                            promotionPanel.setLocation(300, 60);
                                                        case 500 ->
                                                            promotionPanel.setLocation(200, 60);
                                                        case 600 ->
                                                            promotionPanel.setLocation(100, 60);
                                                        case 700 ->
                                                            promotionPanel.setLocation(0, 60);
                                                    }
                                                }
                                            }

                                            promotionPanel.drawPromotionPanel();
                                            promotionPanel.setVisible(true);
                                            candidateMove.setPromotionPiece(null);

                                            selectedTile = null;
                                            selectedPiece = null;
                                            destinationTile = null;

                                            boardPanel.drawBoard(chessBoard);
                                        } else {
                                            if (destinationTile.isTileOccupied()) {
                                                if (destinationTile.getPiece().getPieceAlliance().isWhite()) {
                                                    blackCapturedPiecesPanel.blackCapturedPieces.add(destinationTile.getPiece());
                                                } else {
                                                    whiteCapturedPiecesPanel.whiteCapturedPieces.add(destinationTile.getPiece());
                                                }
                                            } else if (candidateMove.getMoveType().equals("EnPassantMove")) {
                                                if (chessBoard.getEnPassantPawn().getPieceAlliance().isWhite()) {
                                                    blackCapturedPiecesPanel.blackCapturedPieces.add(chessBoard.getEnPassantPawn());
                                                } else {
                                                    whiteCapturedPiecesPanel.whiteCapturedPieces.add(chessBoard.getEnPassantPawn());
                                                }
                                            }

                                            chessBoard = chessBoard.getCurrentPlayer().makeMove(candidateMove);

                                            try {
                                                playSound(candidateMove);
                                            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                                                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                                            }

                                            attackingTile = selectedTile;
                                            attackedTile = destinationTile;
                                            selectedTile = null;
                                            selectedPiece = null;
                                            destinationTile = null;
                                            boardPanel.drawBoard(chessBoard);
                                            whiteCapturedPiecesPanel.drawWhiteCapturedPiecesPanel();
                                            blackCapturedPiecesPanel.drawBlackCapturedPiecesPanel();
                                            System.out.println("");
                                            chessBoard.print();

                                            isCurrentPlayerInCheckMate = chessBoard.getCurrentPlayer().isInCheckMate();
                                            isCurrentPlayerInStalemate = chessBoard.getCurrentPlayer().isInStaleMate();
                                        }

                                        if (isCurrentPlayerInCheckMate || isCurrentPlayerInStalemate) {
                                            playAgainPanel.showPlayAgainPanel();
                                            playAgainPanel.setVisible(true);
                                        }
                                    } else {
                                        if (destinationTile.isTileOccupied()) {
                                            selectedTile = destinationTile;
                                            selectedPiece = selectedTile.getPiece();
                                            destinationTile = null;
                                            boardPanel.drawBoard(chessBoard);
                                        } else {
                                            selectedTile = null;
                                            selectedPiece = null;
                                            destinationTile = null;
                                            boardPanel.drawBoard(chessBoard);
                                        }
                                    }
                                } else {
                                    selectedTile = null;
                                    selectedPiece = null;
                                    destinationTile = null;
                                    boardPanel.drawBoard(chessBoard);
                                }
                            }
                        }
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });
        }

        private void assignTileBackground() {
            JLabel rankLabel = new JLabel();
            rankLabel.setFont(new Font("Arial", Font.BOLD, 23));
            rankLabel.setBounds(5, 5, 25, 25);

            JLabel fileLabel = new JLabel();
            fileLabel.setFont(new Font("Arial", Font.BOLD, 23));
            fileLabel.setBounds(80, 73, 25, 25);

            if (!isBoardFlipped) {
                if (tileId == 0) {
                    rankLabel.setText("8");
                    rankLabel.setForeground(darkTileColor);
                    add(rankLabel);
                }

                if (tileId == 8) {
                    rankLabel.setText("7");
                    rankLabel.setForeground(lightTileColor);
                    add(rankLabel);
                }

                if (tileId == 16) {
                    rankLabel.setText("6");
                    rankLabel.setForeground(darkTileColor);
                    add(rankLabel);
                }

                if (tileId == 24) {
                    rankLabel.setText("5");
                    rankLabel.setForeground(lightTileColor);
                    add(rankLabel);
                }

                if (tileId == 32) {
                    rankLabel.setText("4");
                    rankLabel.setForeground(darkTileColor);
                    add(rankLabel);
                }

                if (tileId == 40) {
                    rankLabel.setText("3");
                    rankLabel.setForeground(lightTileColor);
                    add(rankLabel);
                }

                if (tileId == 48) {
                    rankLabel.setText("2");
                    rankLabel.setForeground(darkTileColor);
                    add(rankLabel);
                }

                if (tileId == 56) {
                    rankLabel.setText("1");
                    rankLabel.setForeground(lightTileColor);
                    fileLabel.setText("a");
                    fileLabel.setForeground(lightTileColor);
                    add(rankLabel);
                    add(fileLabel);
                }

                if (tileId == 57) {
                    fileLabel.setText("b");
                    fileLabel.setForeground(darkTileColor);
                    add(fileLabel);
                }

                if (tileId == 58) {
                    fileLabel.setText("c");
                    fileLabel.setForeground(lightTileColor);
                    add(fileLabel);
                }

                if (tileId == 59) {
                    fileLabel.setText("d");
                    fileLabel.setForeground(darkTileColor);
                    add(fileLabel);
                }

                if (tileId == 60) {
                    fileLabel.setText("e");
                    fileLabel.setForeground(lightTileColor);
                    add(fileLabel);
                }

                if (tileId == 61) {
                    fileLabel.setText("f");
                    fileLabel.setForeground(darkTileColor);
                    add(fileLabel);
                }

                if (tileId == 62) {
                    fileLabel.setLocation(80, 69);
                    fileLabel.setText("g");
                    fileLabel.setForeground(lightTileColor);
                    add(fileLabel);
                }

                if (tileId == 63) {
                    fileLabel.setText("h");
                    fileLabel.setForeground(darkTileColor);
                    add(fileLabel);
                }
            } else {
                if (tileId == 63) {
                    rankLabel.setText("1");
                    rankLabel.setForeground(darkTileColor);
                    add(rankLabel);
                }

                if (tileId == 55) {
                    rankLabel.setText("2");
                    rankLabel.setForeground(lightTileColor);
                    add(rankLabel);
                }

                if (tileId == 47) {
                    rankLabel.setText("3");
                    rankLabel.setForeground(darkTileColor);
                    add(rankLabel);
                }

                if (tileId == 39) {
                    rankLabel.setText("4");
                    rankLabel.setForeground(lightTileColor);
                    add(rankLabel);
                }

                if (tileId == 31) {
                    rankLabel.setText("5");
                    rankLabel.setForeground(darkTileColor);
                    add(rankLabel);
                }

                if (tileId == 23) {
                    rankLabel.setText("6");
                    rankLabel.setForeground(lightTileColor);
                    add(rankLabel);
                }

                if (tileId == 15) {
                    rankLabel.setText("7");
                    rankLabel.setForeground(darkTileColor);
                    add(rankLabel);
                }

                if (tileId == 7) {
                    rankLabel.setText("8");
                    rankLabel.setForeground(lightTileColor);
                    fileLabel.setText("h");
                    fileLabel.setForeground(lightTileColor);
                    add(rankLabel);
                    add(fileLabel);
                }

                if (tileId == 6) {
                    fileLabel.setLocation(80, 69);
                    fileLabel.setText("g");
                    fileLabel.setForeground(darkTileColor);
                    add(fileLabel);
                }

                if (tileId == 5) {
                    fileLabel.setText("f");
                    fileLabel.setForeground(lightTileColor);
                    add(fileLabel);
                }

                if (tileId == 4) {
                    fileLabel.setText("e");
                    fileLabel.setForeground(darkTileColor);
                    add(fileLabel);
                }

                if (tileId == 3) {
                    fileLabel.setText("d");
                    fileLabel.setForeground(lightTileColor);
                    add(fileLabel);
                }

                if (tileId == 2) {
                    fileLabel.setText("c");
                    fileLabel.setForeground(darkTileColor);
                    add(fileLabel);
                }

                if (tileId == 1) {
                    fileLabel.setText("b");
                    fileLabel.setForeground(lightTileColor);
                    add(fileLabel);
                }

                if (tileId == 0) {
                    fileLabel.setText("a");
                    fileLabel.setForeground(darkTileColor);
                    add(fileLabel);
                }
            }

            if (BoardUtils.EIGHTH_RANK[tileId] || BoardUtils.SIXTH_RANK[tileId] || BoardUtils.FOURTH_RANK[tileId] || BoardUtils.SECOND_RANK[tileId]) {
                if (tileId % 2 == 0) {
                    if (selectedTile != null && selectedTile.getTileCoordinate() == tileId) {
                        setBackground(lightSelectedTile);
                    } else if (destinationTile != null && destinationTile.getTileCoordinate() == tileId) {
                        setBackground(lightSelectedTile);
                    } else if (attackingTile != null && attackingTile.getTileCoordinate() == tileId) {
                        setBackground(lightSelectedTile);
                    } else if (attackedTile != null && attackedTile.getTileCoordinate() == tileId) {
                        setBackground(lightSelectedTile);
                    } else {
                        setBackground(lightTileColor);
                    }
                } else {
                    if (selectedTile != null && selectedTile.getTileCoordinate() == tileId) {
                        setBackground(darkSelectedTile);
                    } else if (destinationTile != null && destinationTile.getTileCoordinate() == tileId) {
                        setBackground(darkSelectedTile);
                    } else if (attackingTile != null && attackingTile.getTileCoordinate() == tileId) {
                        setBackground(darkSelectedTile);
                    } else if (attackedTile != null && attackedTile.getTileCoordinate() == tileId) {
                        setBackground(darkSelectedTile);
                    } else {
                        setBackground(darkTileColor);
                    }
                }
            } else if (BoardUtils.SEVENTH_RANK[tileId] || BoardUtils.FIFTH_RANK[tileId] || BoardUtils.THIRD_RANK[tileId] || BoardUtils.FIRST_RANK[tileId]) {
                if (tileId % 2 != 0) {
                    if (selectedTile != null && selectedTile.getTileCoordinate() == tileId) {
                        setBackground(lightSelectedTile);
                    } else if (destinationTile != null && destinationTile.getTileCoordinate() == tileId) {
                        setBackground(lightSelectedTile);
                    } else if (attackingTile != null && attackingTile.getTileCoordinate() == tileId) {
                        setBackground(lightSelectedTile);
                    } else if (attackedTile != null && attackedTile.getTileCoordinate() == tileId) {
                        setBackground(lightSelectedTile);
                    } else {
                        setBackground(lightTileColor);
                    }
                } else {
                    if (selectedTile != null && selectedTile.getTileCoordinate() == tileId) {
                        setBackground(darkSelectedTile);
                    } else if (destinationTile != null && destinationTile.getTileCoordinate() == tileId) {
                        setBackground(darkSelectedTile);
                    } else if (attackingTile != null && attackingTile.getTileCoordinate() == tileId) {
                        setBackground(darkSelectedTile);
                    } else if (attackedTile != null && attackedTile.getTileCoordinate() == tileId) {
                        setBackground(darkSelectedTile);
                    } else {
                        setBackground(darkTileColor);
                    }
                }
            }

            if (chessBoard.getCurrentPlayer().isInCheck()) {
                if (chessBoard.getTile(tileId).isTileOccupied()) {
                    if (chessBoard.getTile(tileId).getPiece().getPieceType().isKing()) {
                        if (chessBoard.getTile(tileId).getPiece().getPieceAlliance() == chessBoard.getCurrentPlayer().getAlliance()) {
                            if (selectedPiece == null) {
                                setBackground(checkTileColor);
                            } else if (selectedPiece.getPiecePosition() != tileId) {
                                setBackground(checkTileColor);
                            }
                        }
                    }
                }
            }
        }

        private void assignTilePieceImage(final Board board) {
            if (board.getTile(tileId).isTileOccupied()) {
                ImageIcon pieceImg = new ImageIcon(getClass().getResource(DEFAULT_PIECES_IMAGES_PATH + board.getTile(tileId).getPiece().getPieceAlliance().toString().substring(0, 1) + board.getTile(tileId).getPiece().toString() + ".png"));
                Image auxImg = pieceImg.getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
                pieceImg = new ImageIcon(auxImg);
                JLabel pieceImgLabel = new JLabel(pieceImg);

                if (board.getTile(tileId).isTileOccupied()) {
                    if (board.getTile(tileId).getPiece().getPieceType().isRook()) {
                        pieceImgLabel.setBounds(1, -3, 100, 100);
                    } else {
                        pieceImgLabel.setBounds(0, 0, 100, 100);
                    }
                }

                add(pieceImgLabel);
            }
        }

        private void highlightLegalMoves(final Board board) {
            final List<Move> selectedPieceLegalMoves = new ArrayList<>();

            if (selectedPiece != null) {
                selectedPieceLegalMoves.addAll(selectedPiece.calculateLegalMoves(board));

                if (selectedPiece.getPieceType().isKing()) {
                    selectedPieceLegalMoves.addAll(board.getCurrentPlayer().calculateKingCastleMoves(board.getCurrentPlayer().getOpponent().getLegalMoves()));
                }
            }

            if (highlightLegalMoves && selectedPiece != null && selectedPiece.getPieceAlliance() == board.getCurrentPlayer().getAlliance()) {
                for (final Move pieceLegalMove : selectedPieceLegalMoves) {
                    if (tileId == pieceLegalMove.getDestinationCoordinate() && !Move.leavesPlayerInCheck(board, pieceLegalMove)) {
                        if (!board.getTile(tileId).isTileOccupied()) {
                            ImageIcon legalMoveMoveHintImg = new ImageIcon(getClass().getResource(DEFAULT_MISC_IMAGES_PATH + "legalMoveHint.png"));
                            Image auxImg = legalMoveMoveHintImg.getImage().getScaledInstance(44, 44, java.awt.Image.SCALE_SMOOTH);
                            legalMoveMoveHintImg = new ImageIcon(auxImg);
                            JLabel legalMoveHintLabel = new JLabel(legalMoveMoveHintImg);

                            legalMoveHintLabel.setBounds(30, 28, 44, 44);

                            add(legalMoveHintLabel);
                        } else {
                            ImageIcon legalAttackMoveHintImg = new ImageIcon(getClass().getResource(DEFAULT_MISC_IMAGES_PATH + "legalAttackMoveHint.png"));
                            Image auxImg = legalAttackMoveHintImg.getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
                            legalAttackMoveHintImg = new ImageIcon(auxImg);
                            JLabel legalAttackMoveHintLabel = new JLabel(legalAttackMoveHintImg);

                            legalAttackMoveHintLabel.setBounds(0, 0, 100, 100);

                            add(legalAttackMoveHintLabel);
                        }
                    }
                }
            }
        }

        private void drawTile(Board board) {
            removeAll();
            assignTilePieceImage(board);
            assignTileBackground();
            highlightLegalMoves(board);
            validate();
            repaint();
        }
    }

    private class PromotionPanel extends JPanel {

        public PromotionPanel() {
            setLayout(null);
            setSize(100, 450);
            setBackground(Color.WHITE);
            setVisible(false);
        }

        private void drawPromotionPanel() {
            removeAll();
            addQuuenImg();
            addKnightImg();
            addRookImg();
            addBishopImg();
            addCrossButtonImg();
            validate();
            repaint();
        }

        private void addQuuenImg() {
            ImageIcon quuenImg = new ImageIcon(getClass().getResource(DEFAULT_PIECES_IMAGES_PATH + "WQ.png"));
            Image auxImg = quuenImg.getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
            quuenImg = new ImageIcon(auxImg);

            JLabel quuenImgLabel = new JLabel(quuenImg);

            if (!isBoardFlipped) {
                if (candidateMove.getMovedPiece().getPieceAlliance().isWhite()) {
                    quuenImgLabel.setBounds(0, 0, 100, 100);
                } else {
                    quuenImgLabel.setBounds(0, 350, 100, 100);
                }
            } else {
                if (candidateMove.getMovedPiece().getPieceAlliance().isWhite()) {
                    quuenImgLabel.setBounds(0, 350, 100, 100);
                } else {
                    quuenImgLabel.setBounds(0, 0, 100, 100);
                }
            }

            quuenImgLabel.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    Tile promotionTile = chessBoard.getTile(candidateMove.getDestinationCoordinate());

                    if (promotionTile.isTileOccupied()) {
                        if (promotionTile.getPiece().getPieceAlliance().isWhite()) {
                            blackCapturedPiecesPanel.blackCapturedPieces.add(promotionTile.getPiece());
                        } else {
                            whiteCapturedPiecesPanel.whiteCapturedPieces.add(promotionTile.getPiece());
                        }
                    }

                    candidateMove.setPromotionPiece(new Queen(candidateMove.getDestinationCoordinate(), candidateMove.getMovedPiece().getPieceAlliance(), false));
                    chessBoard = chessBoard.getCurrentPlayer().makeMove(candidateMove);

                    try {
                        playSound(candidateMove);
                    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    attackingTile = chessBoard.getTile(candidateMove.getOriginCoordinate());
                    attackedTile = chessBoard.getTile(candidateMove.getDestinationCoordinate());
                    boardPanel.drawBoard(chessBoard);
                    whiteCapturedPiecesPanel.drawWhiteCapturedPiecesPanel();
                    blackCapturedPiecesPanel.drawBlackCapturedPiecesPanel();
                    setVisible(false);
                    candidateMove.setPromotionPiece(null);
                    System.out.println("");
                    chessBoard.print();

                    highlightLegalMoves = true;
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });

            add(quuenImgLabel);
        }

        private void addKnightImg() {
            ImageIcon knightImg = new ImageIcon(getClass().getResource(DEFAULT_PIECES_IMAGES_PATH + "WN.png"));
            Image auxImg = knightImg.getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
            knightImg = new ImageIcon(auxImg);

            JLabel knightImgLabel = new JLabel(knightImg);

            if (!isBoardFlipped) {
                if (candidateMove.getMovedPiece().getPieceAlliance().isWhite()) {
                    knightImgLabel.setBounds(0, 100, 100, 100);
                } else {
                    knightImgLabel.setBounds(0, 250, 100, 100);
                }
            } else {
                if (candidateMove.getMovedPiece().getPieceAlliance().isWhite()) {
                    knightImgLabel.setBounds(0, 250, 100, 100);
                } else {
                    knightImgLabel.setBounds(0, 100, 100, 100);
                }
            }

            knightImgLabel.addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    Tile promotionTile = chessBoard.getTile(candidateMove.getDestinationCoordinate());

                    if (promotionTile.isTileOccupied()) {
                        if (promotionTile.getPiece().getPieceAlliance().isWhite()) {
                            blackCapturedPiecesPanel.blackCapturedPieces.add(promotionTile.getPiece());
                        } else {
                            whiteCapturedPiecesPanel.whiteCapturedPieces.add(promotionTile.getPiece());
                        }
                    }

                    candidateMove.setPromotionPiece(new Knight(candidateMove.getDestinationCoordinate(), candidateMove.getMovedPiece().getPieceAlliance(), false));
                    chessBoard = chessBoard.getCurrentPlayer().makeMove(candidateMove);

                    try {
                        playSound(candidateMove);
                    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    attackingTile = chessBoard.getTile(candidateMove.getOriginCoordinate());
                    attackedTile = chessBoard.getTile(candidateMove.getDestinationCoordinate());
                    boardPanel.drawBoard(chessBoard);
                    whiteCapturedPiecesPanel.drawWhiteCapturedPiecesPanel();
                    blackCapturedPiecesPanel.drawBlackCapturedPiecesPanel();
                    setVisible(false);
                    candidateMove.setPromotionPiece(null);
                    System.out.println("");
                    chessBoard.print();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });

            add(knightImgLabel);
        }

        private void addRookImg() {
            ImageIcon rookImg = new ImageIcon(getClass().getResource(DEFAULT_PIECES_IMAGES_PATH + "WR.png"));
            Image auxImg = rookImg.getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
            rookImg = new ImageIcon(auxImg);

            JLabel rookImgLabel = new JLabel(rookImg);

            if (!isBoardFlipped) {
                if (candidateMove.getMovedPiece().getPieceAlliance().isWhite()) {
                    rookImgLabel.setBounds(0, 200, 100, 100);
                } else {
                    rookImgLabel.setBounds(0, 150, 100, 100);
                }
            } else {
                if (candidateMove.getMovedPiece().getPieceAlliance().isWhite()) {
                    rookImgLabel.setBounds(0, 150, 100, 100);
                } else {
                    rookImgLabel.setBounds(0, 200, 100, 100);
                }
            }

            rookImgLabel.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    Tile promotionTile = chessBoard.getTile(candidateMove.getDestinationCoordinate());

                    if (promotionTile.isTileOccupied()) {
                        if (promotionTile.getPiece().getPieceAlliance().isWhite()) {
                            blackCapturedPiecesPanel.blackCapturedPieces.add(promotionTile.getPiece());
                        } else {
                            whiteCapturedPiecesPanel.whiteCapturedPieces.add(promotionTile.getPiece());
                        }
                    }

                    candidateMove.setPromotionPiece(new Rook(candidateMove.getDestinationCoordinate(), candidateMove.getMovedPiece().getPieceAlliance(), false));
                    chessBoard = chessBoard.getCurrentPlayer().makeMove(candidateMove);

                    try {
                        playSound(candidateMove);
                    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    attackingTile = chessBoard.getTile(candidateMove.getOriginCoordinate());
                    attackedTile = chessBoard.getTile(candidateMove.getDestinationCoordinate());
                    boardPanel.drawBoard(chessBoard);
                    whiteCapturedPiecesPanel.drawWhiteCapturedPiecesPanel();
                    blackCapturedPiecesPanel.drawBlackCapturedPiecesPanel();
                    setVisible(false);
                    candidateMove.setPromotionPiece(null);
                    System.out.println("");
                    chessBoard.print();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });

            add(rookImgLabel);
        }

        private void addBishopImg() {
            ImageIcon bishopImg = new ImageIcon(getClass().getResource(DEFAULT_PIECES_IMAGES_PATH + "WB.png"));
            Image auxImg = bishopImg.getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
            bishopImg = new ImageIcon(auxImg);

            JLabel bishopImgLabel = new JLabel(bishopImg);

            if (!isBoardFlipped) {
                if (candidateMove.getMovedPiece().getPieceAlliance().isWhite()) {
                    bishopImgLabel.setBounds(0, 300, 100, 100);
                } else {
                    bishopImgLabel.setBounds(0, 50, 100, 100);
                }
            } else {
                if (candidateMove.getMovedPiece().getPieceAlliance().isWhite()) {
                    bishopImgLabel.setBounds(0, 50, 100, 100);
                } else {
                    bishopImgLabel.setBounds(0, 300, 100, 100);
                }
            }

            bishopImgLabel.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    Tile promotionTile = chessBoard.getTile(candidateMove.getDestinationCoordinate());

                    if (promotionTile.isTileOccupied()) {
                        if (promotionTile.getPiece().getPieceAlliance().isWhite()) {
                            blackCapturedPiecesPanel.blackCapturedPieces.add(promotionTile.getPiece());
                        } else {
                            whiteCapturedPiecesPanel.whiteCapturedPieces.add(promotionTile.getPiece());
                        }
                    }

                    candidateMove.setPromotionPiece(new Bishop(candidateMove.getDestinationCoordinate(), candidateMove.getMovedPiece().getPieceAlliance(), false));
                    chessBoard = chessBoard.getCurrentPlayer().makeMove(candidateMove);

                    try {
                        playSound(candidateMove);
                    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                        Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    attackingTile = chessBoard.getTile(candidateMove.getOriginCoordinate());
                    attackedTile = chessBoard.getTile(candidateMove.getDestinationCoordinate());
                    boardPanel.drawBoard(chessBoard);
                    whiteCapturedPiecesPanel.drawWhiteCapturedPiecesPanel();
                    blackCapturedPiecesPanel.drawBlackCapturedPiecesPanel();
                    setVisible(false);
                    candidateMove.setPromotionPiece(null);
                    System.out.println("");
                    chessBoard.print();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });

            add(bishopImgLabel);
        }

        private void addCrossButtonImg() {
            ImageIcon crossButtonImg = new ImageIcon(getClass().getResource(DEFAULT_MISC_IMAGES_PATH + "crossButton.png"));
            Image auxImg = crossButtonImg.getImage().getScaledInstance(100, 50, java.awt.Image.SCALE_SMOOTH);
            crossButtonImg = new ImageIcon(auxImg);

            JLabel crossButtonImgLabel = new JLabel(crossButtonImg);

            if (!isBoardFlipped) {
                if (candidateMove.getMovedPiece().getPieceAlliance().isWhite()) {
                    crossButtonImgLabel.setBounds(0, 400, 100, 50);
                } else {
                    crossButtonImgLabel.setBounds(0, 0, 100, 50);
                }
            } else {
                if (candidateMove.getMovedPiece().getPieceAlliance().isWhite()) {
                    crossButtonImgLabel.setBounds(0, 0, 100, 50);
                } else {
                    crossButtonImgLabel.setBounds(0, 400, 100, 50);
                }
            }

            crossButtonImgLabel.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    candidateMove.setPromotionPiece(null);
                    boardPanel.drawBoard(chessBoard);
                    setVisible(false);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });

            add(crossButtonImgLabel);
        }
    }

    private class WhiteCapturedPiecesPanel extends JPanel {

        private final ArrayList<Piece> whiteCapturedPieces;

        public WhiteCapturedPiecesPanel() {
            setLayout(new GridLayout(1, 32));
            setBounds(5, 885, 800, 25);
            setBackground(frameBackgroundColor);
            setVisible(true);
            whiteCapturedPieces = new ArrayList<>();
        }

        private void drawWhiteCapturedPiecesPanel() {
            removeAll();
            addCapturedPieces();
            validate();
            repaint();
        }

        private void addCapturedPieces() {
            int availableSlots = 32;

            if (!whiteCapturedPieces.isEmpty()) {
                for (Piece capturedPiece : whiteCapturedPieces) {
                    if (capturedPiece.getPieceAlliance() == Alliance.BLACK && capturedPiece.getPieceType() == Piece.PieceType.PAWN) {
                        ImageIcon pawnImg = new ImageIcon(getClass().getResource(DEFAULT_CAPTURED_PIECES_IMAGES_PATH + "BP.png"));
                        Image auxImg = pawnImg.getImage().getScaledInstance(18, 25, java.awt.Image.SCALE_SMOOTH);
                        pawnImg = new ImageIcon(auxImg);

                        JLabel pawnImgLabel = new JLabel(pawnImg);

                        add(pawnImgLabel);

                        availableSlots--;
                    }
                }

                for (Piece capturedPiece : whiteCapturedPieces) {
                    if (capturedPiece.getPieceAlliance() == Alliance.BLACK && capturedPiece.getPieceType() == Piece.PieceType.BISHOP) {
                        ImageIcon bishopImg = new ImageIcon(getClass().getResource(DEFAULT_CAPTURED_PIECES_IMAGES_PATH + "BB.png"));
                        Image auxImg = bishopImg.getImage().getScaledInstance(18, 25, java.awt.Image.SCALE_SMOOTH);
                        bishopImg = new ImageIcon(auxImg);

                        JLabel bishopImgLabel = new JLabel(bishopImg);

                        add(bishopImgLabel);

                        availableSlots--;
                    }
                }

                for (Piece capturedPiece : whiteCapturedPieces) {
                    if (capturedPiece.getPieceAlliance() == Alliance.BLACK && capturedPiece.getPieceType() == Piece.PieceType.KNIGHT) {
                        ImageIcon knightImg = new ImageIcon(getClass().getResource(DEFAULT_CAPTURED_PIECES_IMAGES_PATH + "BN.png"));
                        Image auxImg = knightImg.getImage().getScaledInstance(18, 25, java.awt.Image.SCALE_SMOOTH);
                        knightImg = new ImageIcon(auxImg);

                        JLabel knightImgLabel = new JLabel(knightImg);

                        add(knightImgLabel);

                        availableSlots--;
                    }
                }

                for (Piece capturedPiece : whiteCapturedPieces) {
                    if (capturedPiece.getPieceAlliance() == Alliance.BLACK && capturedPiece.getPieceType() == Piece.PieceType.ROOK) {
                        ImageIcon rookImg = new ImageIcon(getClass().getResource(DEFAULT_CAPTURED_PIECES_IMAGES_PATH + "BR.png"));
                        Image auxImg = rookImg.getImage().getScaledInstance(20, 25, java.awt.Image.SCALE_SMOOTH);
                        rookImg = new ImageIcon(auxImg);

                        JLabel rookImgLabel = new JLabel(rookImg);

                        add(rookImgLabel);

                        availableSlots--;
                    }
                }

                for (Piece capturedPiece : whiteCapturedPieces) {
                    if (capturedPiece.getPieceAlliance() == Alliance.BLACK && capturedPiece.getPieceType() == Piece.PieceType.QUEEN) {
                        ImageIcon queenImg = new ImageIcon(getClass().getResource(DEFAULT_CAPTURED_PIECES_IMAGES_PATH + "BQ.png"));
                        Image auxImg = queenImg.getImage().getScaledInstance(24, 25, java.awt.Image.SCALE_SMOOTH);
                        queenImg = new ImageIcon(auxImg);

                        JLabel queenImgLabel = new JLabel(queenImg);

                        add(queenImgLabel);

                        availableSlots--;
                    }
                }

                for (int j = 0; j < availableSlots; j++) {
                    JLabel emptyLabel = new JLabel("");
                    emptyLabel.setSize(25, 25);
                    add(emptyLabel);
                }
            }
        }
    }

    private class BlackCapturedPiecesPanel extends JPanel {

        private final ArrayList<Piece> blackCapturedPieces;

        public BlackCapturedPiecesPanel() {
            setLayout(new GridLayout(1, 32));
            setBounds(5, 25, 800, 25);
            setBackground(frameBackgroundColor);
            setVisible(true);
            blackCapturedPieces = new ArrayList<>();
        }

        private void drawBlackCapturedPiecesPanel() {
            removeAll();
            addCapturedPieces();
            validate();
            repaint();
        }

        private void addCapturedPieces() {
            int availableSlots = 32;

            if (!blackCapturedPieces.isEmpty()) {
                for (Piece capturedPiece : blackCapturedPieces) {
                    if (capturedPiece.getPieceAlliance() == Alliance.WHITE && capturedPiece.getPieceType() == Piece.PieceType.PAWN) {
                        ImageIcon pawnImg = new ImageIcon(getClass().getResource(DEFAULT_CAPTURED_PIECES_IMAGES_PATH + "WP.png"));
                        Image auxImg = pawnImg.getImage().getScaledInstance(19, 25, java.awt.Image.SCALE_SMOOTH);
                        pawnImg = new ImageIcon(auxImg);

                        JLabel pawnImgLabel = new JLabel(pawnImg);

                        add(pawnImgLabel);

                        availableSlots--;
                    }
                }

                for (Piece capturedPiece : blackCapturedPieces) {
                    if (capturedPiece.getPieceAlliance() == Alliance.WHITE && capturedPiece.getPieceType() == Piece.PieceType.BISHOP) {
                        ImageIcon bishopImg = new ImageIcon(getClass().getResource(DEFAULT_CAPTURED_PIECES_IMAGES_PATH + "WB.png"));
                        Image auxImg = bishopImg.getImage().getScaledInstance(18, 25, java.awt.Image.SCALE_SMOOTH);
                        bishopImg = new ImageIcon(auxImg);

                        JLabel bishopImgLabel = new JLabel(bishopImg);

                        add(bishopImgLabel);

                        availableSlots--;
                    }
                }

                for (Piece capturedPiece : blackCapturedPieces) {
                    if (capturedPiece.getPieceAlliance() == Alliance.WHITE && capturedPiece.getPieceType() == Piece.PieceType.KNIGHT) {
                        ImageIcon knightImg = new ImageIcon(getClass().getResource(DEFAULT_CAPTURED_PIECES_IMAGES_PATH + "WN.png"));
                        Image auxImg = knightImg.getImage().getScaledInstance(18, 25, java.awt.Image.SCALE_SMOOTH);
                        knightImg = new ImageIcon(auxImg);

                        JLabel knightImgLabel = new JLabel(knightImg);

                        add(knightImgLabel);

                        availableSlots--;
                    }
                }

                for (Piece capturedPiece : blackCapturedPieces) {
                    if (capturedPiece.getPieceAlliance() == Alliance.WHITE && capturedPiece.getPieceType() == Piece.PieceType.ROOK) {
                        ImageIcon rookImg = new ImageIcon(getClass().getResource(DEFAULT_CAPTURED_PIECES_IMAGES_PATH + "WR.png"));
                        Image auxImg = rookImg.getImage().getScaledInstance(19, 25, java.awt.Image.SCALE_SMOOTH);
                        rookImg = new ImageIcon(auxImg);

                        JLabel rookImgLabel = new JLabel(rookImg);

                        add(rookImgLabel);

                        availableSlots--;
                    }
                }

                for (Piece capturedPiece : blackCapturedPieces) {
                    if (capturedPiece.getPieceAlliance() == Alliance.WHITE && capturedPiece.getPieceType() == Piece.PieceType.QUEEN) {
                        ImageIcon queenImg = new ImageIcon(getClass().getResource(DEFAULT_CAPTURED_PIECES_IMAGES_PATH + "WQ.png"));
                        Image auxImg = queenImg.getImage().getScaledInstance(23, 25, java.awt.Image.SCALE_SMOOTH);
                        queenImg = new ImageIcon(auxImg);

                        JLabel queenImgLabel = new JLabel(queenImg);

                        add(queenImgLabel);

                        availableSlots--;
                    }
                }

                for (int j = 0; j < availableSlots; j++) {
                    JLabel emptyLabel = new JLabel("");
                    emptyLabel.setSize(25, 25);
                    add(emptyLabel);
                }
            }
        }
    }

    private class PlayAgainPanel extends JPanel {

        public PlayAgainPanel() {
            setLayout(null);
            setBounds(250, 260, 300, 400);
            setBackground(Color.decode("#666564"));
            setOpaque(false);
            setVisible(false);
        }

        private void showPlayAgainPanel() {
            removeAll();
            addTitleLabel();
            addReasonLabel();
            addStateImg();
            addPlayAgainButton();
            validate();
            repaint();
        }

        private void addTitleLabel() {
            JLabel titleLabel = new JLabel();

            if (isCurrentPlayerInCheckMate) {
                titleLabel.setText("Partida finalizada");
                titleLabel.setBounds(21, 10, 300, 35);
            } else if (isCurrentPlayerInStalemate) {
                titleLabel.setText("Tablas");
                titleLabel.setBounds(97, 10, 116, 35);
            }

            titleLabel.setFont(new Font("Rockwell Extra Bold", Font.TRUETYPE_FONT, 35));
            titleLabel.setForeground(Color.WHITE);

            add(titleLabel);
        }

        private void addReasonLabel() {
            JLabel reasonLabel = new JLabel();

            if (isCurrentPlayerInCheckMate) {
                reasonLabel.setText("por jaque mate");
                reasonLabel.setBounds(100, 45, 100, 18);
            } else if (isCurrentPlayerInStalemate) {
                reasonLabel.setText("por rey ahogado");
                reasonLabel.setBounds(97, 45, 116, 18);
            }

            reasonLabel.setFont(new Font("Rockwell Extra Bold", Font.TRUETYPE_FONT, 15));
            reasonLabel.setForeground(Color.GRAY);

            add(reasonLabel);
        }

        private void addStateImg() {
            if (isCurrentPlayerInCheckMate) {
                ImageIcon checkmateImg = new ImageIcon(getClass().getResource(DEFAULT_MISC_IMAGES_PATH + "checkmate.png"));
                Image auxImg = checkmateImg.getImage().getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH);
                checkmateImg = new ImageIcon(auxImg);
                JLabel checkmateImgLabel = new JLabel(checkmateImg);

                checkmateImgLabel.setBounds(75, 105, 150, 150);
                checkmateImgLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

                add(checkmateImgLabel);
            } else if (isCurrentPlayerInStalemate) {
                ImageIcon stalemateImg = new ImageIcon(getClass().getResource(DEFAULT_MISC_IMAGES_PATH + "stalemate.png"));
                Image auxImg = stalemateImg.getImage().getScaledInstance(100, 170, java.awt.Image.SCALE_SMOOTH);
                stalemateImg = new ImageIcon(auxImg);
                JLabel stalemateImgLabel = new JLabel(stalemateImg);

                stalemateImgLabel.setBounds(100, 95, 100, 170);
                stalemateImgLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

                add(stalemateImgLabel);
            }
        }

        private void addPlayAgainButton() {
            JButton playAgainButton = new RoundButton("Volver a jugar", 15);

            playAgainButton.setBounds(75, 300, 150, 50);
            playAgainButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    chessBoard = Board.createStandardBoard();
                    isCurrentPlayerInCheckMate = chessBoard.getCurrentPlayer().isInCheckMate();
                    isCurrentPlayerInStalemate = chessBoard.getCurrentPlayer().isInStaleMate();
                    setVisible(false);
                    selectedTile = null;
                    selectedPiece = null;
                    attackingTile = null;
                    attackedTile = null;
                    boardPanel.drawBoard(chessBoard);
                    whiteCapturedPiecesPanel.whiteCapturedPieces.removeAll(whiteCapturedPiecesPanel.whiteCapturedPieces);
                    blackCapturedPiecesPanel.blackCapturedPieces.removeAll(blackCapturedPiecesPanel.blackCapturedPieces);
                    whiteCapturedPiecesPanel.drawWhiteCapturedPiecesPanel();
                    blackCapturedPiecesPanel.drawBlackCapturedPiecesPanel();
                    System.out.println("");
                    chessBoard.print();
                }
            });

            add(playAgainButton);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Dimension arcs = new Dimension(15, 15);

            int width = getWidth();
            int height = getHeight();

            Graphics2D graphics = (Graphics2D) g;

            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.fillRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
            graphics.setColor(Color.BLACK);
            graphics.drawRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);
        }
    }
}
