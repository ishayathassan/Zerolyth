package com.example.zerolyth.puzzle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SlidingTilePuzzleController {

    @FXML private Button btn00, btn01, btn02;
    @FXML private Button btn10, btn11, btn12;
    @FXML private Button btn20, btn21, btn22;

    private Button[][] buttons;
    private int emptyRow;
    private int emptyCol;

    @FXML
    public void initialize() {
        buttons = new Button[][]{
                {btn00, btn01, btn02},
                {btn10, btn11, btn12},
                {btn20, btn21, btn22}
        };

        scrambleBoard();
    }

    private void scrambleBoard() {
        List<String> tiles = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            tiles.add(String.valueOf(i));
        }
        tiles.add(""); // empty tile

        Collections.shuffle(tiles); // Randomize

        int idx = 0;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                String value = tiles.get(idx++);
                Button btn = buttons[r][c];
                btn.setText(value);

                if (value.equals("")) {
                    emptyRow = r;
                    emptyCol = c;
                }
            }
        }

        rebindActions();
    }

    private void handleMove(int row, int col) {
        if ((Math.abs(row - emptyRow) == 1 && col == emptyCol) ||
                (Math.abs(col - emptyCol) == 1 && row == emptyRow)) {

            Button clicked = buttons[row][col];
            Button empty = buttons[emptyRow][emptyCol];

            // Swap text
            empty.setText(clicked.getText());
            clicked.setText("");

            // Update empty location
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
                if (btn.getText().isEmpty()) {
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
        String[][] correct = {
                {"1", "2", "3"},
                {"4", "5", "6"},
                {"7", "8", ""}
        };

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (!buttons[r][c].getText().equals(correct[r][c])) {
                    return;
                }
            }
        }

        System.out.println("🎉 You won!");
    }
}

