package com.example.zerolyth.puzzle;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.Random;

public class CipherDiscController {

    @FXML private Pane outerLetters;
    @FXML private Group innerLetters;
    @FXML private Label hintPlaintextLabel;
    @FXML private Label hintCiphertextLabel;
    @FXML private Label puzzleCiphertextLabel;
    @FXML private TextField puzzlePlaintextInput;


    private int currentShift = 0; // Current disc shift
    private int encryptionShift;  // Random shift used for encryption
    private final double centerX = 300;
    private final double centerY = 300;
    private final double outerRadius = 200;
    private final double innerRadius = 160;
    private final Font boldFont = Font.font("System", FontWeight.BOLD, 14);
    private Text[] innerTexts = new Text[26];
    private Text[] outerTexts = new Text[26];


    // Example messages (can be changed)
    private final String hintPlaintext = "HELLO";
    private String hintCiphertext;
    private String puzzlePlaintext = "WELCOME HOME";
    private String puzzleCiphertext;


    @FXML
    public void initialize() {
        // Generate random shift between 1-25
        Random random = new Random();
        encryptionShift = random.nextInt(25) + 1;

        // Encrypt messages using the random shift
        hintCiphertext = encrypt(hintPlaintext, encryptionShift);
        puzzleCiphertext = encrypt(puzzlePlaintext, encryptionShift);

        // Set labels
        hintPlaintextLabel.setText("Plaintext: " + hintPlaintext);
        hintCiphertextLabel.setText("Ciphertext: " + hintCiphertext);
        puzzleCiphertextLabel.setText("\"" + puzzleCiphertext + "\"");

        // Draw cipher disc
        drawLetters();
        highlightLetters();
    }

    // Caesar cipher encryption method
    private String encrypt(String plaintext, int shift) {
        StringBuilder ciphertext = new StringBuilder();
        for (char c : plaintext.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                char shifted = (char) ((c - base + shift) % 26 + base);
                ciphertext.append(shifted);
            } else {
                ciphertext.append(c); // Keep non-letter characters
            }
        }
        return ciphertext.toString();
    }

    // Caesar cipher decryption method
    private String decrypt(String ciphertext, int shift) {
        StringBuilder plaintext = new StringBuilder();
        for (char c : ciphertext.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                char shifted = (char) ((c - base - shift + 26) % 26 + base);
                plaintext.append(shifted);
            } else {
                plaintext.append(c); // Keep non-letter characters
            }
        }
        return plaintext.toString();
    }

    private void drawLetters() {
        outerLetters.getChildren().clear();
        innerLetters.getChildren().clear();

        for (int i = 0; i < 26; i++) {
            double angle = Math.toRadians(i * (360.0 / 26));
            String letter = String.valueOf((char) ('A' + i));

            // Outer Letter (fixed positions)
            double xOuter = centerX + outerRadius * Math.cos(angle);
            double yOuter = centerY + outerRadius * Math.sin(angle);
            Text textOuter = new Text(xOuter, yOuter, letter);
            textOuter.setFont(boldFont);
            textOuter.setX(xOuter - textOuter.getLayoutBounds().getWidth() / 2);
            textOuter.setY(yOuter + textOuter.getLayoutBounds().getHeight() / 4);
            outerLetters.getChildren().add(textOuter);
            outerTexts[i] = textOuter;

            // Inner Letter (positions will be updated on shift)
            double xInner = centerX + innerRadius * Math.cos(angle);
            double yInner = centerY + innerRadius * Math.sin(angle);
            Text textInner = new Text(xInner, yInner, letter);
            textInner.setFont(boldFont);
            innerTexts[i] = textInner;
            innerLetters.getChildren().add(textInner);
        }
        updateInnerPositions();
    }

    private void updateInnerPositions() {
        for (int i = 0; i < 26; i++) {
            int shiftedIndex = (i + currentShift) % 26;
            double angle = Math.toRadians(shiftedIndex * (360.0 / 26));

            double xInner = centerX + innerRadius * Math.cos(angle);
            double yInner = centerY + innerRadius * Math.sin(angle);

            innerTexts[i].setX(xInner - innerTexts[i].getLayoutBounds().getWidth() / 2);
            innerTexts[i].setY(yInner + innerTexts[i].getLayoutBounds().getHeight() / 4);
        }
    }

    private void highlightLetters() {
        // Reset all to black first
        for (Text text : outerTexts) {
            text.setFill(Color.BLACK);
        }
        for (Text text : innerTexts) {
            text.setFill(Color.BLACK);
        }

        // Highlight hint plaintext letters in outer circle (RED)
        for (char c : hintPlaintext.toCharArray()) {
            if (Character.isLetter(c)) {
                char upper = Character.toUpperCase(c);
                int index = upper - 'A';
                if (index >= 0 && index < 26) {
                    outerTexts[index].setFill(Color.RED);
                }
            }
        }

        // Highlight hint ciphertext letters in inner circle (RED)
        for (char c : hintCiphertext.toCharArray()) {
            if (Character.isLetter(c)) {
                char upper = Character.toUpperCase(c);
                int index = upper - 'A';
                if (index >= 0 && index < 26) {
                    innerTexts[index].setFill(Color.RED);
                }
            }
        }
    }

    @FXML
    public void rotateLeft() {
        currentShift = (currentShift - 1 + 26) % 26;
        updateInnerPositions();
    }

    @FXML
    public void rotateRight() {
        currentShift = (currentShift + 1) % 26;
        updateInnerPositions();
    }

    @FXML
    private void checkAnswer() {
        String userInput = puzzlePlaintextInput.getText().trim();
        String expectedPlaintext = decrypt(puzzleCiphertext, encryptionShift);

        if (userInput.equalsIgnoreCase(expectedPlaintext)) {
            showAlert("Correct!", "Your decryption is correct!", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Incorrect", "Try again! The correct answer is: " + expectedPlaintext,
                    Alert.AlertType.WARNING);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}