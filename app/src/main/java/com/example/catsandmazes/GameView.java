package com.example.catsandmazes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Bitmap;
import android.content.Context;
import android.os.Handler;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends View {
    // hardcoded mazes
    boolean isWon = false;
    boolean isOver = false;
    Node[] mazeSolutionNodes;
    int[][] mazeSolution;
    int[][] maze0;
    /*
    int[][] maze0 = {
            {1,1,1,0,0,0,1,0,1,1},
            {0,1,0,1,1,1,1,1,1,0},
            {1,1,2,1,0,1,0,0,1,0},
            {0,1,0,1,0,1,1,1,1,1},
            {0,1,1,1,0,0,1,0,0,1},
            {0,0,1,0,1,0,0,0,0,1},
            {1,1,1,1,1,0,3,0,1,1},
            {0,1,0,0,1,0,1,0,0,1},
            {1,1,1,0,1,0,1,1,1,1},
            {0,0,1,1,1,1,0,0,0,0}
    };
    */
    /*
    int[][] maze0 = {
            {2, 1, 1},
            {1, 0, 1},
            {0, 0, 3}
    };
    */

    // Bitmaps
    Bitmap paw, forwardBitmap, background, cat, pathBitmap, pathFinalBitmap, lightningBitmap, arrowLeft, arrowRight, arrowDown, arrowUp;

    // sounds. rand var used to rotate sounds
    MediaPlayer[] mpHappy = new MediaPlayer[3];
    MediaPlayer[] mpWin = new MediaPlayer[3];
    MediaPlayer mpOver;

    Random rand = new Random();

    // Coordinates
    int catX, catY;
    int catIndexX, catIndexY;
    float prevX;
    float prevY;

    // reference rectangles
    Rect rectForward, rectPaw, rectBackground, rectCat, rectPath, rectLightning, rectLeft, rectRight, rectDown, rectUp, rectArrows;
    Context context;
    Handler handler;
    final long UPDATE_MILLS = 30;
    // font vars
    float TEXT_SIZE = 120;
    int energy = 19;
    int optimalNumSteps = 19;
    // device dimentions
    static int dWidth, dHeight;
    // array to store path instances
    ArrayList<Path> paths;
    ArrayList<Paw> paws;
    Runnable runnable;
    // paints
    Paint textPaint = new Paint();
    Paint backgroundPaint = new Paint();

    public GameView(Context context) {
        super(context);
        this.context = context;

        MazeGenerator generator = new MazeGenerator("Medium");
        maze0 = generator.generateMaze();

        FloodFill solve = new FloodFill(maze0);
        mazeSolutionNodes = solve.solve();

        optimalNumSteps = mazeSolutionNodes.length;
        energy =  (int) (optimalNumSteps* 1.5);


        // initialize Sounds
        mpHappy[0] = MediaPlayer.create(context, R.raw.happycat0);
        mpHappy[1] = MediaPlayer.create(context, R.raw.happycat1);
        mpHappy[2] = MediaPlayer.create(context, R.raw.happycat);
        mpWin[0] = MediaPlayer.create(context, R.raw.victorycat0);
        mpWin[1] = MediaPlayer.create(context, R.raw.victorycat1);
        mpWin[2] = MediaPlayer.create(context, R.raw.victorycat2);
        mpOver = MediaPlayer.create(context, R.raw.sadcat);

        // initialize Bitmaps
        forwardBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fast_forward_circle_fill);
        paw = BitmapFactory.decodeResource(getResources(), R.drawable.catpaw);
        cat = BitmapFactory.decodeResource(getResources(), R.drawable.cat_sprite);
        pathBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.grass_sprite_3d);
        lightningBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lightning_charge_fill);
        arrowDown = BitmapFactory.decodeResource(getResources(), R.drawable.play_fill_down);
        arrowUp = BitmapFactory.decodeResource(getResources(), R.drawable.play_fill_up);
        arrowLeft = BitmapFactory.decodeResource(getResources(), R.drawable.play_fill_left);
        arrowRight = BitmapFactory.decodeResource(getResources(), R.drawable.play_fill_right);
        pathFinalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.grass_sprite_3d_fish);
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        // Device width and height
        dWidth = size.x;
        dHeight = size.y;
        Log.d("debug", dWidth + " " + dHeight);
        // initialize reference rectangle
        rectForward = new Rect(dWidth-dWidth/4, 0, dWidth, dWidth/4);
        rectBackground = new Rect(0,0, dWidth, dHeight);
        rectCat = new Rect(0,0, dWidth/4, dWidth*2363/(4*1796));
        rectPaw = new Rect(0,0, dWidth/8, dWidth*1235/(8*1373));
        rectPath = new Rect(0,200, dWidth/4, 200+dWidth*235/1568);
        rectLightning = new Rect(0, 20, dWidth/9,20+dWidth/9);
        rectDown = new Rect(dWidth-dWidth*2/9, dHeight-dWidth*2/9, dWidth-dWidth/9,dHeight-dWidth/9);
        rectUp = new Rect(dWidth-dWidth*2/9, dHeight-dWidth*4/9, dWidth-dWidth/9,dHeight-dWidth*3/9);
        rectLeft = new Rect(dWidth-dWidth*3/9, dHeight-dWidth*3/9, dWidth-dWidth*2/9,dHeight-dWidth*2/9);
        rectRight = new Rect(dWidth-dWidth/9, dHeight-dWidth*3/9, dWidth,dHeight-dWidth*2/9);
        rectArrows = new Rect(dWidth-dWidth*3/9, dHeight-dWidth*4/9, dWidth, dHeight-dWidth/9);

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                invalidate();
            }
        };
        // initialize paints
        textPaint.setColor(Color.rgb(255,255,255));
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface(ResourcesCompat.getFont(context, R.font.caveatbrushregular));
        backgroundPaint.setColor(Color.rgb(111, 196, 252));

        // initialize coordinates
        catX = dWidth / 2 - cat.getWidth() /2;
        catY = dHeight/2 - cat.getHeight() / 2;
        paths = new ArrayList<>();
        paws = new ArrayList<>();

        // Initialize the paws (optimal route markers)
        int pawX = 0;
        int pawY = dHeight/5;
        mazeSolution = new int[maze0[0].length][maze0[0].length]; // maze is square
        for (Node e : mazeSolutionNodes) {
            mazeSolution[e.y][e.x] = 1;
            Log.d("TOUCHED", "paw created: " + e.y + ", " + e.x);
        }

        // initialise number of paths
        int previousX = 0;
        int pathX = 0;
        int pathY = dHeight/5;
        for (int i = 0, l = maze0.length; i < l; i++) {
            for (int j = 0, w = maze0[i].length; j < w; j++) {
                if (j == 0){
                    previousX = pathX;
                }
                if (mazeSolution[i][j] > 0) {
                    paws.add(new Paw(context, paw, pathX, pathY, dWidth));
                    Log.d("TOUCHED", "I'm inside the loop");
                }
                if(maze0[i][j] == 1) {
                    paths.add(new Path(context, pathBitmap, pathX, pathY, dWidth, maze0[i][j]));
                }
                // if it is the start of the maze
                // set cat's coordinates
                else if (maze0[i][j] == 2) {
                    paths.add(new Path(context, pathBitmap, pathX, pathY, dWidth, maze0[i][j]));
                    catIndexX = j;
                    catIndexY = i;
                    catX = pathX - 20;
                    catY = pathY - dWidth*2363/(5*1796);
                    rectCat.set(catX,catY, catX+dWidth/4, catY+dWidth*2363/(4*1796));
                }
                else if (maze0[i][j] == 3) {
                    paths.add(new Path(context, pathFinalBitmap, pathX, pathY, dWidth, maze0[i][j]));
                }
                pathX += dWidth/4;
            }
            pathX = previousX - dWidth/12;
            pathY += dWidth*27/224;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw background
        canvas.drawRect(0,0,dWidth, dHeight, backgroundPaint);
        // print the paths according to the maze
        for (Path p : paths) {
            canvas.drawBitmap(p.path, null, p.rectPath, null);
        }
        // if won/Gameover display optimal route
        if (isWon || isOver) {
            for (Paw pa : paws) {
                canvas.drawBitmap(pa.paw,null, pa.rectPaw, null);
            }
            // Display button to continue
            canvas.drawBitmap(forwardBitmap, null, rectForward, null);
        }
        // draw cat
        canvas.drawBitmap(cat, null, rectCat, null);
        //draw energy
        canvas.drawBitmap(lightningBitmap, null, rectLightning, null);
        canvas.drawText("    " + energy, 20, TEXT_SIZE, textPaint);
        // draw controls
        canvas.drawBitmap(arrowDown, null, rectDown, null);
        canvas.drawBitmap(arrowUp, null, rectUp, null);
        canvas.drawBitmap(arrowLeft, null, rectLeft, null);
        canvas.drawBitmap(arrowRight, null, rectRight, null);
        handler.postDelayed(runnable, UPDATE_MILLS);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float shiftX;
        float shiftY;
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            // if won/gameover allow to click continue
            if (isWon) {
                if(rectForward.contains((int) x, (int) y)) {
                    int temp = rand.nextInt(2);
                    mpWin[temp].start();
                    // if end of maze, celebrate
                    Intent intent = new Intent(context, GameWon.class);
                    //intent.putExtra("points", energy - optimalNumSteps);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }
            if (isOver) {
                if(rectForward.contains((int) x, (int) y)) {
                    mpOver.start();
                    Intent intent = new Intent(context, GameOver.class);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }
            //Check if the x and y position of the touch is inside the bitmap
            if (rectArrows.contains((int) x, (int) y)) {
                int temp = rand.nextInt(2);
                if(rectDown.contains((int) x, (int) y)) {
                    Log.d("TOUCHED", "Down. X: " + x + " Y: " + y);
                    //Bitmap touched
                    // Check if there is path
                    // move cat
                    if (catIndexY + 1 < maze0.length && catIndexY + 1 >= 0) {
                        if (maze0[catIndexY+1][catIndexX] > 0) {
                            if (!isOver && !isWon)
                                energy--;
                            catIndexY++;
                            catX -= dWidth/12;
                            catY += dWidth*27/224;
                            rectCat.set(catX,catY, catX+dWidth/4, catY+dWidth*2363/(4*1796));
                            mpHappy[temp].start();
                        }
                    }
                    // otherwise do nothing
                }
                else if(rectUp.contains((int) x, (int) y)) {
                    Log.d("TOUCHED", "Up. X: " + x + " Y: " + y);
                    //Bitmap touched
                    // Check if there is path
                    // move cat
                    if (catIndexY - 1 < maze0.length && catIndexY - 1 >= 0){
                        if (maze0[catIndexY-1][catIndexX] > 0) {
                            if (!isOver && !isWon)
                                energy--;
                            catIndexY--;
                            catX += dWidth/12;
                            catY -= dWidth*27/224;
                            rectCat.set(catX,catY, catX+dWidth/4, catY+dWidth*2363/(4*1796));
                            mpHappy[temp].start();
                        }
                    }
                    // otherwise do nothing
                }
                else if(rectLeft.contains((int) x, (int) y)) {
                    Log.d("TOUCHED", "Left. X: " + x + " Y: " + y);
                    //Bitmap touched
                    // Check if there is path
                    // move cat
                    if (catIndexX - 1 < maze0[0].length && catIndexX - 1 >= 0) {
                        if (maze0[catIndexY][catIndexX-1] > 0) {
                            if (!isOver && !isWon)
                                energy--;
                            catIndexX--;
                            catX -= dWidth/4;
                            rectCat.set(catX,catY, catX+dWidth/4, catY+dWidth*2363/(4*1796));
                            mpHappy[temp].start();
                        }
                    }
                    // otherwise do nothing
                }
                else if(rectRight.contains((int) x, (int) y)) {
                    Log.d("TOUCHED", "Right. X: " + x + " Y: " + y);
                    //Bitmap touched
                    // Check if there is path
                    // move cat
                    if (catIndexX + 1 < maze0[0].length && catIndexX + 1 >= 0){
                        if (maze0[catIndexY][catIndexX+1] > 0) {
                            if (!isOver && !isWon)
                                energy--;
                            catIndexX++;
                            catX += dWidth/4;
                            rectCat.set(catX,catY, catX+dWidth/4, catY+dWidth*2363/(4*1796));
                            mpHappy[temp].start();
                        }
                    }
                    // otherwise do nothing
                }
                if (maze0[catIndexY][catIndexX] == 3 && !isOver){
                    // Display Optimal route
                    isWon = true;
                }
                else if (energy <= 0 && !isWon) {
                    // if energy is drained, gameover
                    isOver = true;
                }
            }
            prevX = event.getX();
            prevY = event.getY();
            Log.d("TOUCHED", "Prev assigned!! prevX: " + prevX + " prevY: " + prevY);
            return true;
        }
        if (action == MotionEvent.ACTION_MOVE) {
            Log.d("TOUCHED", "Scrool. X: " + x + " Y: " + y + "PrevX:" + prevX);
            // get movement
            shiftX = (prevX - x)/10;
            shiftY = (prevY - y)/10;

            Log.d("TOUCHED", "Shift: " + shiftX);
            // let's focus on x displacement
            // displace y coordinates
            // displace x coordinates
            catX -= (int) shiftX;
            catY -= (int) shiftY;
            rectCat.set(catX,catY, catX+dWidth/4, catY+dWidth*2363/(4*1796));
            for (Path p : paths) {
                p.pathX -= (int) shiftX;
                p.pathY -= (int) shiftY;
                p.rectPath.set(p.pathX, p.pathY, p.pathX+dWidth/4, p.pathY+dWidth*235/1568);
            }
            for (Paw pa : paws) {
                pa.pawX -= (int) shiftX;
                pa.pawY -= (int) shiftY;
                pa.rectPaw.set(pa.pawX, pa.pawY, pa.pawX+dWidth/10, pa.pawY+dWidth*1235/(10*1373));
            }
        }
        return true;
    }
}


