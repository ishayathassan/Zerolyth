package com.example.zerolyth;


import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SplashController {

    @FXML
    private ImageView gamelogo;

    @FXML
    private Label homeprompt;

    @FXML
    private AnchorPane rootPane;

    private boolean animationDone = false;

    @FXML
    public void initialize() {
        gamelogo.setOpacity(0);
        homeprompt.setOpacity(0);
        gamelogo.setVisible(true);
        homeprompt.setVisible(true);

        FadeTransition logoFade = new FadeTransition(Duration.seconds(2), gamelogo);
        logoFade.setFromValue(0);
        logoFade.setToValue(1);
        logoFade.setDelay(Duration.seconds(1));

        logoFade.setOnFinished(event -> {
            FadeTransition promptFade = new FadeTransition(Duration.seconds(1), homeprompt);
            promptFade.setFromValue(0);
            promptFade.setToValue(1);
            promptFade.play();
            animationDone = true;
        });

        logoFade.play();

        // ⚠ Instead of requestFocus() here, add a listener to scene property
        rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(this::handleKeyPress); // Attach key event to scene
                rootPane.requestFocus(); // Ensure initial focus if needed
            }
        });
    }


    private void handleKeyPress(KeyEvent event) {
        if (animationDone) {
            loadGameMenu();
        }
    }

    private void loadGameMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/zerolyth/gamemenu.fxml"));
            AnchorPane menuRoot = loader.load();

            GameMenuController controller = loader.getController();
            Stage stage = (Stage) rootPane.getScene().getWindow();
            controller.setPrimaryStage(stage);  // pass the stage

            Scene scene = new Scene(menuRoot);
            stage.setScene(scene);
            stage.setTitle("Game Menu");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

