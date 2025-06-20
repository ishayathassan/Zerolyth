package com.example.zerolyth;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

class LevelLoader {
    public static Level loadFromFile(String filePath) throws Exception {
        InputStream inputStream = LevelLoader.class.getResourceAsStream("/" + filePath);
        if (inputStream == null) {
            throw new FileNotFoundException("Level file not found in resources: " + filePath);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        List<String> lines = reader.lines().toList();

        int rows = lines.size();
        int cols = lines.get(0).split(" ").length;
        TileType[][] map = new TileType[rows][cols];

        int startRow = -1;
        int startCol = -1;

        for (int row = 0; row < rows; row++) {
            String[] tokens = lines.get(row).split(" ");
            for (int col = 0; col < cols; col++) {
                int code = Integer.parseInt(tokens[col]);

                if (code == -1) {
                    startRow = row;
                    startCol = col;
                    map[row][col] = TileType.PATH; // assume -1 is on a walkable tile
                } else {
                    map[row][col] = switch (code) {
                        case 0 -> TileType.PATH;
                        case 1 -> TileType.WALL;
                        case 2 -> TileType.COLLECTIBLE;
                        case 3 -> TileType.DOOR;
                        case 4 -> TileType.EXIT;
                        default -> TileType.ENVIRONMENT;
                    };
                }
            }
        }

        if (startRow == -1 || startCol == -1) {
            throw new IllegalArgumentException("Start position (-1) not found in the level file.");
        }

        return new Level(map, startRow, startCol);
    }
}
