package com.example.zerolyth;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class GameMenuController {

    @FXML
    private Button startGameButton;

    @FXML
    private CheckBox protagonistCheckBox;

    @FXML
    private CheckBox antagonistCheckBox;

    @FXML
    private Label statusLabel;

    @FXML
    private TextField nameField;

    private GameClient gameClient;
    private Stage primaryStage;
    private PlayerType selectedRole;

    private LevelViewController levelViewController; // Changed

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    public void initialize() {
        protagonistCheckBox.setOnAction(e -> {
            if (protagonistCheckBox.isSelected()) {
                antagonistCheckBox.setSelected(false);
            }
        });

        antagonistCheckBox.setOnAction(e -> {
            if (antagonistCheckBox.isSelected()) {
                protagonistCheckBox.setSelected(false);
            }
        });

        startGameButton.setOnAction(e -> onStartGameClicked());
    }

    @FXML
    private void onStartGameClicked() {
        if (!protagonistCheckBox.isSelected() && !antagonistCheckBox.isSelected()) {
            statusLabel.setText("Please select a role!");
            return;
        }

        // Merge Change
        String playerName = nameField.getText().trim();
        if (playerName.isEmpty()) {
            statusLabel.setText("Please enter a name!");
            return;
        }

        selectedRole = protagonistCheckBox.isSelected() ? PlayerType.PROTAGONIST : PlayerType.ANTAGONIST;
        statusLabel.setText("Connecting to server...");

        new Thread(() -> {
            try {
                gameClient = new GameClient("localhost", 55555, this);
                gameClient.connect();

                // Send role to server
                gameClient.sendRole(selectedRole.name());

                Platform.runLater(() -> statusLabel.setText("Role sent: " + selectedRole + ". Waiting for confirmation..."));

                gameClient.listenForMessages();

            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> statusLabel.setText("Connection failed: " + ex.getMessage()));
            }
        }).start();
    }

    // Called from GameClient when a message is received
    public void onServerMessage(String message) {
        System.out.println("Server message: " + message);

        if (message.startsWith("ROLE_ACCEPTED")) {
            Platform.runLater(() -> statusLabel.setText("Role accepted by server. Waiting for other player..."));
        }
        else if (message.startsWith("ROLE_DENIED")) {
//            Platform.runLater(() -> statusLabel.setText("Role denied by server: " + message.split(":", 2)[1]));
            // Merge Change
            String reason = message.split(":", 2).length > 1 ? message.split(":", 2)[1] : "unknown reason";
            Platform.runLater(() -> statusLabel.setText("Role denied by server: " + reason));
        }
        else if (message.equals("START_GAME")) {
//            Platform.runLater(() -> {
//                statusLabel.setText("Both players ready! Starting game...");
//                startGame();
//            });
            Platform.runLater(this::startGame); // Merge Change
        }
//        else if (message.startsWith("PROGRESS")) {
//            Platform.runLater(() -> {
//                System.out.println("Other player's progress: " + message.substring(9));
//            });
//        }
        // Changed
        else if (message.startsWith("PROGRESS")) {
//            String[] parts = message.split(":");
//            if (parts.length >= 3) {
//                String role = parts[1];
//                int progress = Integer.parseInt(parts[2]);
//                // Forward to LevelViewController
//                if (primaryStage.getScene().getRoot() instanceof Parent root) {
//                    Object controller = root.getUserData();
//                    if (controller instanceof LevelViewController levelVC) {
//                        levelVC.handleProgressUpdate(role, progress);
//                    }
//                }
//            }
            String[] parts = message.split(":");
            if (parts.length >= 3) {
                String role = parts[1];
                int progress = Integer.parseInt(parts[2]);

                // Forward to LevelViewController if available
                if (levelViewController != null) {
                    levelViewController.handleProgressUpdate(role, progress);
                }
            }
        }
        else if (message.equals("PROTAGONIST_DISCONNECTED") || message.equals("ANTAGONIST_DISCONNECTED")) {
            Platform.runLater(() -> statusLabel.setText("Other player disconnected. Waiting..."));
        }

    }

    private void startGame() {
        try {
            Player player = new Player(nameField.getText().trim(), selectedRole);
            Level level = LevelLoader.loadFromFile("levels/level1.txt");
            GameSession session = new GameSession(player, level, gameClient);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/zerolyth/level1_collectibles.fxml"));
            Parent root = loader.load();

            LevelViewController controller = loader.getController();
            controller.setGameSession(session);
            controller.initializeLevel();

            this.levelViewController = controller; // Changed

            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Zerolyth - Game Running");

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error starting game: " + e.getMessage());
        }
    }
}
