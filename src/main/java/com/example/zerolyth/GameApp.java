package com.example.zerolyth;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/zerolyth/splash.fxml"));
        Parent root = loader.load();

        SplashController splashController = loader.getController();
        Scene splashScene = new Scene(root);
        primaryStage.setScene(splashScene);
        primaryStage.setTitle("Welcome to Zerolyth");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
