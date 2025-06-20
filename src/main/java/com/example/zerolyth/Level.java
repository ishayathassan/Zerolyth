package com.example.zerolyth;


public class Level {
    private TileType[][] map;
    private int startRow;
    private int startCol;

    public Level(TileType[][] map, int startRow, int startCol) {
        this.map = map;
        this.startRow = startRow;
        this.startCol = startCol;
    }

    public TileType[][] getMap() {
        return map;
    }

    public int getStartRow() {
        return startRow;
    }

    public int getStartCol() {
        return startCol;
    }
}

