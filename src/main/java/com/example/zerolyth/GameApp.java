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
        // Initialize player (Player 1 - Human)
        Player player = new Player("Player 1", PlayerType.HUMAN);

        // Load level from text file
        Level level = LevelLoader.loadFromFile("levels/level1.txt");
//        Level level = LevelLoader.loadFromFile("levels/test_level.txt");

        // Initialize game session
        gameSession = new GameSession(player, level);

        // Load FXML and controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/zerolyth/level1.fxml"));
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/zerolyth/test_level_48x48.fxml"));

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