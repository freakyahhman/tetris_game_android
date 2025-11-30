package com.example.tetris;

public abstract class Piece {
    protected int xMin, yMin, xMax, yMax;
    protected int[][] blocks;
    public Board board;
    public int[][] projection;
    protected int direction = 0;

    public Piece(int xMin, int yMin) {
        this.xMin = xMin;
        this.yMin = yMin;
    }

    public void fitBoard() {
        if (xMin < 0) {
            for(int[] block : blocks) {
                block[1] -= xMin;
            }
            xMax -= xMin;
            xMin = 0;
        }
        if(xMax > 9) {
            int change = xMax - 9;
            for(int[] block : blocks) {
                block[1] -= change;
            }
            xMin -= change;
            xMax = 9;
        }
    }

    public int[][] getProjection() {
        int[][] ans = new int[blocks.length][];
        for(int i = 0; i < blocks.length; i++) {
            ans[i] = blocks[i].clone();
        }
        boolean next = true;
        for(int[] point : ans) {
            if(board.filled[point[0] - 1][point[1]]) {
                next = false;
                break;
            }
        }
        if(!next)
            return ans;
        for(int[] point : ans) {
            point[0] -= 1;
        }
        return ans;
    }
    public void rotate() {}

    public boolean checkDone() {
        for (int i = 0; i < blocks.length; i++) {
            if (blocks[i][0] == projection[i][0]) return true;
        }
        return false;
    }


    public boolean checkCollision(int blocks[][]) {
        for(int[] block : blocks) {
            if(block[1] < 0 || block[1] > 9) return true;
            if(board.filled[block[0]][block[1]]) return true;
        }
        return false;
    }

    public void shiftLeft() {
        if(xMin == 0) return;
        int[][] ans = new int[blocks.length][];
        for(int i = 0; i < blocks.length; i++) {
            ans[i] = blocks[i].clone();
        }
        for(int[] block : ans) block[1] -= 1;
        if(!checkCollision(ans)) {
            xMin -= 1;
            xMax -= 1;
            blocks = ans;
            projection = getProjection();
        }
    }
    public void shiftRight() {
        if(xMax == 9) return;
        int[][] ans = new int[blocks.length][];
        for(int i = 0; i < blocks.length; i++) {
            ans[i] = blocks[i].clone();
        }
        for(int[] block : ans) block[1] += 1;
        if(!checkCollision(ans)) {
            xMin += 1;
            xMax += 1;
            blocks = ans;
            projection = getProjection();
        }
    }
    public void shiftDown() {
        if(checkDone()) return;
        for(int[] block : blocks) block[0] -= 1;
        yMin -= 1;
        yMax -= 1;
    }

    public void fillBoard() {
        if(!checkDone()) return;
        for(int[] block : blocks) {
            board.filled[block[0]][block[1]] = true;
            board.countRow[block[0]]++;
        }
    }
}