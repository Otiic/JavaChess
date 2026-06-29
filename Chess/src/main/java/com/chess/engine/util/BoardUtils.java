package com.chess.engine.util;

public class BoardUtils {

    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_PER_ROW = 8;

    public static final boolean[] FIRST_FILE = initFile(0);
    public static final boolean[] SECOND_FILE = initFile(1);
    public static final boolean[] THIRD_FILE = initRank(2);
    public static final boolean[] FOURTH_FILE = initRank(3);
    public static final boolean[] FIFTH_FILE = initRank(4);
    public static final boolean[] SIXTH_FILE = initRank(5);
    public static final boolean[] SEVENTH_FILE = initFile(6);
    public static final boolean[] EIGHTH_FILE = initFile(7);

    public static final boolean[] FIRST_RANK = initRank(56);
    public static final boolean[] SECOND_RANK = initRank(48);
    public static final boolean[] THIRD_RANK = initRank(40);
    public static final boolean[] FOURTH_RANK = initRank(32);
    public static final boolean[] FIFTH_RANK = initRank(24);
    public static final boolean[] SIXTH_RANK = initRank(16);
    public static final boolean[] SEVENTH_RANK = initRank(8);
    public static final boolean[] EIGHTH_RANK = initRank(0);

    private BoardUtils() {
        throw new RuntimeException("Why would you instantiate this lol xD");
    }

    private static boolean[] initFile(int coordinate) {
        final boolean[] file = new boolean[NUM_TILES];

        do {
            file[coordinate] = true;
            coordinate += NUM_TILES_PER_ROW;
        } while (coordinate < NUM_TILES);

        return file;
    }

    private static boolean[] initRank(int coordinate) {
        final boolean[] rank = new boolean[NUM_TILES];
        int contador = 0;
        
        do {
            rank[coordinate] = true;
            coordinate++;
            contador++;
        } while (contador < 8);

        return rank;
    }

    public static boolean isValidTileCoordinate(final int coordinate) {
        return coordinate >= 0 && coordinate < NUM_TILES;
    }
}
