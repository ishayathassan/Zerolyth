package com.example.zerolyth.puzzle;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.util.*;

public class SimonSaysController {
    @FXML private Button redButton;
    @FXML private Button greenButton;
    @FXML private Button blueButton;
    @FXML private Button yellowButton;
    @FXML private Label statusLabel;

    private List<Button> buttons;
    private List<Integer> sequence = new ArrayList<>();
    private List<Integer> playerInput = new ArrayList<>();
    private Random random = new Random();

    @FXML
    public void initialize() {
        buttons = List.of(redButton, greenButton, blueButton, yellowButton);

        for (int i = 0; i < buttons.size(); i++) {
            final int index = i;
            buttons.get(i).setOnAction(e -> handlePlayerInput(index));
        }

        startNewGame();
    }

    private void startNewGame() {
        sequence.clear();
        playerInput.clear();
        statusLabel.setText("Watch the sequence");
        addNextInSequence();
    }

    private void addNextInSequence() {
        sequence.add(random.nextInt(4));
        playSequence();
    }

    private void playSequence() {
        Timeline timeline = new Timeline();

        for (int i = 0; i < sequence.size(); i++) {
            int index = sequence.get(i);
            Button btn = buttons.get(index);

            KeyFrame on = new KeyFrame(Duration.seconds(i * 1.0), e -> highlightButton(btn));
            KeyFrame off = new KeyFrame(Duration.seconds(i * 1.0 + 0.5), e -> unhighlightButton(btn));

            timeline.getKeyFrames().addAll(on, off);
        }

        timeline.setOnFinished(e -> {
            statusLabel.setText("Your turn");
            playerInput.clear();
        });

        timeline.play();
    }

    private void highlightButton(Button btn) {
        btn.setOpacity(0.5);
    }

    private void unhighlightButton(Button btn) {
        btn.setOpacity(1.0);
    }

    private void handlePlayerInput(int index) {
        if (playerInput.size() >= sequence.size()) return;

        Button btn = buttons.get(index);
        highlightButton(btn);
        PauseTransition pt = new PauseTransition(Duration.seconds(0.3));
        pt.setOnFinished(e -> unhighlightButton(btn));
        pt.play();

        playerInput.add(index);
        checkPlayerProgress();
    }

    private void checkPlayerProgress() {
        int current = playerInput.size() - 1;
        if (!playerInput.get(current).equals(sequence.get(current))) {
            statusLabel.setText("Wrong! Game Over.");
            return;
        }

        if (playerInput.size() == sequence.size()) {
            statusLabel.setText("Correct! Next round...");
            PauseTransition pt = new PauseTransition(Duration.seconds(1.5));
            pt.setOnFinished(e -> addNextInSequence());
            pt.play();
        }
    }
}
