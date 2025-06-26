package com.example.zerolyth.puzzle;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class SimonSaysController {

    @FXML private Button btn_top_left;
    @FXML private Button btn_top_right;
    @FXML private Button btn_bottom_left;
    @FXML private Button btn_bottom_right;

    @FXML private ImageView top_left;
    @FXML private ImageView top_right;
    @FXML private ImageView bottom_left;
    @FXML private ImageView bottom_right;

    @FXML private Label statusLabel;
    @FXML private Button startButton;
    private Runnable onSolved;


    private List<ImageView> tiles;
    private List<Integer> sequence = new ArrayList<>();
    private List<Integer> playerInput = new ArrayList<>();
    private Random random = new Random();
    private boolean playerTurn = false;
    private int currentRound = 1;
    private static final int MAX_ROUNDS = 3;

    @FXML
    public void initialize() {
        tiles = List.of(top_left, top_right, bottom_left, bottom_right);
        List<Button> inputButtons = List.of(btn_top_left, btn_top_right, btn_bottom_left, btn_bottom_right);

        for (int i = 0; i < inputButtons.size(); i++) {
            final int index = i;
            inputButtons.get(i).setOnAction(e -> handlePlayerInput(index));
        }

        for (ImageView tile : tiles) {
            tile.setVisible(false);
        }

        startButton.setOnAction(e -> startRound());
        startButton.setVisible(false);

        startNewGame();
    }
    public void setOnSolved(Runnable onSolved) {
        this.onSolved = onSolved;
    }
    private void startNewGame() {
        currentRound = 1;
        sequence.clear();
        playerInput.clear();
        statusLabel.setText("🌟 Welcome to Simon Says! Press Start to begin Round 1.");
        startButton.setText("Start");
        startButton.setVisible(true);

        Platform.runLater(() -> {
            Alert startAlert = new Alert(Alert.AlertType.INFORMATION);
            startAlert.setTitle("Simon Says Puzzle");
            startAlert.setHeaderText("Simon Says Challenge");
            startAlert.setContentText("Repeat the sequence for 3 rounds to win. If you fail, you'll start over!");
            startAlert.showAndWait();
        });
    }

    private void startRound() {
        statusLabel.setText("📜 Round " + currentRound + ": Watch closely...");
        startButton.setVisible(false);
        sequence.add(random.nextInt(4));
        playerInput.clear();
        playSequence();
    }

    private void playSequence() {
        Timeline timeline = new Timeline();

        for (int i = 0; i < sequence.size(); i++) {
            int index = sequence.get(i);
            ImageView tile = tiles.get(index);

            double delay = i * 1.0;
            KeyFrame on = new KeyFrame(Duration.seconds(delay), e -> highlightTile(tile));
            KeyFrame off = new KeyFrame(Duration.seconds(delay + 0.5), e -> unhighlightTile(tile));

            timeline.getKeyFrames().addAll(on, off);
        }

        timeline.setOnFinished(e -> {
            statusLabel.setText("👤 Your turn! Repeat the sequence.");
            playerInput.clear();
            playerTurn = true;
        });

        timeline.play();
    }

    private void handlePlayerInput(int index) {
        if (!playerTurn || playerInput.size() >= sequence.size()) return;

        ImageView tile = tiles.get(index);
        highlightTile(tile);

        PauseTransition pt = new PauseTransition(Duration.seconds(0.3));
        pt.setOnFinished(e -> unhighlightTile(tile));
        pt.play();

        playerInput.add(index);
        checkPlayerProgress();
    }

    private void checkPlayerProgress() {
        int current = playerInput.size() - 1;
        if (!playerInput.get(current).equals(sequence.get(current))) {
            statusLabel.setText("❌ Wrong! Starting over from Round 1.");
            startButton.setText("Restart");
            startButton.setVisible(true);
            playerTurn = false;
            Platform.runLater(() -> {
                Alert failAlert = new Alert(Alert.AlertType.WARNING);
                failAlert.setTitle("Simon Says Puzzle");
                failAlert.setHeaderText("Incorrect Sequence");
                failAlert.setContentText("You made a mistake! The game will restart from Round 1.");
                failAlert.showAndWait();
                startNewGame();
            });
            return;
        }

        if (playerInput.size() == sequence.size()) {
            playerTurn = false;
            if (currentRound == MAX_ROUNDS) {
                statusLabel.setText("🏆 You won! Congratulations!");
                Platform.runLater(() -> {
                    Alert winAlert = new Alert(Alert.AlertType.INFORMATION);
                    winAlert.setTitle("Simon Says Puzzle");
                    winAlert.setHeaderText("Congratulations!");
                    winAlert.setContentText("You have successfully completed all 3 rounds of Simon Says!");
                    winAlert.showAndWait();
                    // Close the window and notify parent
                    Node node = statusLabel.getScene().getRoot();
                    Stage stage = (Stage) node.getScene().getWindow();
                    stage.close();
                    if (onSolved != null) {
                        onSolved.run();
                    }
                });
            } else {
                currentRound++;
                statusLabel.setText("✅ Correct! Press Start for Round " + currentRound + ".");
                startButton.setText("Start Round " + currentRound);
                startButton.setVisible(true);
            }
        }
    }

    private void highlightTile(ImageView tile) {
        tile.setVisible(true);
        animateGlow(tile);
    }

    private void unhighlightTile(ImageView tile) {
        tile.setVisible(false);
    }

    private void animateGlow(Node node) {
        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.3), node);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.1);
        scale.setToY(1.1);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);

        FadeTransition fade = new FadeTransition(Duration.seconds(0.3), node);
        fade.setFromValue(0.5);
        fade.setToValue(1.0);
        fade.setAutoReverse(true);
        fade.setCycleCount(2);

        ParallelTransition animation = new ParallelTransition(scale, fade);
        animation.play();
    }
}