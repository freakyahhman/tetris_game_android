package com.example.tetris.shapes;
import com.example.tetris.Piece;

public class ShapeI extends Piece {
    public ShapeI(int xMin, int yMin, int direction) {
        super(xMin, yMin);
        this.direction = direction;
        if(direction == 0) {
            this.xMax = xMin + 3;
            this.yMax = yMin;
            this.blocks[0] = new int[]{yMin, xMin};
            this.blocks[1] = new int[]{yMin, xMin + 1};
            this.blocks[2] = new int[]{yMin, xMin + 2};
            this.blocks[3] = new int[]{yMin, xMin + 3};
        }
        if(direction == 1) {
            this.xMax = xMin;
            this.yMax = yMin + 3;
            this.blocks[0] = new int[]{yMin, xMin};
            this.blocks[1] = new int[]{yMin + 1, xMin};
            this.blocks[2] = new int[]{yMin + 2, xMin};
            this.blocks[3] = new int[]{yMin + 3, xMin};
        }
    }

    @Override
    public void rotate() {
        ShapeI[] temp = new ShapeI[4];
        if(direction == 0) {
            temp[0] = new ShapeI(xMin + 2, yMin - 1, 1);
            temp[1] = new ShapeI(xMin + 3, yMin - 1, 1);
            temp[2] = new ShapeI(xMin + 1, yMin - 1, 1);
            temp[3] = new ShapeI(xMin, yMin - 1, 1);
        }
        if(direction == 1) {
            temp[0] = new ShapeI(xMin - 2, yMin - 2, 0);
            temp[1] = new ShapeI(xMin - 2, yMin - 3, 0);
            temp[2] = new ShapeI(xMin - 2, yMin - 1, 0);
            temp[3] = new ShapeI(xMin - 2, yMin, 0);
        }
        if(direction == 2) {
            temp[0] = new ShapeI(xMin + 1, yMin - 2, 1);
            temp[1] = new ShapeI(xMin , yMin - 2, 1);
            temp[2] = new ShapeI(xMin + 2, yMin - 2, 1);
            temp[3] = new ShapeI(xMin + 3, yMin - 2, 1);
        }
        if(direction == 3) {
            temp[0] = new ShapeI(xMin - 1, yMin + 1, 0);
            temp[1] = new ShapeI(xMin - 1, yMin, 0);
            temp[2] = new ShapeI(xMin - 1, yMin + 2, 0);
            temp[3] = new ShapeI(xMin - 1, yMin + 3, 0);
        }
        for(int i = 0; i < 4; i++) {
            temp[i].board = this.board;
            if(!checkCollision(temp[i].blocks)) {
                xMin = temp[i].xMin;
                xMax = temp[i].xMax;
                yMin = temp[i].yMin;
                yMax = temp[i].yMax;
                this.blocks = temp[i].blocks;
                this.projection = getProjection();
                direction = (direction + 1) % 4;
            }
        }
    }
}