package com.example.tetris;

import com.example.tetris.shapes.*;

public class Board {

    public boolean[][] filled = new boolean[25][10];
    public int[] countRow = new int[21];
    public Piece current;

    public int rows = 25;
    public int cols = 10;

    public Board() {
        // đáy = hàng 0 (theo hệ toạ độ của sếp)
        for (int i = 0; i < 10; i++) filled[0][i] = true;

        spawnPiece();
    }

    // =============================================================
    //                SPAWN PIECE NGẪU NHIÊN
    // =============================================================

    public void spawnPiece() {
        int rand = (int)(Math.random() * 7);

        // spawn ở giữa màn hình (col = 3)
        int x = 3;
        int y = 22;   // phía trên 20

        switch (rand) {
            // TODO: thêm ShapeO, ShapeT,... nếu sếp đã code rồi
            case 1: current = new ShapeJ(x, y, 0); break;
            case 2: current = new ShapeL(x, y, 0); break;
            case 3: current = new ShapeO(x, y); break;
            case 4: current = new ShapeS(x, y, 0); break;
            case 5: current = new ShapeT(x, y, 0); break;
            case 6: current = new ShapeZ(x, y, 0); break;
            default: current = new ShapeI(x, y, 0); break;
        }

        // liên kết board để tính projection
        current.board = this;

        // lần đầu phải tính projection
        current.projection = current.getProjection();
    }

    // =============================================================
    //                     UPDATE LOGIC (FALL)
    // =============================================================

    public void update() {

        if (current == null) {
            spawnPiece();
            return;
        }

        // nếu piece chạm đáy hoặc block khác
        if (current.checkDone()) {

            // fill block vào board
            current.fillBoard();

            // xoá hàng
            terminateRows();

            // spawn piece mới
            spawnPiece();
            return;
        }

        // NGƯỢC LẠI: chưa chạm → rơi xuống
        current.shiftDown();
    }

    // =============================================================
    //                     CLEAR ROWS
    // =============================================================

    public int terminateRows() {
        int cleared = 0;

        for (int r = 1; r < 21; r++) {
            if (countRow[r] == 10) {
                cleared++;

                // shift all rows above down
                for (int rr = r; rr < 20; rr++) {
                    filled[rr] = filled[rr + 1].clone();
                    countRow[rr] = countRow[rr + 1];
                }

                // top row becomes empty
                filled[20] = new boolean[10];
                countRow[20] = 0;

                r--; // recheck
            }
        }

        return cleared;
    }
}
