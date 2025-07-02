package com.example.zerolyth;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ScoreBoardController {
    @FXML private Label resultLabel;
    @FXML private Label collectiblesLabel;

    public void setPlayer(Player player) {
        collectiblesLabel.setText("Collectibles Collected: " + player.getCollectiblesCollected());
    }

    public void setResult(boolean won) {
        resultLabel.setText(won ? "🎉 YOU WON! 🎉" : "😞 YOU LOST 😞");
    }
}
