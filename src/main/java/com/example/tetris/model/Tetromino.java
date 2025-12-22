package com.example.tetris.model;

import javafx.scene.paint.Color;

public enum Tetromino {
    I(new int[][]{{1, 1, 1, 1}}, Color.CYAN),
    J(new int[][]{{1, 0, 0}, {1, 1, 1}}, Color.BLUE),
    L(new int[][]{{0, 0, 1}, {1, 1, 1}}, Color.ORANGE),
    O(new int[][]{{1, 1}, {1, 1}}, Color.YELLOW),
    S(new int[][]{{0, 1, 1}, {1, 1, 0}}, Color.GREEN),
    T(new int[][]{{0, 1, 0}, {1, 1, 1}}, Color.PURPLE),
    Z(new int[][]{{1, 1, 0}, {0, 1, 1}}, Color.RED);

    private final int[][] shape;
    private final Color color;

    Tetromino(int[][] shape, Color color) {
        this.shape = shape;
        this.color = color;
    }

    public int[][] getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }
}