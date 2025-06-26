package com.example.zerolyth;

import com.example.zerolyth.puzzle.CipherDiscController;
import com.example.zerolyth.puzzle.SimonSaysController;
import com.example.zerolyth.puzzle.SlidingTilePuzzleController;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.example.zerolyth.puzzle.HanoiController;

public class LevelViewController {

    @FXML
    private GridPane grid;
    private ImageView playerView;
    private Image playerUp;
    private Image playerDown;
    private Image playerLeft;
    private Image playerRight;
    private GameSession gameSession;
    private int playerRow;
    private int playerCol;


    public void setGameSession(GameSession session) {
        this.gameSession = session;
    }




    private void loadPlayerImages() {
        String base;
        PlayerType type = gameSession.getPlayer().getType();
        if(type == PlayerType.PROTAGONIST) {
            base = "protagonist";
        } else {
            base = "antagonist";
        }
        playerUp = new Image(getClass().getResourceAsStream("assets/player/" + base + "_up.png"));
        playerDown = new Image(getClass().getResourceAsStream("assets/player/" + base + "_down.png"));
        playerLeft = new Image(getClass().getResourceAsStream("assets/player/" + base + "_left.png"));
        playerRight = new Image(getClass().getResourceAsStream("assets/player/" + base + "_right.png"));
    }
    private void animateDirectionChange(Image newImage) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(8), playerView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(8), playerView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        fadeOut.setOnFinished(e -> {
            playerView.setImage(newImage);
            fadeIn.play();
        });

        fadeOut.play();
    }
    public void initializeLevel() {
        loadPlayerImages();
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
            ImageView view = new ImageView(playerDown);
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
    private void freezeGame() {
        grid.setDisable(true);
    }

    private void unfreezeGame() {
        grid.setDisable(false);
    }


    private void handleKeyPress(KeyEvent event) {
        int newRow = playerRow;
        int newCol = playerCol;

        switch (event.getCode()) {
            case W -> {
                newRow--;
                animateDirectionChange(playerUp);
            }
            case S -> {
                newRow++;
                animateDirectionChange(playerDown);

            }
            case A -> {
                newCol--;
                animateDirectionChange(playerLeft);

            }
            case D -> {
                newCol++;
                animateDirectionChange(playerRight);

            }
            case E -> {
                TileType puzzleType = getAdjacentPuzzleTileType(newRow, newCol);
                if (puzzleType != null) {
                    freezeGame();
                    switch (puzzleType) {
                        case HANOI -> showHanoiPuzzle(this::unfreezeGame);
                        case SIMONSAYS -> showSimonSaysPuzzle(this::unfreezeGame);
                        case CIPHER -> showCipherPuzzle(this::unfreezeGame);
                        case SLIDING -> showSlidingPuzzle(this::unfreezeGame);
                        default -> unfreezeGame();
                    }
                } else {
                    System.out.println("No puzzle to interact with at (" + newRow + ", " + newCol + ")");
                }
                return;
            }
            default -> {
                return;
            }
        }

        if (isValidMove(newRow, newCol)) {
            updatePlayerPosition(newRow, newCol);
        }
    }
    private void showHanoiPuzzle(Runnable onComplete) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/zerolyth/puzzles/tower.fxml"));
            Parent hanoiRoot = loader.load();
            HanoiController hanoiController = loader.getController();
            hanoiController.setOnSolved(onComplete);

            Stage hanoiStage = new Stage();
            hanoiStage.setTitle("Hanoi Puzzle");
            hanoiStage.setScene(new javafx.scene.Scene(hanoiRoot));
            hanoiStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            hanoiStage.setOnHidden(e -> onComplete.run());
            hanoiStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            onComplete.run();
        }
    }
    private void showSlidingPuzzle(Runnable onComplete) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/zerolyth/puzzles/sliding_puzzle.fxml"));
            Parent slidingRoot = loader.load();
            SlidingTilePuzzleController slidingController = loader.getController();
            slidingController.setOnSolved(onComplete); // Set the callback for when the puzzle is solved


            Stage slidingStage = new Stage();
            slidingStage.setTitle("Sliding Tile Puzzle");
            slidingStage.setScene(new javafx.scene.Scene(slidingRoot));
            slidingStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            slidingStage.setOnHidden(e -> onComplete.run());
            slidingStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            onComplete.run();
        }
    }

    private void showCipherPuzzle(Runnable onComplete) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/zerolyth/puzzles/caesar_cipher.fxml"));
            Parent root = loader.load();

            // Optionally set a callback for when the puzzle is solved
            CipherDiscController controller = loader.getController();
            controller.setOnSolved(onComplete); // Uncomment if you add this to the controller

            Stage stage = new Stage();
            stage.setTitle("Cipher Disc Puzzle");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setOnHidden(e -> onComplete.run());
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            onComplete.run();
        }
    }

    public static void showSimonSaysPuzzle(Runnable onComplete) {
        try {
            FXMLLoader loader = new FXMLLoader(LevelLoader.class.getResource("/com/example/zerolyth/puzzles/simon_says.fxml"));
            Parent root = loader.load();
            SimonSaysController controller = loader.getController();
            controller.setOnSolved(onComplete); // Set the callback for when the puzzle is solved

            Stage stage = new Stage();
            stage.setTitle("Simon Says Puzzle");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOnHidden(e -> onComplete.run());
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            onComplete.run();
        }
    }

    private boolean isValidMove(int row, int col) {
        TileType[][] map = gameSession.getCurrentLevel().getMap();
        if (row < 0 || row >= map.length || col < 0 || col >= map[0].length) return false;
        TileType tile = map[row][col];
        return tile == TileType.PATH || tile == TileType.COLLECTIBLE || tile == TileType.EXIT;
    }
    // Add this method to LevelViewController
    private TileType getAdjacentPuzzleTileType(int playerRow, int playerCol) {
        TileType[][] map = gameSession.getCurrentLevel().getMap();
        int[][] directions = { {0, 1}, {1, 0}, {0, -1}, {-1, 0} }; // right, down, left, up
        for (int[] dir : directions) {
            int newRow = playerRow + dir[0];
            int newCol = playerCol + dir[1];
            if (newRow >= 0 && newRow < map.length && newCol >= 0 && newCol < map[0].length) {
                TileType tile = map[newRow][newCol];
                if (tile == TileType.HANOI || tile == TileType.SIMONSAYS ||
                        tile == TileType.CIPHER || tile == TileType.SLIDING) {
                    return tile;
                }
            }
        }
        return null;
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

