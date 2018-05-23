package nl.norbot.senseswipe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class MazeActivity extends AppCompatActivity {
    private static final String TAG = MazeActivity.class.getSimpleName();
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Log.d(TAG, Integer.toString(intent.getIntExtra("gesture_id", 0)));
                int direction = intent.getIntExtra("gesture_id", 0);

                if(direction == 2){
                    moveleft(findViewById(R.id.maze_imageviewer));
                }
                if(direction == 8){
                    movedown(findViewById(R.id.maze_imageviewer));
                }
                if(direction == 1){
                    moveright(findViewById(R.id.maze_imageviewer));
                }
                if(direction == 4){
                    moveup(findViewById(R.id.maze_imageviewer));
                }

                Log.d("MW", "Current position: " + currentPosition.toString());
                // For direction codes see https://developer.android.com/reference/android/accessibilityservice/FingerprintGestureController#FINGERPRINT_GESTURE_SWIPE_DOWN
            } catch (Exception e){
                Log.d(TAG, e.toString());
            }
        }
    };
    boolean initialized = false;

    private Canvas canvas;
    private Paint paint = new Paint();
    private Bitmap bitmap;

    private Point currentPosition = new Point();
    private int gridsize = 8;
    private Point startpos;
    private Point finishpos;
    ArrayList<Point> walls = getmaze(0);

    private Vibrator v;
    int vibrationlength = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maze);
        Log.d(TAG, "Maze Activity created.");

        ImageView screen = findViewById(R.id.maze_imageviewer);
        Log.d("Screenwidth", String.valueOf(screen.getWidth()));

        Rect vierkantje = new Rect();
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //draw(screen);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        // Gets called when window changes focus
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            ImageView img = (ImageView) findViewById(R.id.maze_imageviewer);

            int screenwidth = img.getWidth();
            int screenheight = img.getHeight();

            if (initialized == false) {
                currentPosition = startpos;
            }
            draw(img);
            initialized = true;
        }
    }


    public void draw(ImageView view) {
        int screenWidth = view.getWidth();
        int screenHeight = view.getHeight();

        bitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);

        view.setImageBitmap(bitmap);
        canvas = new Canvas(bitmap);

        //Draw the background
        //canvas.drawColor(Color.RED);

        //Draw the walls
        paint.setColor(Color.rgb(90, 90, 90));
        for (Point wall : walls){
            Rect rectangle = new Rect(wall.x * (screenWidth / gridsize), wall.y * (screenHeight / gridsize), (wall.x + 1) * (screenWidth / gridsize), (wall.y + 1) * (screenHeight / gridsize));
            canvas.drawRect(rectangle, paint);
        }
        paint.setColor(Color.rgb(0, 0, 0));


        //Draw the playing field as a grid
        /*
        for (int i = 0; i < gridsize; i++)
        {
            for (int j = 0; j < gridsize; j++)
            {
                canvas.drawCircle(i * screenWidth / gridsize, j * screenHeight / gridsize, 5, paint);
            }
        }
        */

        //Draw the player
        paint.setColor(Color.rgb(0,0,100));
        Point playerpixelpos = convertToPixelCoordinates(currentPosition, screenWidth, screenHeight);
        canvas.drawCircle(playerpixelpos.x, playerpixelpos.y, 50, paint);
        paint.setColor(Color.rgb(0,0,0));

        //Draw the finish
        paint.setColor(Color.rgb(0,255,0));
        Point finishpixelpos = convertToPixelCoordinates(finishpos, screenWidth, screenHeight);
        canvas.drawCircle(finishpixelpos.x, finishpixelpos.y, 50, paint);
        paint.setColor(Color.rgb(0,0,0));

        view.invalidate();

    }

    ArrayList<Point> getmaze(int num) {
        if (num == 0) {
            int width = 8;
            int height = 8;

            ArrayList<Point> walls = new ArrayList<>();

            //Outer walls
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (i == 0 || i == width - 1 || j == 0 || j == height - 1) {
                        walls.add(new Point(i, j));
                    }
                }
            }

            //Maze walls
            walls.add(new Point(4, 1));
            walls.add(new Point(5, 1));
            walls.add(new Point(6, 1));

            walls.add(new Point(1, 2));
            walls.add(new Point(2, 2));

            walls.add(new Point(2, 3));
            walls.add(new Point(3, 3));
            walls.add(new Point(4, 3));
            walls.add(new Point(5, 3));

            walls.add(new Point(2, 4));

            walls.add(new Point(2, 5));
            walls.add(new Point(4, 5));
            walls.add(new Point(5, 5));
            walls.add(new Point(6, 5));

            startpos = new Point(1, 1);
            finishpos = new Point(1, 3);


            return walls;
        } else {
            //TODO: add more mazes.
        }
        return new ArrayList<Point>();
    }

    Point convertToPixelCoordinates(Point in, int screenwidth, int screenheight){
        return new Point(in.x * screenwidth / gridsize + (int)(0.5 * (screenwidth / gridsize)), in.y * screenheight / gridsize + (int)(0.5 * (screenheight / gridsize)));
    }

    private boolean isWall(Point point){

        if(walls.indexOf(point) >= 0){
            //Log.d("MW", "Current position is wall!");
            return true;

        }
        else{
            return false;
        }
    }

    private boolean isFinish(Point point){
        return (point == finishpos);
    }

    public void moveright(View view){
        Point newposition = new Point(currentPosition.x + 1, currentPosition.y);

        //Log.d("MW", "moveright");

        if(!isWall(newposition)){
            currentPosition = newposition;

            ImageView image = (ImageView) view;
            draw(image);
        }
        else{
            //Player hit a wall, vibrate
            v.vibrate(VibrationEffect.createOneShot(vibrationlength,VibrationEffect.DEFAULT_AMPLITUDE));
        }

        if (isFinish(currentPosition)) {
            //TODO: what needs to happen when the player finishes?

        }
    }

    public void moveleft(View view){
        Point newposition = new Point(currentPosition.x - 1, currentPosition.y);
        //Log.d("MW", "moveleft");

        if(!isWall(newposition)){
            currentPosition = newposition;

            ImageView image = (ImageView) view;
            draw(image);
        }
        else{
            //Player hit a wall, vibrate
            v.vibrate(VibrationEffect.createOneShot(vibrationlength,VibrationEffect.DEFAULT_AMPLITUDE));
        }
        if (isFinish(currentPosition)) {
            //TODO: what needs to happen when the player finishes?

        }

    }
    public void moveup(View view){
        Point newposition = new Point(currentPosition.x, currentPosition.y - 1);
        //Log.d("MW", "moveup");

        if(!isWall(newposition)){
            currentPosition = newposition;

            ImageView image = (ImageView) view;
            draw(image);
        }
        else{
            //Player hit a wall, vibrate
            v.vibrate(VibrationEffect.createOneShot(vibrationlength,VibrationEffect.DEFAULT_AMPLITUDE));
        }
        if (isFinish(currentPosition)) {
            //TODO: what needs to happen when the player finishes?

        }

    }
    public void movedown(View view){
        Point newposition = new Point(currentPosition.x, currentPosition.y + 1);
        //Log.d("MW", "movedown");

        if(!isWall(newposition)){
            currentPosition = newposition;

            ImageView image = (ImageView) view;
            draw(image);
        }
        else{
            //Player hit a wall, vibrate.
            v.vibrate(VibrationEffect.createOneShot(vibrationlength,VibrationEffect.DEFAULT_AMPLITUDE));
        }
        if (isFinish(currentPosition)) {
            //TODO: what needs to happen when the player finishes?

        }

    }
    //TODO: implement controls. Map input (screen & sensor) to moveup, movedown, moveright, moveleft

protected void onResume() {
        super.onResume();

        registerReceiver(receiver, new IntentFilter("FINGERPRINT_GESTURE_DETECTED"));
    }

    protected void onPause() {
        super.onPause();

        Log.d(TAG, "Unregistering receiver.");
        unregisterReceiver(receiver);
    }
}
