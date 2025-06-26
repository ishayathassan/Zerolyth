package com.example.zerolyth.puzzle;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
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


    private List<ImageView> tiles;
    private List<Integer> sequence = new ArrayList<>();
    private List<Integer> playerInput = new ArrayList<>();
    private Random random = new Random();
    private boolean playerTurn = false;

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


    private void startNewGame() {
        sequence.clear();
        playerInput.clear();
        statusLabel.setText("🌟 Welcome to the temple. Press Start Round.");
        startButton.setVisible(true);
    }
    private void startRound() {
        startButton.setVisible(false);
        statusLabel.setText("📜 Watch closely...");
        sequence.add(random.nextInt(4));
        playerInput.clear();
        playSequence();
    }


    private void addNextInSequence() {
        sequence.add(random.nextInt(4));
        playerInput.clear();
        System.out.println("📜 New Sequence Length: " + sequence.size());
        playSequence();
    }

    private void playSequence() {
        Timeline timeline = new Timeline();

        for (int i = 0; i < sequence.size(); i++) {
            int index = sequence.get(i);
            ImageView tile = tiles.get(index);

            double delay = i * 1.0;
            KeyFrame on = new KeyFrame(Duration.seconds(delay), e -> {
                System.out.println("🔆 Showing tile: " + index);
                highlightTile(tile);
            });
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

        System.out.println("✅ Clicked: " + index);
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
            statusLabel.setText("❌ Wrong! Game Over.");
            startButton.setText("Restart");
            startButton.setVisible(true);
            playerTurn = false;
            return;
        }

        if (playerInput.size() == sequence.size()) {
            statusLabel.setText("✅ Correct! Press Start for next round.");
            startButton.setText("Start Round");
            startButton.setVisible(true);
            playerTurn = false;
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
