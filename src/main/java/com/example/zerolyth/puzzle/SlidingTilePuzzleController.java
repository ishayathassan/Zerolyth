package com.example.zerolyth.puzzle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SlidingTilePuzzleController {

    @FXML private Button btn00, btn01, btn02;
    @FXML private Button btn10, btn11, btn12;
    @FXML private Button btn20, btn21, btn22;

    private Button[][] buttons;
    private int[][] tiles; // 0 = empty, 1-8 = tile numbers
    private String[][] imagePaths; // image path for each tile
    private String[] solvedImagePaths; // correct order
    private int emptyRow;
    private int emptyCol;

    @FXML
    public void initialize() {
        buttons = new Button[][]{
                {btn00, btn01, btn02},
                {btn10, btn11, btn12},
                {btn20, btn21, btn22}
        };
        tiles = new int[3][3];
        imagePaths = new String[3][3];

        // Initialize image paths (update as needed)
        String[] paths = {
                "/com/example/zerolyth/assets/knight/1.jpg",
                "/com/example/zerolyth/assets/knight/2.jpg",
                "/com/example/zerolyth/assets/knight/3.jpg",
                "/com/example/zerolyth/assets/knight/4.jpg",
                "/com/example/zerolyth/assets/knight/5.jpg",
                "/com/example/zerolyth/assets/knight/6.jpg",
                "/com/example/zerolyth/assets/knight/7.jpg",
                "/com/example/zerolyth/assets/knight/8.jpg",
                "" // empty
        };
        solvedImagePaths = paths.clone();
        scrambleBoard();
    }

    private void scrambleBoard() {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < 9; i++) indices.add(i); // 0-8
        Collections.shuffle(indices);

        int idx = 0;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                int shuffledIdx = indices.get(idx++);
                if (shuffledIdx == 8) { // empty tile
                    tiles[r][c] = 0;
                    imagePaths[r][c] = "";
                    emptyRow = r;
                    emptyCol = c;
                } else {
                    tiles[r][c] = shuffledIdx + 1;
                    imagePaths[r][c] = solvedImagePaths[shuffledIdx];
                }
                setButtonImage(buttons[r][c], imagePaths[r][c]);
            }
        }
        rebindActions();
    }

    private void setButtonImage(Button btn, String imgPath) {
        if (imgPath == null || imgPath.isEmpty()) {
            btn.setGraphic(null);
        } else {
            ImageView iv = new ImageView(new Image(getClass().getResourceAsStream(imgPath)));
            iv.setFitWidth(128);
            iv.setFitHeight(128);
            iv.setPreserveRatio(false); // Fill the button completely
            btn.setGraphic(iv);
        }
        btn.setText(""); // Ensure no text is shown
    }

    private void handleMove(int row, int col) {
        if ((Math.abs(row - emptyRow) == 1 && col == emptyCol) ||
                (Math.abs(col - emptyCol) == 1 && row == emptyRow)) {

            // Swap in tiles and imagePaths arrays
            int tempTile = tiles[row][col];
            tiles[row][col] = tiles[emptyRow][emptyCol];
            tiles[emptyRow][emptyCol] = tempTile;

            String tempPath = imagePaths[row][col];
            imagePaths[row][col] = imagePaths[emptyRow][emptyCol];
            imagePaths[emptyRow][emptyCol] = tempPath;

            // Update button graphics
            setButtonImage(buttons[row][col], imagePaths[row][col]);
            setButtonImage(buttons[emptyRow][emptyCol], imagePaths[emptyRow][emptyCol]);

            emptyRow = row;
            emptyCol = col;

            rebindActions();
            checkWin();
        }
    }

    private void rebindActions() {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                Button btn = buttons[r][c];
                if (tiles[r][c] == 0) {
                    btn.setOnAction(null);
                } else if ((Math.abs(r - emptyRow) == 1 && c == emptyCol) ||
                        (Math.abs(c - emptyCol) == 1 && r == emptyRow)) {
                    int finalR = r, finalC = c;
                    btn.setOnAction(e -> handleMove(finalR, finalC));
                } else {
                    btn.setOnAction(null);
                }
            }
        }
    }

    private void checkWin() {
        int idx = 0;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                String expected = solvedImagePaths[idx++];
                if (!expected.equals(imagePaths[r][c])) return;
            }
        }
        System.out.println("🎉 You won!");
    }
}