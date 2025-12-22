package com.example.tetris.controller;

import com.example.tetris.model.GameModel;
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
    private static final int BLOCK_SIZE = 30;
    private long lastUpdate = 0;

    @FXML
    public void initialize() {
        model = new GameModel();
        gc = gameCanvas.getGraphicsContext2D();
        draw();
        startGameLoop();
    }

    private void startGameLoop() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 500_000_000) { // 0.5s per tick
                    update();
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }

    private void update() {
        if (model.isGameOver()) return;
        if (!model.move(0, 1)) {
            model.lockPiece();
        }
        draw();
    }

    public void handleInput(KeyEvent event) {
        if (model.isGameOver()) return;

        KeyCode code = event.getCode();
        if (code == KeyCode.LEFT) model.move(-1, 0);
        else if (code == KeyCode.RIGHT) model.move(1, 0);
        else if (code == KeyCode.UP) model.rotate();
        else if (code == KeyCode.DOWN) model.move(0, 1);

        draw();
    }

    private void draw() {
        // Clear background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        // Draw Locked Blocks
        int[][] grid = model.getGrid();
        for (int r = 0; r < GameModel.ROWS; r++) {
            for (int c = 0; c < GameModel.COLS; c++) {
                if (grid[r][c] != 0) {
                    gc.setFill(Color.GRAY);
                    gc.fillRect(c * BLOCK_SIZE, r * BLOCK_SIZE, BLOCK_SIZE - 1, BLOCK_SIZE - 1);
                }
            }
        }

        // Draw Current Piece
        int[][] shape = model.getCurrentShape();
        gc.setFill(model.getCurrentPiece().getColor());
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[0].length; c++) {
                if (shape[r][c] != 0) {
                    gc.fillRect((model.getCurrentX() + c) * BLOCK_SIZE,
                            (model.getCurrentY() + r) * BLOCK_SIZE,
                            BLOCK_SIZE - 1, BLOCK_SIZE - 1);
                }
            }
        }

        // Draw Game Over text
        if(model.isGameOver()){
            gc.setFill(Color.WHITE);
            gc.fillText("GAME OVER", 100, 300);
        }

        scoreLabel.setText("Score: " + model.getScore());
    }
}