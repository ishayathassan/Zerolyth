package com.example.zerolyth;

import com.example.zerolyth.puzzle.CipherDiscController;
import com.example.zerolyth.puzzle.SimonSaysController;
import com.example.zerolyth.puzzle.SlidingTilePuzzleController;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.example.zerolyth.puzzle.HanoiController;

import javafx.scene.media.AudioClip;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

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

    // Merge Change
    @FXML private Label hanoiLabel;
    @FXML private Label simonLabel;
    @FXML private Label cipherLabel;
    @FXML private Label slidingLabel;
    private AudioClip moveSound;
    private AudioClip puzzleSound;

    private Map<Point, ImageView> collectibleViews = new HashMap<>();

    @FXML private ImageView blueCollectible1;
    @FXML private ImageView blueCollectible2;
    @FXML private ImageView blueCollectible3;
    @FXML private ImageView blueCollectible4;
    @FXML private ImageView blueCollectible5;
    @FXML private ImageView goldCollectible1;
    @FXML private ImageView goldCollectible2;
    @FXML private ImageView goldCollectible3;

    private final String[] blueImages = {
            "/collectibles/blue1.png",
            "/collectibles/blue2.png",
            "/collectibles/blue3.png",
            "/collectibles/blue4.png",
            "/collectibles/blue5.png"
    };

    private final String[] goldImages = {
            "/collectibles/gold1.png",
            "/collectibles/gold2.png",
            "/collectibles/gold3.png",
            "/collectibles/gold4.png",
            "/collectibles/gold5.png",
            "/collectibles/gold6.png",
            "/collectibles/gold7.png",
            "/collectibles/gold8.png"
    };

    private int blueIndex = 0;
    private int goldIndex = 0;

    // Changed
    @FXML private ProgressBar protagonistProgressBar;
    @FXML private ProgressBar antagonistProgressBar;
    private void initializeProgressBars() {
        protagonistProgressBar.setProgress(0);
        antagonistProgressBar.setProgress(0);
    }
    private void sendPuzzleProgressUpdate() {
        int progress = gameSession.getPlayer().getProgressPercentage();
        String role = gameSession.getPlayer().getType().name();
        gameSession.sendProgressUpdate("PROGRESS:" + role + ":" + progress);
    }
//    public void handleProgressUpdate(String role, int progress) {
//        System.out.println("Received progress update - Role: " + role + ", Progress: " + progress + "%");
//
//        Platform.runLater(() -> {
//            if ("PROTAGONIST".equals(role)) {
//                System.out.println("Updating protagonist progress bar to: " + progress + "%");
//                protagonistProgressBar.setProgress(progress / 100.0);
//            } else if ("ANTAGONIST".equals(role)) {
//                System.out.println("Updating antagonist progress bar to: " + progress + "%");
//                antagonistProgressBar.setProgress(progress / 100.0);
//            }
//        });
//    }
    public void handleProgressUpdate(String role, int progress) {
        System.out.println("Received REMOTE progress update - Role: " + role + ", Progress: " + progress + "%");

        Platform.runLater(() -> {
            // Only update the OTHER player's progress bar
            if ("PROTAGONIST".equals(role) && gameSession.getPlayer().getType() == PlayerType.ANTAGONIST) {
                System.out.println("Updating PROTAGONIST progress bar to: " + progress + "%");
                protagonistProgressBar.setProgress(progress / 100.0);
            } else if ("ANTAGONIST".equals(role) && gameSession.getPlayer().getType() == PlayerType.PROTAGONIST) {
                System.out.println("Updating ANTAGONIST progress bar to: " + progress + "%");
                antagonistProgressBar.setProgress(progress / 100.0);
            }
        });
    }
    private void updateLocalProgressBar() {
        int progress = gameSession.getPlayer().getProgressPercentage();
        PlayerType type = gameSession.getPlayer().getType();

        if (type == PlayerType.PROTAGONIST) {
            protagonistProgressBar.setProgress(progress / 100.0);
        } else {
            antagonistProgressBar.setProgress(progress / 100.0);
        }

        System.out.println("Updated LOCAL progress: " + type + " = " + progress + "%");
    }


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
        // Changed
        System.out.println("Initializing level view");
        System.out.println("Protagonist progress bar: " + protagonistProgressBar.getProgress());
        System.out.println("Antagonist progress bar: " + antagonistProgressBar.getProgress());
        initializeProgressBars();

