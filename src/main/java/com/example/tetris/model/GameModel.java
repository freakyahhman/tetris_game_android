package com.example.tetris.model;

public class GameModel {
    public static final int COLS = 12;
    public static final int ROWS = 20;

    private int[][] grid = new int[ROWS][COLS];

    // POLYMORPHISM: We treat any shape (I, L, T...) as a generic "Tetromino"
    private Tetromino currentTetromino;

    private int score = 0;

    public GameModel() {
        spawnPiece();
    }

    public void rotate(int dir) {
        int originalX = currentTetromino.x;
        int offset = 1;

        // 1. Delegate rotation to the object itself
        currentTetromino.rotate(dir);

        // 2. Wall Kick Logic (Model handles this because it owns the Grid)
        while (checkCollision(currentTetromino.x, currentTetromino.y, currentTetromino.getShape())) {
            currentTetromino.x += offset;
            offset = -(offset + (offset > 0 ? 1 : -1));

            if (offset > currentTetromino.getShape()[0].length + 2) {
                // Revert if kicks fail
                currentTetromino.rotate(-dir);
                currentTetromino.x = originalX;
                return;
            }
        }
    }

    public boolean move(int dx, int dy) {
        // Optimistic Move
        currentTetromino.move(dx, dy);

        if (checkCollision(currentTetromino.x, currentTetromino.y, currentTetromino.getShape())) {
            // Revert if hit something
            currentTetromino.move(-dx, -dy);
            return false;
        }
        return true;
    }

    public void drop() {
        if (!move(0, 1)) {
            merge();
            arenaSweep();
            spawnPiece();

            // Game Over Check
            if (checkCollision(currentTetromino.x, currentTetromino.y, currentTetromino.getShape())) {
                grid = new int[ROWS][COLS];
                score = 0;
            }
        }
    }

    private boolean checkCollision(int x, int y, int[][] shape) {
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[0].length; c++) {
                if (shape[r][c] != 0) {
                    int newX = x + c;
                    int newY = y + r;
                    if (newX < 0 || newX >= COLS || newY >= ROWS ||
                            (newY >= 0 && grid[newY][newX] != 0)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void merge() {
        int[][] shape = currentTetromino.getShape();
        int curX = currentTetromino.x;
        int curY = currentTetromino.y;

        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[0].length; c++) {
                if (shape[r][c] != 0) {
                    grid[curY + r][curX + c] = shape[r][c];
                }
            }
        }
    }

    private void arenaSweep() {
        int rowCount = 1;
        outer: for (int y = ROWS - 1; y > 0; y--) {
            for (int x = 0; x < COLS; x++) {
                if (grid[y][x] == 0) continue outer;
            }
            for (int k = y; k > 0; k--) {
                System.arraycopy(grid[k - 1], 0, grid[k], 0, COLS);
            }
            grid[0] = new int[COLS];
            y++;
            score += rowCount * 10;
            rowCount *= 2;
        }
    }

    private void spawnPiece() {
        // Use Factory to get a random specific subclass
        currentTetromino = TetrominoFactory.getRandomTetromino();

        // Set Spawn Position
        currentTetromino.x = (COLS / 2) - (currentTetromino.getShape()[0].length / 2);
        currentTetromino.y = 0;
    }

    // Getters
    public int[][] getGrid() { return grid; }
    public Tetromino getCurrentTetromino() { return currentTetromino; }
    public int getScore() { return score; }
}