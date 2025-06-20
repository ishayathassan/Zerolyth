package com.example.zerolyth;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class LevelViewController {

    @FXML
    private GridPane grid;
    private ImageView playerView;

    private GameSession gameSession;
    private int playerRow;
    private int playerCol;

    public void setGameSession(GameSession session) {
        this.gameSession = session;
    }

    public void initializeLevel() {
        TileType[][] map = gameSession.getCurrentLevel().getMap();
        playerRow = gameSession.getCurrentLevel().getStartRow();
        playerCol = gameSession.getCurrentLevel().getStartCol();

        playerView = createPlayerView();

        StackPane startCell = getCell(playerRow, playerCol);
        if (startCell != null) {
            startCell.getChildren().add(playerView);
        }

        grid.setFocusTraversable(true);
        grid.requestFocus();
        grid.setOnKeyPressed(this::handleKeyPress);
    }

    private ImageView createPlayerView() {
        try {
            Image playerImage = new Image(getClass().getResourceAsStream("assets/knight.png"));
            ImageView view = new ImageView(playerImage);
            view.setFitHeight(48);
            view.setFitWidth(48);
            view.setPreserveRatio(true);
            view.setPickOnBounds(true);
            return view;
        } catch (Exception e) {
            System.err.println("Error loading player image: " + e.getMessage());
            return new ImageView();
        }
    }

    private void handleKeyPress(KeyEvent event) {
        int newRow = playerRow;
        int newCol = playerCol;

        switch (event.getCode()) {
            case W -> newRow--;
            case S -> newRow++;
            case A -> newCol--;
            case D -> newCol++;
            default -> {
                return;
            }
        }

        if (isValidMove(newRow, newCol)) {
            updatePlayerPosition(newRow, newCol);
        }
    }

    private boolean isValidMove(int row, int col) {
        TileType[][] map = gameSession.getCurrentLevel().getMap();
        if (row < 0 || row >= map.length || col < 0 || col >= map[0].length) return false;
        TileType tile = map[row][col];
        return tile == TileType.PATH || tile == TileType.COLLECTIBLE || tile == TileType.EXIT;
    }

    private void updatePlayerPosition(int newRow, int newCol) {
        TileType[][] map = gameSession.getCurrentLevel().getMap();

        if (map[newRow][newCol] == TileType.COLLECTIBLE) {
//            gameSession.getPlayer().addScore(10);
        }

        movePlayerView(newRow, newCol);
        playerRow = newRow;
        playerCol = newCol;
    }

    private void movePlayerView(int newRow, int newCol) {
        StackPane currentCell = getCell(playerRow, playerCol);
        if (currentCell != null) {
            currentCell.getChildren().remove(playerView);
        }

        StackPane newCell = getCell(newRow, newCol);
        if (newCell != null) {
            newCell.getChildren().add(playerView);
        }
    }

    private StackPane getCell(int row, int col) {
        for (Node node : grid.getChildren()) {
            Integer rowIndex = GridPane.getRowIndex(node);
            Integer colIndex = GridPane.getColumnIndex(node);
            if (rowIndex == null) rowIndex = 0;
            if (colIndex == null) colIndex = 0;
            if (rowIndex == row && colIndex == col) {
                return (StackPane) node;
            }
        }
        return null;
    }
}

