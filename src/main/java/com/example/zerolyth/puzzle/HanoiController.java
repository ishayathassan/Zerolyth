package com.example.zerolyth.puzzle;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.Stack;

public class HanoiController {

    @FXML private Rectangle disk1, disk2, disk3;
    @FXML private Pane towerPane1, towerPane2, towerPane3;

    private double offsetX, offsetY;
    private Stack<Rectangle> tower1 = new Stack<>();
    private Stack<Rectangle> tower2 = new Stack<>();
    private Stack<Rectangle> tower3 = new Stack<>();
    private final double baseHeight = 10.0; // Height of the base
    private boolean isDragging = false; // Flag to track if the disk is eligible for dragging
    private int moveCount = 0;
    private boolean gameWon = false; // Track game state

    @FXML
    public void initialize() {
        // Add disks to tower1 initially, bottom to top
        tower1.push(disk3);
        snapToTower(disk3, towerPane1, 1);
        tower1.push(disk2);
        snapToTower(disk2, towerPane1, 2);
        tower1.push(disk1);
        snapToTower(disk1, towerPane1, 3);

        makeDraggable(disk1);
        makeDraggable(disk2);
        makeDraggable(disk3);
    }

    private void makeDraggable(Rectangle disk) {
        disk.setOnMousePressed(e -> {
            if (gameWon) return; // Disable moves after winning

            Stack<Rectangle> currentTower = getCurrentTowerOfDisk(disk);
            if (currentTower != null && !currentTower.isEmpty() && currentTower.peek() == disk) {
                offsetX = e.getSceneX() - disk.getLayoutX();
                offsetY = e.getSceneY() - disk.getLayoutY();
                isDragging = true;
            } else {
                isDragging = false;
            }
        });

        disk.setOnMouseDragged(e -> {
            if (isDragging && !gameWon) {
                disk.setLayoutX(e.getSceneX() - offsetX);
                disk.setLayoutY(e.getSceneY() - offsetY);
            }
        });

        disk.setOnMouseReleased(e -> {
            if (isDragging && !gameWon) {
                handleDrop(e);
                isDragging = false;
            }
        });
    }

    private void handleDrop(MouseEvent e) {
        Rectangle disk = (Rectangle) e.getSource();
        Pane targetTowerPane = getTargetTower(e.getSceneX());
        Stack<Rectangle> fromTower = getCurrentTowerOfDisk(disk);

        // If dropped outside valid area
        if (targetTowerPane == null) {
            snapToTower(disk, getPaneByStack(fromTower), fromTower.size());
            return;
        }

        Stack<Rectangle> toTower = getStackByPane(targetTowerPane);
        Pane fromTowerPane = getPaneByStack(fromTower);

        // Prevent moving to the same tower
        if (fromTowerPane == targetTowerPane) {
            snapToTower(disk, fromTowerPane, fromTower.size());
            return;
        }

        // Validate move
        if (!fromTower.isEmpty() && fromTower.peek() == disk) {
            if (toTower.isEmpty() || disk.getWidth() < toTower.peek().getWidth()) {
                // Valid move - update towers
                fromTower.pop();
                toTower.push(disk);
                snapToTower(disk, targetTowerPane, toTower.size());

                // Count and display moves
                moveCount++;
                System.out.println("Move #" + moveCount + ": Moved disk to " +
                        getTowerName(targetTowerPane));

                // Check win condition
                checkWinCondition();
            } else {
                // Invalid move (disk too large)
                snapToTower(disk, fromTowerPane, fromTower.size());
                System.out.println("Invalid move! Cannot place larger disk on smaller one.");
            }
        } else {
            // Shouldn't happen due to drag check, but handle anyway
            snapToTower(disk, fromTowerPane, fromTower.size());
        }
    }

    private void checkWinCondition() {
        // Win when all 3 disks are on tower3
        if (tower3.size() == 3) {
            gameWon = true;
            System.out.println("\nCONGRATULATIONS! You won in " + moveCount + " moves!");
            System.out.println("Minimum possible moves: 7");

            // Optional: Disable further moves
            disk1.setDisable(true);
            disk2.setDisable(true);
            disk3.setDisable(true);
        }
    }

    // Helper to get tower name for messages
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

    private void snapToTower(Rectangle disk, Pane towerPane, int positionFromBottom) {
        double centerX = towerPane.getLayoutX() + (towerPane.getPrefWidth() - disk.getWidth()) / 2;
        double bottomY = towerPane.getLayoutY() + towerPane.getPrefHeight() - baseHeight;
        double diskHeight = disk.getHeight();
        disk.setLayoutX(centerX);
        disk.setLayoutY(bottomY - diskHeight * positionFromBottom);
    }

    private Stack<Rectangle> getCurrentTowerOfDisk(Rectangle disk) {
        if (tower1.contains(disk)) return tower1;
        if (tower2.contains(disk)) return tower2;
        if (tower3.contains(disk)) return tower3;
        return null;
    }

    private Stack<Rectangle> getStackByPane(Pane towerPane) {
        if (towerPane == towerPane1) return tower1;
        if (towerPane == towerPane2) return tower2;
        return tower3;
    }

    private Pane getPaneByStack(Stack<Rectangle> tower) {
        if (tower == tower1) return towerPane1;
        if (tower == tower2) return towerPane2;
        return towerPane3;
    }
}