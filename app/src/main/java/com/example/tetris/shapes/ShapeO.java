package com.example.tetris.shapes;
import com.example.tetris.Piece;

public class ShapeO extends Piece {
    public ShapeO(int xMin, int yMin) {
        super(xMin, yMin);
        this.xMax = xMin + 1;
        this.yMax = yMin + 1;
        this.blocks[0] = new int[]{yMin, xMin};
        this.blocks[1] = new int[]{yMin, xMin + 1};
        this.blocks[2] = new int[]{yMin + 1, xMin + 1};
        this.blocks[3] = new int[]{yMin + 1, xMin};
    }
}