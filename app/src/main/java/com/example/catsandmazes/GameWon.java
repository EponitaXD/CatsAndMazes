package com.example.catsandmazes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

public class GameWon extends Activity {
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameover_win);
    }

    public void startGame(View view) {
        GameView gameView = new GameView(this);
        setContentView(gameView);
    }

    public void MainMenu(View view) {
        Intent intent = new Intent(GameWon.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}