//        loadPlayerImages();
//        TileType[][] map = gameSession.getCurrentLevel().getMap();
//        playerRow = gameSession.getCurrentLevel().getStartRow();
//        playerCol = gameSession.getCurrentLevel().getStartCol();
//
//        playerView = createPlayerView();
//
//        StackPane startCell = getCell(playerRow, playerCol);
//        if (startCell != null) {
//            startCell.getChildren().add(playerView);
//        }
//
//        grid.setFocusTraversable(true);
//        grid.requestFocus();
//        grid.setOnKeyPressed(this::handleKeyPress);
        // Merge Change
        loadPlayerImages();
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(150), e -> animateCollectibles()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        try {
            moveSound = new AudioClip(getClass().getResource("/sounds/step_rock.wav").toExternalForm());
            puzzleSound = new AudioClip(getClass().getResource("/sounds/puzzle.wav").toExternalForm());
        } catch (Exception e) {
            System.err.println("Could not load move sound: " + e.getMessage());
        }
        TileType[][] map = gameSession.getCurrentLevel().getMap();
        playerRow = gameSession.getCurrentLevel().getStartRow();
        playerCol = gameSession.getCurrentLevel().getStartCol();

        playerView = createPlayerView();

        StackPane startCell = getCell(playerRow, playerCol);
        if (startCell != null) {
            startCell.getChildren().add(playerView);
        }
        collectibleViews.put(new Point(2, 1), blueCollectible5);
        collectibleViews.put(new Point(2, 10), goldCollectible3);
        collectibleViews.put(new Point(6, 8), blueCollectible4);
        collectibleViews.put(new Point(7, 11), blueCollectible3);
        collectibleViews.put(new Point(13, 1), goldCollectible2);

        collectibleViews.put(new Point(13, 14), blueCollectible1);
        collectibleViews.put(new Point(14, 12), blueCollectible2);
        collectibleViews.put(new Point(17, 1), goldCollectible1);

        grid.setFocusTraversable(true);
        grid.requestFocus();
        grid.setOnKeyPressed(this::handleKeyPress);
    }

    private void removeCollectibleFromMap(int row, int col) {
        gameSession.getCurrentLevel().getMap()[row][col] = TileType.PATH;
    }

    private void animateCollectibles() {
        blueIndex = (blueIndex + 1) % blueImages.length;
        goldIndex = (goldIndex + 1) % goldImages.length;

        // Animate blue collectibles
        blueCollectible1.setImage(new Image(getClass().getResourceAsStream(blueImages[blueIndex])));
        blueCollectible2.setImage(new Image(getClass().getResourceAsStream(blueImages[blueIndex])));
        blueCollectible3.setImage(new Image(getClass().getResourceAsStream(blueImages[blueIndex])));
        blueCollectible4.setImage(new Image(getClass().getResourceAsStream(blueImages[blueIndex])));
        blueCollectible5.setImage(new Image(getClass().getResourceAsStream(blueImages[blueIndex])));

        // Repeat for all blue collectibles

        // Animate gold collectibles
        goldCollectible1.setImage(new Image(getClass().getResourceAsStream(goldImages[goldIndex])));
        goldCollectible2.setImage(new Image(getClass().getResourceAsStream(goldImages[goldIndex])));
        goldCollectible3.setImage(new Image(getClass().getResourceAsStream(goldImages[goldIndex])));

        // Repeat for all gold collectibles
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
                    if (puzzleSound != null) {
                        puzzleSound.play();
                    }
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
        if (gameSession.getPlayer().isCompletedHanoiPuzzle()) {
            onComplete.run();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/zerolyth/puzzles/tower.fxml"));
            Parent hanoiRoot = loader.load();
            HanoiController hanoiController = loader.getController();
            hanoiController.setOnSolved(() -> {
                gameSession.getPlayer().setCompletedHanoiPuzzle(true);
                updateLocalProgressBar(); // Changed
                sendPuzzleProgressUpdate(); // Changed
                onComplete.run();
            });

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
        if (gameSession.getPlayer().isCompletedSlidingTilePuzzle()) {
            onComplete.run();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/zerolyth/puzzles/sliding_puzzle.fxml"));
            Parent slidingRoot = loader.load();
            SlidingTilePuzzleController slidingController = loader.getController();
            slidingController.setOnSolved(() ->{
                gameSession.getPlayer().setCompletedSlidingTilePuzzle(true);
                updateLocalProgressBar(); // Changed
                sendPuzzleProgressUpdate(); // Changed
                onComplete.run();
            });
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
        if(gameSession.getPlayer().isCompletedCipherPuzzle()){
            onComplete.run();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/zerolyth/puzzles/caesar_cipher.fxml"));
            Parent root = loader.load();
            CipherDiscController controller = loader.getController();
            controller.setOnSolved(() -> {
                gameSession.getPlayer().setCompletedCipherPuzzle(true);
                updateLocalProgressBar(); // Changed
                sendPuzzleProgressUpdate(); // Changed
                onComplete.run();
            });

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

    private void showSimonSaysPuzzle(Runnable onComplete) {
        if (gameSession.getPlayer().isCompletedSimonSaysPuzzle()) {
            onComplete.run();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(LevelLoader.class.getResource("/com/example/zerolyth/puzzles/simon_says.fxml"));
            Parent root = loader.load();
            SimonSaysController controller = loader.getController();
            controller.setOnSolved(() -> {
                gameSession.getPlayer().setCompletedSimonSaysPuzzle(true);
                updateLocalProgressBar(); // Changed
                sendPuzzleProgressUpdate(); // Changed
                onComplete.run();
            });

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
//        TileType[][] map = gameSession.getCurrentLevel().getMap();
//
//        if (map[newRow][newCol] == TileType.COLLECTIBLE) {
////            gameSession.getPlayer().addScore(10);
//        }
//
//        movePlayerView(newRow, newCol);
//        playerRow = newRow;
//        playerCol = newCol;
        // Merge Change
        TileType[][] map = gameSession.getCurrentLevel().getMap();
        TileType puzzleType = getAdjacentPuzzleTileType(newRow, newCol);
        if (map[newRow][newCol] == TileType.COLLECTIBLE) {
            System.out.println("Player at: " + newRow + ", " + newCol);
            Point pos = new Point(newRow, newCol);
            ImageView collectedView = collectibleViews.get(pos);
            if (collectedView != null) {
                System.out.println("Found collectible at: " + pos);
                collectedView.setVisible(false);
                collectibleViews.remove(pos);
            } else {
                System.out.println("No collectible ImageView found at: " + pos);
            }

            gameSession.getPlayer().addCollectible();
            map[newRow][newCol] = TileType.PATH;
        }

        if (puzzleType == TileType.HANOI) {
            hanoiLabel.setVisible(true);
        } else  if (puzzleType == TileType.SIMONSAYS) {
            simonLabel.setVisible(true);
        } else if (puzzleType == TileType.CIPHER) {
            cipherLabel.setVisible(true);
        } else if (puzzleType == TileType.SLIDING) {
            slidingLabel.setVisible(true);
        } else {
            hanoiLabel.setVisible(false);
            simonLabel.setVisible(false);
            cipherLabel.setVisible(false);
            slidingLabel.setVisible(false);
        }

        movePlayerView(newRow, newCol);
        playerRow = newRow;
        playerCol = newCol;

        if (moveSound != null) {
            moveSound.play();
        }
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

