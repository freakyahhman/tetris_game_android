package com.example.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {

    private Board board;
    private Thread gameThread;
    private boolean running = false;

    private Paint paint = new Paint();
    private float cellSize;

    private float touchStartX;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setFocusable(true);
        setFocusableInTouchMode(true);

        board = new Board();
    }

    // =============================================================
    //                        GAME LOOP
    // =============================================================

    public void startGame() {
        running = true;

        gameThread = new Thread(() -> {

            long lastUpdate = System.currentTimeMillis();

            while (running) {

                long now = System.currentTimeMillis();

                // Rơi mỗi 350ms
                if (now - lastUpdate >= 350) {
                    board.update();
                    postInvalidate();
                    lastUpdate = now;
                }

                try { Thread.sleep(10); }
                catch (Exception ignored) {}
            }
        });

        gameThread.start();
    }

    public void stopGame() {
        running = false;
        try {
            if (gameThread != null)
                gameThread.join();
        } catch (Exception ignored) {}
    }


    // =============================================================
    //                           DRAW
    // =============================================================

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int rows = board.rows;
        int cols = board.cols;

        cellSize = getWidth() / (float) cols;

        // nền
        canvas.drawRGB(30, 30, 30);

        // ============== Vẽ filled ===================
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board.filled[r][c]) {
                    paint.setColor(0xFF00B0FF); // xanh
                    drawCell(canvas, r, c);
                }
            }
        }

        // ============== Vẽ projection ===============
        if (board.current != null && board.current.projection != null) {
            paint.setColor(0x44FFFFFF); // trắng mờ
            for (int[] p : board.current.projection) {
                drawCell(canvas, p[0], p[1]);
            }
        }

        // ============== Vẽ current piece =============
        if (board.current != null) {
            paint.setColor(0xFFFF7043); // cam
            for (int[] p : board.current.blocks) {
                drawCell(canvas, p[0], p[1]);
            }
        }
    }

    private void drawCell(Canvas canvas, int row, int col) {
        float left = col * cellSize;
        float top  = (20 - row) * cellSize; // đảo trục của sếp: row 20 = top

        RectF rect = new RectF(left, top, left + cellSize, top + cellSize);
        canvas.drawRect(rect, paint);
    }


    // =============================================================
    //                        KEY INPUT
    // =============================================================

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (board.current == null) return true;

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_A:
                board.current.shiftLeft();
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_D:
                board.current.shiftRight();
                break;

            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_S:
                board.current.shiftDown();
                break;

            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_W:
            case KeyEvent.KEYCODE_SPACE:
                board.current.rotate();
                break;
        }

        invalidate();
        return true;
    }


    // =============================================================
    //                     TOUCH INPUT
    // =============================================================

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (board.current == null) return true;

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                touchStartX = event.getX();
                return true;

            case MotionEvent.ACTION_UP:
                float dx = event.getX() - touchStartX;

                if (dx > 100) {
                    board.current.shiftRight();
                } else if (dx < -100) {
                    board.current.shiftLeft();
                } else {
                    board.current.rotate();
                }

                invalidate();
                return true;
        }

        return true;
    }
}
