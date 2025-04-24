package com.example.catsandmazes;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

//import androidx.appcompat.app.AppCompatActivity;

public class GameOver extends Activity {
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameover_lose);
    }
}
