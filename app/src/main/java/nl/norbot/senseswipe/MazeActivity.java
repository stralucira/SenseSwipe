package nl.norbot.senseswipe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MazeActivity extends AppCompatActivity implements GestureDetector.OnGestureListener{
    private static final String TAG = MazeActivity.class.getSimpleName();
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Log.d(TAG, Integer.toString(intent.getIntExtra("gesture_id", 0)));
                int direction = intent.getIntExtra("gesture_id", 0);

                if(usefingerprintgestures) {
                    if (direction == 2) {
                        moveleft(findViewById(R.id.maze_imageviewer));
                    }
                    if (direction == 8) {
                        movedown(findViewById(R.id.maze_imageviewer));
                    }
                    if (direction == 1) {
                        moveright(findViewById(R.id.maze_imageviewer));
                    }
                    if (direction == 4) {
                        moveup(findViewById(R.id.maze_imageviewer));
                    }
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

    private boolean usefingerprintgestures = false;
    private boolean usescreengestures = true;

    private Vibrator v;
    int vibrationlength = 200;

    private GestureDetectorCompat mDetector;
    private int currentmaze = -1;

    private AlertDialog.Builder alertbuilder;

    private long Mazestarttime;
    private FirebaseDatabase database;
    private DatabaseReference databasereference;

    private int numberOfErrors = 0;
    private int id;

    private Drawable finishimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maze);
        Log.d(TAG, "Maze Activity created.");

        id = getIntent().getIntExtra("id", 0);
        usefingerprintgestures = getIntent().getBooleanExtra("useFingerprint", false);
        usescreengestures = !usefingerprintgestures;

        ImageView screen = findViewById(R.id.maze_imageviewer);
        Log.d("Screenwidth", String.valueOf(screen.getWidth()));

        Rect vierkantje = new Rect();
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mDetector = new GestureDetectorCompat(this,this);

        alertbuilder = new AlertDialog.Builder(this);

        if(usefingerprintgestures) alertbuilder.setMessage("Swipe the fingerprint sensor to move the dot to the finish as fast as possible. The first maze counts as a practice area.");
        else alertbuilder.setMessage("Swipe the screen to move the dot to the finish as fast as possible. The first maze counts as a practice area.");
        alertbuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                startmaze(-1);
            }
        });

        AlertDialog alert11 = alertbuilder.create();
        alert11.show();

        Resources resourcse;
        int resourceid = getResources().getIdentifier("@android:drawable/star_big_on", "drawable", this.getPackageName());

        finishimage = getResources().getDrawable(resourceid);

        database = FirebaseDatabase.getInstance();
        databasereference = database.getReference();

        new CountDownTimer(3600000, 16) {

            public void onTick(long millisUntilFinished) {
                draw((ImageView) findViewById(R.id.maze_imageviewer));
            }

            public void onFinish() {

            }
        }.start();

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
        int finishimagesize = 120;
        paint.setColor(Color.rgb(0,255,0));
        Point finishpixelpos = convertToPixelCoordinates(finishpos, screenWidth, screenHeight);

        Rect imageBounds = new Rect(finishpixelpos.x - (finishimagesize / 2), finishpixelpos.y - (finishimagesize / 2), finishpixelpos.x + (finishimagesize / 2), finishpixelpos.y + (finishimagesize / 2));
        //imageBounds = new Rect(0, 0, 50, 50);
        Rect imageBoundscanvas = canvas.getClipBounds();

        Log.d("MW", "imagebounds: " + imageBounds);
        Log.d("MW", "imagesboundscanvas: " + imageBoundscanvas);


        finishimage.setBounds(imageBounds);
        finishimage.draw(canvas);
        //canvas.drawCircle(finishpixelpos.x, finishpixelpos.y, 50, paint);
        paint.setColor(Color.rgb(255,0,0));


        long currenttime = System.currentTimeMillis();
        float timediff = (currenttime - Mazestarttime) / 1000.f;

        paint.setTextSize(100);
        canvas.drawText("" + timediff, 750, 120, paint);


        view.invalidate();

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
        /*if(point.x == finishpos.x && point.y == finishpos.y){
            Log.d("MW","This point is the same");
        }*/
        return (point.x == finishpos.x && point.y == finishpos.y);
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
            numberOfErrors++;

        }

        if (isFinish(currentPosition)) {
            dearProgramWouldYouPleaseSubmitTheResultsOfTheCurrentMazeToTheDatabaseOkThanks();
            Log.d("MW", "FInished!");
            currentmaze++;
            startmaze(currentmaze);
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
            numberOfErrors++;
        }
        if (isFinish(currentPosition)) {
            dearProgramWouldYouPleaseSubmitTheResultsOfTheCurrentMazeToTheDatabaseOkThanks();
            Log.d("MW", "FInished!");
            currentmaze++;
            startmaze(currentmaze);
        }

    }

    public void dearProgramWouldYouPleaseSubmitTheResultsOfTheCurrentMazeToTheDatabaseOkThanks(){
        long currenttime = System.currentTimeMillis();
        float timediff = currenttime - Mazestarttime;

        Log.d("MW", "Maze completed in " + timediff);

        String inputmethod;
        if(usefingerprintgestures){
            inputmethod = "fingerprint";
        }
        else{
            inputmethod = "screen";
        }

        DatabaseReference mazeposition = databasereference.child(Integer.toString(id)).child(inputmethod).child("Maze").child(Integer.toString(currentmaze));
        mazeposition.child("completionTime").setValue(Float.toString(timediff));
        mazeposition.child("errorRate").setValue(Integer.toString(numberOfErrors));
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
            numberOfErrors++;

        }
        if (isFinish(currentPosition)) {
            Log.d("MW", "FInished!");
            dearProgramWouldYouPleaseSubmitTheResultsOfTheCurrentMazeToTheDatabaseOkThanks();
            currentmaze++;
            startmaze(currentmaze);
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
            numberOfErrors++;

        }
        if (isFinish(currentPosition)) {
            dearProgramWouldYouPleaseSubmitTheResultsOfTheCurrentMazeToTheDatabaseOkThanks();
            Log.d("MW", "FInished!");
            currentmaze++;
            startmaze(currentmaze);
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

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (this.mDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onShowPress(MotionEvent event) {
        //Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
        //return false;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d("MW", "tap up gesture received");
        //startmaze(0);
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d("MW", "Fling gesture received");
        if (usescreengestures) {
            if (Math.abs(velocityX) > Math.abs(velocityY)) {
                //Move along X-axis
                if (velocityX > 0) {
                    moveright(findViewById(R.id.maze_imageviewer));
                } else {
                    moveleft(findViewById(R.id.maze_imageviewer));
                }
            } else {
                //Move along Y-axis
                if (velocityY > 0) {
                    movedown(findViewById(R.id.maze_imageviewer));
                } else {
                    moveup(findViewById(R.id.maze_imageviewer));
                }
            }
        }
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    public boolean startmaze(int num){
        Log.d("MW", "Timer started");
        Mazestarttime = System.currentTimeMillis();
        numberOfErrors = 0;

        walls = getmaze(num);
        currentPosition = startpos;

        draw((ImageView)findViewById(R.id.maze_imageviewer));
        return true;
    }

    ArrayList<Point> getmaze(int num) {
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

        if(num == -1){
            //Practice maze
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

        }

        else if (num == 0) {
            //Maze vertical line. move down
            //Tests difference between up/down/left/right (1/4)

            for (int i = 1; i < 7; i++) {
                walls.add(new Point(1, i));
                walls.add(new Point(2, i));

                walls.add(new Point(4, i));
                walls.add(new Point(5, i));
                walls.add(new Point(6, i));
            }

            startpos = new Point(3, 1);
            finishpos = new Point(3, 6);

            return walls;
        } else if(num == 1) {
            //Horizontal line. move right.
            //Tests difference between up/down/left/right (2/4)

            for (int i = 1; i < 7; i++) {
                for (int j = 1; j < 7; j++) {
                    if (j != 3) {
                        //Log.d("MW", "Adding wall: " + i + ", " + j);

                        walls.add(new Point(i, j));
                    }
                }
            }

            startpos = new Point(1, 3);
            finishpos = new Point(6, 3);
        }
        else if (num == 2) {
                //Maze vertical line. move up
                //Tests difference between up/down/left/right (3/4)

                for (int i = 1; i < 7; i++) {
                    walls.add(new Point(1, i));
                    walls.add(new Point(2, i));

                    walls.add(new Point(4, i));
                    walls.add(new Point(5, i));
                    walls.add(new Point(6, i));
                }

                finishpos = new Point(3, 1);
                startpos = new Point(3, 6);

        }

        else if(num == 3) {
            //Horizontal line. move left.
            //Tests difference between up/down/left/right (4/4)

            for (int i = 1; i < 7; i++) {
                for (int j = 1; j < 7; j++) {
                    if (j != 3) {
                        walls.add(new Point(i, j));
                    }
                }
            }

            finishpos = new Point(1, 3);
            startpos = new Point(6, 3);

        } else if(num == 4) {
            //Complicated maze 1
            //Tests performance in 'random' maze
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

        }
        else if(num == 5) {
            //Easy long maze to test speed on repeated gestures
            for (int i = 2; i < 6; i++) {
                for (int j = 2; j < 6; j++) {
                    walls.add(new Point(i, j));
                }
            }
            walls.add(new Point(2, 1));

            startpos = new Point(1, 1);
            finishpos = new Point(3, 1);

        }
        else if(num == 6){
            int offset = 0;
            for (int i = 1; i < 7; i++) {
                for (int j = 1; j < 7; j++) {
                    if(j < offset || j > offset + 1) walls.add(new Point(i, j));
                }
                offset++;
            }

            startpos = new Point(1,1);
            finishpos = new Point(6,6);
        }
        else if(num == 6){
            int offset = 0;
            for (int i = 1; i < 7; i++) {
                for (int j = 1; j < 7; j++) {
                    if(j < offset || j > offset + 1) walls.add(new Point(i, j));
                }
                offset++;
            }

            finishpos = new Point(1,1);
            startpos = new Point(6,6);
        }
        else{
            /*currentmaze = 0;
            usefingerprintgestures = true;
            usescreengestures = false;
            walls = getmaze(0);

            alertbuilder.setMessage("Now, swipe the fingerprint scanner to move the dot to the finish.");
            alertbuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            AlertDialog alert11 = alertbuilder.create();
            alert11.show();
            */
            alertbuilder.setMessage("Finished all mazes");
            alertbuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    Intent intent = new Intent(getBaseContext(), DDRActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("useFingerprint", usefingerprintgestures);
                    startActivity(intent);
                    finish();
                }
            });

            AlertDialog alert11 = alertbuilder.create();
            alert11.show();


        }
        return walls;
    }


}
