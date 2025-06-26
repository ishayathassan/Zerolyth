package com.example.zerolyth.puzzle;


import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.util.Stack;

public class HanoiController {

    @FXML private ImageView block1, block2, block3; // frog, snake, eagle
    @FXML private Pane towerPane1, towerPane2, towerPane3;

    private double offsetX, offsetY;
    private Stack<ImageView> tower1 = new Stack<>();
    private Stack<ImageView> tower2 = new Stack<>();
    private Stack<ImageView> tower3 = new Stack<>();
    private final double baseHeight = 10.0;
    private boolean isDragging = false;
    private int moveCount = 0;
    private boolean gameWon = false;
    private Runnable onSolved;




    public void setOnSolved(Runnable onSolved) {
        this.onSolved = onSolved;
    }

    @FXML
    public void initialize() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Hanoi Puzzle");
        alert.setHeaderText("Tower of Hanoi");
        alert.setContentText("These ancient stones must be moved carefully.\n" +
                "Rebuild the stack on the final pillar to unlock your path.");
        alert.showAndWait();

        // Add blocks to tower1: bottom (eagle) to top (frog)
        tower1.push(block3); // eagle (largest)
        snapToTower(block3, towerPane1, 1);
        tower1.push(block2); // snake (middle)
        snapToTower(block2, towerPane1, 2);
        tower1.push(block1); // frog (smallest)
        snapToTower(block1, towerPane1, 3);

