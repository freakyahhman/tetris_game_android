package com.example.tetris.model;

import javafx.scene.paint.Color;

import java.util.Random;

public abstract class Tetromino {
	protected int[][] shape;
	protected Color color;
	protected int value;
	protected int rotationIdx;
	protected char type;
	public int x, y; 

	public Tetromino(int[][] shape, Color color, char type) {
		this.shape = shape;
		this.color = color;
		this.x = 0; 
		this.y = 0;
		this.rotationIdx = 0;
		this.type = type;
	}

	public void move(int dx, int dy) {
		x += dx;
		y += dy;
	}

	public void rotate(int dir) {
		int n = shape.length;
		int[][] newShape = new int[n][n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				newShape[j][i] = shape[i][j];
			}
		}

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

	public int[][] getShape() { 
		return shape; 
	}
	public Color getColor() {
		return color;
	}
	public int getRotationIdx() {
		return this.rotationIdx; 
	}
	public char getType() {
		return this.type; 
	}

	public void setRI(int rotationIdx) {
		this.rotationIdx = rotationIdx;
	}

}

class TetrominoI extends Tetromino {
	public TetrominoI(Color c) {
		super(new int[][]{
			{0, 0, 0, 0},
			{1, 1, 1, 1},
			{0, 0, 0, 0},
			{0, 0, 0, 0}
		}, c, 'I');
	}
}

class TetrominoJ extends Tetromino {
	public TetrominoJ(Color c) {
		super(new int[][]{
			{1, 0, 0},
			{1, 1, 1},
			{0, 0, 0}
		}, c, 'J');
	}
}

class TetrominoL extends Tetromino {
	public TetrominoL(Color c) {
		super(new int[][]{
			{0, 0, 1},
			{1, 1, 1},
			{0, 0, 0}
		}, c, 'L');
	}
}

class TetrominoO extends Tetromino {
	public TetrominoO(Color c) {
		super(new int[][]{
			{1, 1},
			{1, 1}
		}, c, 'O');
	}
	// O không xoay
	@Override public void rotate(int dir) { }
}

class TetrominoS extends Tetromino {
	public TetrominoS(Color c) {
		super(new int[][]{
			{0, 1, 1},
			{1, 1, 0},
			{0, 0, 0}
		}, c, 'S');
	}
}

class TetrominoT extends Tetromino {
	public TetrominoT(Color c) {
		super(new int[][]{
			{0, 1, 0},
			{1, 1, 1},
			{0, 0, 0}
		}, c, 'T');
	}
}

class TetrominoZ extends Tetromino {
	public TetrominoZ(Color c) {
		super(new int[][]{
			{1, 1, 0},
			{0, 1, 1},
			{0, 0, 0}
		}, c, 'Z');
	}
}
