package com.chess.engine.util;

import com.chess.engine.player.Player;
import com.chess.engine.player.WhitePlayer;
import com.chess.engine.player.BlackPlayer;

public enum Alliance {
    WHITE {
        @Override
        public boolean isWhite() {
            return true;
        }

        @Override
        public boolean isBlack() {
            return false;
        }

        @Override
        public Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer) {
            return whitePlayer;
        }

        @Override
        public int getDirection() {
            return -1;
        }
    }, BLACK {
        @Override
        public boolean isWhite() {
            return false;
        }

        @Override
        public boolean isBlack() {
            return true;
        }

        @Override
        public Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer) {
            return blackPlayer;
        }

        @Override
        public int getDirection() {
            return 1;
        }
    };

    public abstract boolean isWhite();

    public abstract boolean isBlack();

    public abstract Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer);

    public abstract int getDirection();

    @Override
    public String toString() {
        if (isWhite()) {
            return "W";
        } else {
            return "B";
        }
    }
}