        makeDraggable(block1);
        makeDraggable(block2);
        makeDraggable(block3);
    }

    private void makeDraggable(ImageView block) {
        block.setOnMousePressed(e -> {
            if (gameWon) return;
            Stack<ImageView> currentTower = getCurrentTowerOfBlock(block);
            if (currentTower != null && !currentTower.isEmpty() && currentTower.peek() == block) {
                offsetX = e.getSceneX() - block.getLayoutX();
                offsetY = e.getSceneY() - block.getLayoutY();
                isDragging = true;
            } else {
                isDragging = false;
            }
        });

        block.setOnMouseDragged(e -> {
            if (isDragging && !gameWon) {
                block.setLayoutX(e.getSceneX() - offsetX);
                block.setLayoutY(e.getSceneY() - offsetY);
            }
        });

        block.setOnMouseReleased(e -> {
            if (isDragging && !gameWon) {
                handleDrop(e);
                isDragging = false;
            }
        });
    }

    private void handleDrop(MouseEvent e) {
        ImageView block = (ImageView) e.getSource();
        Pane targetTowerPane = getTargetTower(e.getSceneX());
        Stack<ImageView> fromTower = getCurrentTowerOfBlock(block);

        if (targetTowerPane == null) {
            snapToTower(block, getPaneByStack(fromTower), fromTower.size());
            return;
        }

        Stack<ImageView> toTower = getStackByPane(targetTowerPane);
        Pane fromTowerPane = getPaneByStack(fromTower);

        if (fromTowerPane == targetTowerPane) {
            snapToTower(block, fromTowerPane, fromTower.size());
            return;
        }

        if (!fromTower.isEmpty() && fromTower.peek() == block) {
            if (toTower.isEmpty() || block.getFitWidth() < toTower.peek().getFitWidth()) {
                fromTower.pop();
                toTower.push(block);
                snapToTower(block, targetTowerPane, toTower.size());
                moveCount++;
                System.out.println("Move #" + moveCount + ": Moved block to " + getTowerName(targetTowerPane));
                checkWinCondition();
            } else {
                snapToTower(block, fromTowerPane, fromTower.size());
                System.out.println("Invalid move! Cannot place larger block on smaller one.");
            }
        } else {
            snapToTower(block, fromTowerPane, fromTower.size());
        }
    }

    private void checkWinCondition() {
        if (tower3.size() == 3) {
            gameWon = true;
            System.out.println("\nCONGRATULATIONS! You won in " + moveCount + " moves!");
            System.out.println("Minimum possible moves: 7");
            block1.setDisable(true);
            block2.setDisable(true);
            block3.setDisable(true);

            // Show win alert with move count
            Alert winAlert = new Alert(AlertType.INFORMATION);
            winAlert.setTitle("Puzzle Solved!");
            winAlert.setHeaderText("Congratulations!");
            winAlert.setContentText("✨ The sacred stones rest in order. The ancient mechanism awakens…");
            winAlert.setContentText("You solved the puzzle in " + moveCount + " moves.\nMinimum possible moves: 7");
            winAlert.showAndWait();

            // Notify and close window
            if (onSolved != null) onSolved.run();
            ((Stage) block1.getScene().getWindow()).close();
        }
    }

    private String getTowerName(Pane towerPane) {
        if (towerPane == towerPane1) return "Tower 1";
        if (towerPane == towerPane2) return "Tower 2";
        return "Tower 3";
    }

    private Pane getTargetTower(double x) {
        if (x >= towerPane1.getLayoutX() && x <= towerPane1.getLayoutX() + towerPane1.getPrefWidth())
            return towerPane1;
        if (x >= towerPane2.getLayoutX() && x <= towerPane2.getLayoutX() + towerPane2.getPrefWidth())
            return towerPane2;
        if (x >= towerPane3.getLayoutX() && x <= towerPane3.getLayoutX() + towerPane3.getPrefWidth())
            return towerPane3;
        return null;
    }

    private void snapToTower(ImageView block, Pane towerPane, int positionFromBottom) {
        ImageView platform = (ImageView) towerPane.getChildren().get(0);
        double platformTopY = towerPane.getLayoutY() + platform.getLayoutY();
        double centerX = towerPane.getLayoutX() + (towerPane.getPrefWidth() - block.getFitWidth()) / 2;

        double blockY;
        if (positionFromBottom == 1) {
            // Bottom block: align with platform with custom overlap
            double platformOverlap = getPlatformOverlap(block);
            blockY = platformTopY - block.getFitHeight() + platformOverlap;
        } else {
            // Get the block below
            Stack<ImageView> tower = getStackByPane(towerPane);
            ImageView blockBelow = tower.get(positionFromBottom - 2);
            double blockBelowY = blockBelow.getLayoutY();

            // Calculate custom overlap based on block types
            double overlap = calculateOverlap(block, blockBelow);
            blockY = blockBelowY - block.getFitHeight() + overlap;
        }

        block.setLayoutX(centerX);
        block.setLayoutY(blockY);
        block.toFront();
    }

    private double getPlatformOverlap(ImageView block) {
        // Custom platform overlap for each block type
        if (block == block1) return 55.0;  // Frog
        if (block == block2) return 35.0;  // Snake
        if (block == block3) return 14.0;  // Eagle
        return 0.0;  // Default
    }

    private double calculateOverlap(ImageView blockAbove, ImageView blockBelow) {
        // Eagle (block3) with Snake (block2) on top
        if (blockBelow == block3 && blockAbove == block2) return 25.0;

        // Snake (block2) with Frog (block1) on top
        if (blockBelow == block2 && blockAbove == block1) return 44.0;

        // Eagle (block3) with Frog (block1) on top
        if (blockBelow == block3 && blockAbove == block1) return 44.0;

        return 30.0; // Default overlap
    }

    private Stack<ImageView> getCurrentTowerOfBlock(ImageView block) {
        if (tower1.contains(block)) return tower1;
        if (tower2.contains(block)) return tower2;
        if (tower3.contains(block)) return tower3;
        return null;
    }

    private Stack<ImageView> getStackByPane(Pane towerPane) {
        if (towerPane == towerPane1) return tower1;
        if (towerPane == towerPane2) return tower2;
        return tower3;
    }

    private Pane getPaneByStack(Stack<ImageView> tower) {
        if (tower == tower1) return towerPane1;
        if (tower == tower2) return towerPane2;
        return towerPane3;
    }
}