package com.example.tetris.controller;

import com.example.tetris.model.GameModel;
import com.example.tetris.model.Tetromino;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

public class GameController {
    @FXML private Canvas gameCanvas;
    @FXML private Label scoreLabel;

    private GameModel model;
    private GraphicsContext gc;
    private static final int BLOCK_SIZE = 25;
    private long lastUpdate = 0;

    @FXML
    public void initialize() {
        model = new GameModel();
        gc = gameCanvas.getGraphicsContext2D();
        draw();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 1_000_000_000) {
                    model.drop();
                    draw();
                    lastUpdate = now;
                }
            }
        }.start();
    }

    public void handleInput(KeyEvent event) {
        KeyCode code = event.getCode();
        if (code == KeyCode.LEFT) model.move(-1, 0);
        else if (code == KeyCode.RIGHT) model.move(1, 0);
        else if (code == KeyCode.UP || code == KeyCode.W) model.rotate(1);
        else if (code == KeyCode.Q) model.rotate(-1);
        else if (code == KeyCode.DOWN) model.drop();

        draw();
    }

    private void draw() {
        // Clear background
        gc.setFill(Color.web("#2b2b33"));
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight()); // Transparent
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());  // Colored

        // 1. Draw Grid
        int[][] grid = model.getGrid();
        for (int r = 0; r < GameModel.ROWS; r++) {
            for (int c = 0; c < GameModel.COLS; c++) {
                if (grid[r][c] != 0) {
                    drawBlock(c, r, Tetromino.getColorByValue(grid[r][c])); // Helper still useful for Grid
                }
            }
        }

        // 2. Draw Current Piece (Using Object Data)
        Tetromino current = model.getCurrentTetromino();
        int[][] shape = current.getShape();
        int curX = current.x;
        int curY = current.y;
        Color color = current.getColor(); // Get specific color from object

        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[0].length; c++) {
                if (shape[r][c] != 0) {
                    drawBlock(curX + c, curY + r, color);
                }
            }
        }

        scoreLabel.setText("Score: " + model.getScore());
    }

    private void drawBlock(int x, int y, Color color) {
        gc.setFill(color);
        gc.fillRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE - 2, BLOCK_SIZE - 2);
    }
}