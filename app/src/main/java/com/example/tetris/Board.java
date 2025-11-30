package com.example.tetris;
import java.util.Arrays;

public class Board {
    boolean[][] filled = new boolean[21][10];
    int[] countRow = new int[21];
    for(int i = 0; i < 10; i++) filled[0][i] = true;
    public Board() {

    }

    public int terminateRows() {
        int count = 0;
        for(int i = 1; i < 21; i++) {
            if(countRow[i] == 10) {
                count++;
                for(int j = i + 1; j < 21; j++) {
                    filled[j - 1] = filled[j];
                }
                filled[20] = new boolean[10];
            }
        }
        return count;
    }

}