package com.example.tetris.model;

import javafx.scene.paint.Color;
import java.util.Random;

// 1. ABSTRACTION: Define the blueprint for all blocks
public abstract class Tetromino {
    protected int[][] shape; // 2. ENCAPSULATION: Protected state
    protected Color color;
    protected int value;
    public int x, y; // Position is now stored INSIDE the object

    public Tetromino(int[][] shape, Color color, int value) {
        this.shape = shape;
        this.color = color;
        this.value = value;
        this.x = 0; // Will be set by GameModel on spawn
        this.y = 0;
    }

    // Common method for all children (INHERITANCE)
    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    // 3. POLYMORPHISM: Default rotation logic
    public void rotate(int dir) {
        // Standard matrix rotation algorithm
        int n = shape.length;
        int[][] newShape = new int[n][n];

        // Transpose
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                newShape[j][i] = shape[i][j];
            }
        }

        // Reverse rows (for clockwise) or columns (for counter-clockwise)
        if (dir > 0) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n / 2; j++) {
                    int temp = newShape[i][j];
                    newShape[i][j] = newShape[i][n - 1 - j];
                    newShape[i][n - 1 - j] = temp;
                }
            }
        } else {
            for(int i = 0; i < n / 2; i++){
                int[] temp = newShape[i];
                newShape[i] = newShape[n - 1 - i];
                newShape[n - 1 - i] = temp;
            }
        }
        this.shape = newShape;
    }

    // Getters
    public int[][] getShape() { return shape; }
    public Color getColor() { return color; }
    // ... inside public abstract class Tetromino ...

    // ✅ STATIC HELPER: Used to convert Grid numbers (1-7) back to Colors
    public static Color getColorByValue(int val) {
        switch (val) {
            case 1: return Color.CYAN;   // I
            case 2: return Color.ORANGE; // L
            case 3: return Color.BLUE;   // J
            case 4: return Color.YELLOW; // O
            case 5: return Color.RED;    // Z
            case 6: return Color.GREEN;  // S
            case 7: return Color.PURPLE; // T
            default: return Color.rgb(43, 43, 51); // Empty/Background color
        }
    }
    public int getValue() { return value; }
}

// --- CONCRETE SUBCLASSES (Inheritance) ---

class TetrominoI extends Tetromino {
    public TetrominoI() {
        super(new int[][]{{0, 1, 0, 0}, {0, 1, 0, 0}, {0, 1, 0, 0}, {0, 1, 0, 0}}, Color.CYAN, 1);
    }
}

class TetrominoL extends Tetromino {
    public TetrominoL() {
        super(new int[][]{{0, 2, 0}, {0, 2, 0}, {0, 2, 2}}, Color.ORANGE, 2);
    }
}

class TetrominoJ extends Tetromino {
    public TetrominoJ() {
        super(new int[][]{{0, 3, 0}, {0, 3, 0}, {3, 3, 0}}, Color.BLUE, 3);
    }
}

class TetrominoO extends Tetromino {
    public TetrominoO() {
        super(new int[][]{{4, 4}, {4, 4}}, Color.YELLOW, 4);
    }

    // POLYMORPHISM: O-Block overrides rotate to do nothing!
    @Override
    public void rotate(int dir) { }
}

class TetrominoZ extends Tetromino {
    public TetrominoZ() {
        super(new int[][]{{5, 5, 0}, {0, 5, 5}, {0, 0, 0}}, Color.RED, 5);
    }
}

class TetrominoS extends Tetromino {
    public TetrominoS() {
        super(new int[][]{{0, 6, 6}, {6, 6, 0}, {0, 0, 0}}, Color.GREEN, 6);
    }
}

class TetrominoT extends Tetromino {
    public TetrominoT() {
        super(new int[][]{{0, 7, 0}, {7, 7, 7}, {0, 0, 0}}, Color.PURPLE, 7);
    }
}

// FACTORY PATTERN: Helper to generate random blocks
class TetrominoFactory {
    public static Tetromino getRandomTetromino() {
        int pick = new Random().nextInt(7);
        switch (pick) {
            case 0: return new TetrominoI();
            case 1: return new TetrominoL();
            case 2: return new TetrominoJ();
            case 3: return new TetrominoO();
            case 4: return new TetrominoZ();
            case 5: return new TetrominoS();
            case 6: return new TetrominoT();
            default: return new TetrominoI();
        }
    }
}