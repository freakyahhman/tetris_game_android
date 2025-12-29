package com.example.tetris.model;

import java.awt.Point;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameModel {
    public static final int COLS = 12;
    public static final int ROWS = 20;

    private Color[][] grid = new Color[ROWS][COLS];
    private Tetromino currentTetromino;
    private Tetromino nextTetromino;
    private List<Integer> bag;
    private int currentIndex;
    private final static Color[] COLOR = {Color.INDIANRED, Color.MEDIUMPURPLE, Color.LIGHTGREEN, Color.YELLOW, Color.DODGERBLUE, Color.CORAL, Color.CYAN};

    private int score = 0;
    private int increaseScore = 0;
    private int line = 0;
    
    // Biến trạng thái để controller biết game đã kết thúc chưa
    private boolean isGameOver = false;

    public GameModel() {
        initBag();
        spawnPiece();
    }

    public void initBag() {
        bag = new ArrayList<>();
        for (int i = 0; i < 7; i++) bag.add(i);
        refill();
    }

    private void refill() {
        Collections.shuffle(bag);
        currentIndex = 0;
    }

    public Tetromino getNextPiece(char state) {
        int pick = bag.get(currentIndex);
        currentIndex++;
        if(state == 'p') currentIndex--;
        if (currentIndex >= bag.size()) refill();
        
        return switch (pick) {
            case 0 -> new TetrominoI(COLOR[pick]);
            case 1 -> new TetrominoL(COLOR[pick]);
            case 2 -> new TetrominoJ(COLOR[pick]);
            case 3 -> new TetrominoO(COLOR[pick]);
            case 4 -> new TetrominoZ(COLOR[pick]);
            case 5 -> new TetrominoS(COLOR[pick]);
            case 6 -> new TetrominoT(COLOR[pick]);
            default -> new TetrominoI(COLOR[pick]);
        };
    }

    private void spawnPiece() {
        currentTetromino = getNextPiece('g');
        nextTetromino = getNextPiece('p');
        currentTetromino.x = (COLS / 2) - (currentTetromino.getShape()[0].length / 2);
        currentTetromino.y = 0;

        // Check Game Over ngay khi spawn (nếu bị kẹt)
        if (checkCollision(currentTetromino.x, currentTetromino.y, currentTetromino.getShape())) {
            isGameOver = true;
        }
    }

    public void rotateTetromino(int dir) {
        if (isGameOver) return;
        int oldRI = this.currentTetromino.getRotationIdx();
        int newRI = (oldRI + dir + 4) % 4;
        int originalX = this.currentTetromino.x;
        int originalY = this.currentTetromino.y;
        int[][] originalShape = this.currentTetromino.shape;

        this.currentTetromino.rotate(dir);
        Point[] kicks = SRS.getKicks(this.currentTetromino.getType(), oldRI, newRI);

        for (Point kick : kicks) {
            this.currentTetromino.x = originalX + kick.x;
            this.currentTetromino.y = originalY + kick.y;
            if (!checkCollision(this.currentTetromino.x, this.currentTetromino.y, this.currentTetromino.getShape())) {
                this.currentTetromino.setRI(newRI);
                return;
            }
        }
        this.currentTetromino.x = originalX;
        this.currentTetromino.y = originalY;
        this.currentTetromino.shape = originalShape;
    }

    public boolean moveTetromino(int dx, int dy) {
        if (isGameOver) return false;
        currentTetromino.move(dx, dy);
        if (checkCollision(currentTetromino.x, currentTetromino.y, currentTetromino.getShape())) {
            currentTetromino.move(-dx, -dy);
            return false;
        }
        return true;
    }

    public void hardDrop() {
        if (isGameOver) return;
        while(moveTetromino(0,1));
        drop();
    }

    // Sửa logic drop: Trả về trạng thái Game Over
    public boolean drop() {
        if (isGameOver) return true;
        
        if (!moveTetromino(0, 1)) {
            lock();
            clearLine();
            spawnPiece();
        }
        return isGameOver;
    }

    private boolean checkCollision(int x, int y, int[][] shape) {
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[0].length; c++) {
                if (shape[r][c] != 0) {
                    int newX = x + c;
                    int newY = y + r;
                    if (newX < 0 || newX >= COLS || newY >= ROWS || (newY >= 0 && grid[newY][newX] != null)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void lock() {
        int[][] shape = currentTetromino.getShape();
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[0].length; c++) {
                if (shape[r][c] != 0) {
                    grid[currentTetromino.y + r][currentTetromino.x + c] = currentTetromino.getColor();
                }
            }
        }
    }

    private void clearLine() {
        int rowCount = 0;
        outer: for (int y = ROWS - 1; y >= 0; y--) {
            for (int x = 0; x < COLS; x++) {
                if (grid[y][x] == null) continue outer;
            }
            rowCount++;
            for (int k = y; k > 0; k--) {
                System.arraycopy(grid[k - 1], 0, grid[k], 0, COLS);
            }
            grid[0] = new Color[COLS];
            y++; 
        }
        if (rowCount > 0) {
            increaseScore += (rowCount * 200) + ((rowCount - 1) * 100); // Cách tính điểm ví dụ
            line += rowCount;
        }
    }

    public void clear() {
        for(int r = 0; r < ROWS; r++) {
            for(int c = 0; c < COLS; c++) grid[r][c] = null;
        }
        this.line = 0;
        this.score = 0;
        this.increaseScore = 0;
        this.isGameOver = false;
        initBag();
        spawnPiece();
    }

    public int getGhostY() {
        int ghostY = currentTetromino.y;
        while (!checkCollision(currentTetromino.x, ghostY + 1, currentTetromino.getShape())) {
            ghostY++;
        }
        return ghostY;
    }

    public int getLine() { return line; }
    public Color[][] getGrid() { return grid; }
    public Tetromino getCurrentTetromino() { return currentTetromino; }
    public Tetromino getNextTetromino() { return nextTetromino; }
    public int getScore() { return score; }
    public int getIncreaseScore() { return increaseScore; }
    public boolean isGameOver() { return isGameOver; }

    public void updateScore() {
        if(this.increaseScore > 0) {
            int d = Math.max(1, increaseScore / 5); 
            score += d;
            increaseScore -= d;
        }
    }
}