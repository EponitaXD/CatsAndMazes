package com.example.catsandmazes;

import android.graphics.BitmapFactory;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

public class Paw {
    Bitmap paw;
    int pawX, pawY;
    Rect rectPaw;

    public Paw(Context context,
                Bitmap paw,
                int pawX,
                int pawY,
                int dWidth) {
        this.paw = paw;
        this.pawX = pawX;
        this.pawY = pawY;
        rectPaw = new Rect(pawX, pawY, pawX+dWidth/10, pawY+dWidth*1235/(10*1373));
        Log.d("TOUCHED", "I'm inside Paw class");
    }
}
