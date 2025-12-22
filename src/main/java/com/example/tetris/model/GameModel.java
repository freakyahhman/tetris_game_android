package com.example.tetris.model;

import java.util.Random;

public class GameModel {
    public static final int COLS = 10;
    public static final int ROWS = 20;
    private int[][] grid = new int[ROWS][COLS];

    private Tetromino currentPiece;
    private int[][] currentShape;
    private int currentX, currentY;
    private int rotationState = 0; // 0: Spawn, 1: Right, 2: 180, 3: Left

    private boolean gameOver = false;
    private int score = 0;

    // --- SRS WALL KICK DATA (Standard Tetris) ---
    // Format: {x, y}. Java Y-axis is down, so Up is -1, Down is +1.
    // JLSTZ Wall Kicks (5 tests per rotation)
    // 0->1, 1->2, 2->3, 3->0 (Clockwise)
    private static final int[][][] WALL_KICK_JLSTZ = {
            {{0, 0}, {-1, 0}, {-1, -1}, { 0, +2}, {-1, +2}}, // 0 -> 1
            {{0, 0}, {+1, 0}, {+1, +1}, { 0, -2}, {+1, -2}}, // 1 -> 2
            {{0, 0}, {+1, 0}, {+1, -1}, { 0, +2}, {+1, +2}}, // 2 -> 3
            {{0, 0}, {-1, 0}, {-1, +1}, { 0, -2}, {-1, -2}}  // 3 -> 0
    };

    // I Wall Kicks (Different rules for 'I' piece)
    private static final int[][][] WALL_KICK_I = {
            {{0, 0}, {-2, 0}, {+1, 0}, {-2, +1}, {+1, -2}}, // 0 -> 1
            {{0, 0}, {-1, 0}, {+2, 0}, {-1, -2}, {+2, +1}}, // 1 -> 2
            {{0, 0}, {+2, 0}, {-1, 0}, {+2, -1}, {-1, +2}}, // 2 -> 3
            {{0, 0}, {+1, 0}, {-2, 0}, {+1, +2}, {-2, -1}}  // 3 -> 0
    };

    public GameModel() {
        spawnPiece();
    }

    public void spawnPiece() {
        Tetromino[] values = Tetromino.values();
        currentPiece = values[new Random().nextInt(values.length)];
        currentShape = currentPiece.getShape();

        // Căn giữa khi spawn
        currentX = COLS / 2 - currentShape[0].length / 2;
        currentY = 0;
        rotationState = 0; // Reset rotation state

        if (!isValidPosition(currentX, currentY, currentShape)) {
            gameOver = true;
        }
    }

    public boolean move(int dx, int dy) {
        if (isValidPosition(currentX + dx, currentY + dy, currentShape)) {
            currentX += dx;
            currentY += dy;
            return true;
        }
        return false;
    }

    // --- LOGIC XOAY MỚI VỚI WALL KICK ---
    public void rotate() {
        if (currentPiece == Tetromino.O) return; // Khối O không cần xoay

        int[][] rotatedShape = getRotatedShape(currentShape);
        int nextState = (rotationState + 1) % 4;

        // Lấy bảng kick data tương ứng với loại khối (I hoặc JLSTZ)
        int[][][] kickData = (currentPiece == Tetromino.I) ? WALL_KICK_I : WALL_KICK_JLSTZ;

        // Lấy dòng test tương ứng với trạng thái hiện tại (rotationState)
        // Vì bảng trên định nghĩa 0->1, 1->2... nên ta dùng rotationState làm index
        int[][] tests = kickData[rotationState];

        // Thử lần lượt 5 trường hợp (Test 1 là vị trí gốc, Test 2-5 là kick)
        for (int[] offset : tests) {
            int kickX = offset[0];
            int kickY = offset[1]; // Lưu ý: trong bảng data, Y+ là xuống dưới (đúng với Java)

            // Kiểm tra xem nếu dịch chuyển theo offset này thì có hợp lệ không
            if (isValidPosition(currentX + kickX, currentY + kickY, rotatedShape)) {
                // Thành công! Áp dụng thay đổi
                currentShape = rotatedShape;
                currentX += kickX;
                currentY += kickY;
                rotationState = nextState;
                return; // Xoay xong thì thoát ngay
            }
        }
        // Nếu chạy hết vòng lặp mà không khớp trường hợp nào -> Xoay thất bại (giữ nguyên)
    }

    private int[][] getRotatedShape(int[][] shape) {
        int w = shape.length;
        int h = shape[0].length;
        int[][] rotated = new int[h][w];

        for (int row = 0; row < w; row++) {
            for (int col = 0; col < h; col++) {
                // Xoay 90 độ theo chiều kim đồng hồ
                rotated[col][w - 1 - row] = shape[row][col];
            }
        }
        return rotated;
    }

    private boolean isValidPosition(int x, int y, int[][] shape) {
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[0].length; col++) {
                if (shape[row][col] != 0) {
                    int newX = x + col;
                    int newY = y + row;
                    // Check bounds & collision
                    if (newX < 0 || newX >= COLS || newY >= ROWS || (newY >= 0 && grid[newY][newX] != 0)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void lockPiece() {
        for (int row = 0; row < currentShape.length; row++) {
            for (int col = 0; col < currentShape[0].length; col++) {
                if (currentShape[row][col] != 0) {
                    // Cần check boundary lần nữa để tránh lỗi ArrayOutOfBounds khi game over sát trần
                    if (currentY + row >= 0 && currentY + row < ROWS && currentX + col >= 0 && currentX + col < COLS) {
                        grid[currentY + row][currentX + col] = 1;
                    }
                }
            }
        }
        clearLines();
        spawnPiece();
    }

    private void clearLines() {
        for (int row = ROWS - 1; row >= 0; row--) {
            boolean full = true;
            for (int col = 0; col < COLS; col++) {
                if (grid[row][col] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                score += 100;
                for (int r = row; r > 0; r--) {
                    System.arraycopy(grid[r - 1], 0, grid[r], 0, COLS);
                }
                grid[0] = new int[COLS]; // Dòng trên cùng thành rỗng
                row++; // Kiểm tra lại dòng vừa tụt xuống
            }
        }
    }

    // Getters
    public int[][] getGrid() { return grid; }
    public Tetromino getCurrentPiece() { return currentPiece; }
    public int[][] getCurrentShape() { return currentShape; }
    public int getCurrentX() { return currentX; }
    public int getCurrentY() { return currentY; }
    public boolean isGameOver() { return gameOver; }
    public int getScore() { return score; }
}