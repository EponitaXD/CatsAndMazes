package com.example.catsandmazes;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.util.*;
import android.view.WindowManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

public class MainActivity extends Activity {
    String[] difficultyLevels = {"Easy", "Medium", "Hard"};
    int difficultyIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Load saved difficulty
        SharedPreferences prefs = getSharedPreferences("game_settings", MODE_PRIVATE);
        difficultyIndex = prefs.getInt("difficulty_index", 0);
    }

    public void startGame(View view) {
        GameView gameView = new GameView(this);
        setContentView(gameView);
    }

    public void settings(View view) {
        setContentView(R.layout.settings);

        // Hook up buttons after the settings layout is loaded
        TextView difficultyText = findViewById(R.id.difficultyText);
        Button btnLeft = findViewById(R.id.btnLeft);
        Button btnRight = findViewById(R.id.btnRight);

        difficultyText.setText(difficultyLevels[difficultyIndex]); // Set initial

        btnLeft.setOnClickListener(v -> {
            difficultyIndex = (difficultyIndex - 1 + difficultyLevels.length) % difficultyLevels.length;
            difficultyText.setText(difficultyLevels[difficultyIndex]);
            saveDifficulty();
        });

        btnRight.setOnClickListener(v -> {
            difficultyIndex = (difficultyIndex + 1) % difficultyLevels.length;
            difficultyText.setText(difficultyLevels[difficultyIndex]);
            saveDifficulty();
        });
    }

    public void leaderboard(View view) {
        setContentView(R.layout.leaderboard);
    }

    public void MainMenu(View view) {
        setContentView(R.layout.index);
    }

    // Save difficulty to SharedPreferences
    private void saveDifficulty() {
        SharedPreferences prefs = getSharedPreferences("game_settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("difficulty_index", difficultyIndex);
        editor.apply();
    }
}
