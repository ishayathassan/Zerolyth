package com.example.zerolyth.puzzle;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class ShiftCipherController {
    @FXML private Label encryptedLabel;
    @FXML private Label decodedPreview;
    @FXML private Label shiftValueLabel;
    @FXML private TextField answerField;
    @FXML private Label feedbackLabel;
    @FXML private Slider shiftSlider;


    private String encryptedText = "FDVWOH RI JROG"; // You can randomize this
    private String correctAnswer = "CASTLE OF GOLD"; // Uppercase, no punctuation
    private int shift = 0;

    @FXML
    public void initialize() {
        encryptedLabel.setText(encryptedText);
        shiftSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            shift = newVal.intValue();
            updateDecodedPreview();
        });
        updateDecodedPreview();
    }


    private void updateDecodedPreview() {
        String decoded = decode(encryptedText, shift);
        decodedPreview.setText(decoded);
        shiftValueLabel.setText("Shift: " + shift);
    }



    private String decode(String text, int shift) {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                int offset = (c - base - shift + 26) % 26;
                result.append((char) (base + offset));
            } else {
                result.append(c); // spaces, punctuation, etc.
            }
        }
        return result.toString();
    }

    public void handleSubmit() {
        String userAnswer = answerField.getText().toUpperCase().trim();
        if (userAnswer.equals(correctAnswer)) {
            feedbackLabel.setText("✅ Correct! Puzzle Solved.");
            feedbackLabel.setStyle("-fx-text-fill: green;");
            // Trigger success event (e.g., close puzzle, unlock door)
        } else {
            feedbackLabel.setText("❌ Incorrect. Try again.");
            feedbackLabel.setStyle("-fx-text-fill: red;");
        }
    }

    public void showHint() {
        feedbackLabel.setText("🔍 Hint: The shift is between -5 and 0.");
        feedbackLabel.setStyle("-fx-text-fill: blue;");
    }
}

