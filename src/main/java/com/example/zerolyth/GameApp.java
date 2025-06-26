package com.example.zerolyth;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameApp extends Application {

    private GameSession gameSession;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Player player = new Player("Player 1", PlayerType.ANTAGONIST);

        // Load level from text file
        Level level = LevelLoader.loadFromFile("levels/level_test.txt");

        // Initialize game session
        gameSession = new GameSession(player, level);

        // Load FXML and controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/zerolyth/level1.fxml"));

        Parent root = loader.load();

        LevelViewController controller = loader.getController();
        controller.setGameSession(gameSession);
        controller.initializeLevel();

        primaryStage.setTitle("Zerolyth");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}