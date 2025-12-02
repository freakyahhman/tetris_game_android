package com.example.tetris;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        gameView = findViewById(R.id.gameView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameView != null) gameView.startGame();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameView != null) gameView.stopGame();
    }
}
