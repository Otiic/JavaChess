package com.chess;

import com.chess.gui.GUI;

public class Chess {

    public static void main(String[] args) {
        GUI table = new GUI();
        
        table.getChessBoard().print();
    }
}
