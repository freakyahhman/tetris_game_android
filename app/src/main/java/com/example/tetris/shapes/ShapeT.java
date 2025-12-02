package com.example.tetris.shapes;
import com.example.tetris.Piece;

public class ShapeT extends Piece {
    public ShapeT(int xMin, int yMin, int direction) {
        super(xMin, yMin);
        this.direction = direction;
        if(direction == 0) {
            this.xMax = xMin + 2;
            this.yMax = yMin + 1;
            this.blocks[0] = new int[]{yMin, xMin};
            this.blocks[1] = new int[]{yMin, xMin + 1};
            this.blocks[2] = new int[]{yMin, xMin + 2};
            this.blocks[3] = new int[]{yMin + 1, xMin + 1};
        }
        if(direction == 1) {
            this.xMax = xMin + 1;
            this.yMax = yMin + 2;
            this.blocks[0] = new int[]{yMin, xMin + 1};
            this.blocks[1] = new int[]{yMin + 1, xMin + 1};
            this.blocks[2] = new int[]{yMin + 2, xMin + 1};
            this.blocks[3] = new int[]{yMin + 1, xMin};
        }
        if(direction == 2) {
            this.xMax = xMin + 2;
            this.yMax = yMin + 1;
            this.blocks[0] = new int[]{yMin, xMin + 1};
            this.blocks[1] = new int[]{yMin + 1, xMin};
            this.blocks[2] = new int[]{yMin + 1, xMin + 1};
            this.blocks[3] = new int[]{yMin + 1, xMin + 2};
        }
        if(direction == 3) {
            this.xMax = xMin + 1;
            this.yMax = yMin + 2;
            this.blocks[0] = new int[]{yMin, xMin};
            this.blocks[1] = new int[]{yMin + 1, xMin};
            this.blocks[2] = new int[]{yMin + 2, xMin};
            this.blocks[3] = new int[]{yMin + 1, xMin + 1};
        }
    }

    @Override
    public void rotate() {
        ShapeT[] temp = new ShapeT[4];
        if(direction == 0) {
            temp[0] = new ShapeT(xMin, yMin - 1, 1);
            temp[1] = new ShapeT(xMin + 1, yMin - 1, 1);
            temp[2] = new ShapeT(xMin, yMin, 1);
            temp[3] = new ShapeT(xMin + 1, yMin, 1);
        }
        if(direction == 1) {
            temp[0] = new ShapeT(xMin, yMin, 2);
            temp[1] = new ShapeT(xMin, yMin + 1, 2);
            temp[2] = new ShapeT(xMin - 1, yMin, 2);
            temp[3] = new ShapeT(xMin - 1, yMin + 1, 2);
        }
        if(direction == 2) {
            temp[0] = new ShapeT(xMin + 1, yMin, 3);
            temp[1] = new ShapeT(xMin + 1 , yMin + 1, 3);
            temp[2] = new ShapeT(xMin, yMin, 3);
            temp[3] = new ShapeT(xMin, yMin + 1, 3);
        }
        if(direction == 3) {
            temp[0] = new ShapeT(xMin - 1, yMin + 1, 0);
            temp[1] = new ShapeT(xMin - 1, yMin, 0);
            temp[2] = new ShapeT(xMin, yMin + 1, 0);
            temp[3] = new ShapeT(xMin, yMin, 0);
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
                return;
            }
        }
    }
}