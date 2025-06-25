package com.example.zerolyth.puzzle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/zerolyth/puzzles/simon_says.fxml"));
        Scene scene = new Scene(loader.load());

        // Load CSS
//        scene.getStylesheets().add(getClass().getResource("/com/example/zerolyth/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Puzzle");
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
