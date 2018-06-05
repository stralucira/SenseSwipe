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
import android.os.CountDownTimer;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class DDR_scrollingActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
    FirebaseDatabase database;
    DatabaseReference databasereference;

    private int id;
    private boolean usefingerprintgestures;
    private boolean usescreengestures;

    private Canvas canvas;
    private Paint paint = new Paint();
    private Bitmap bitmap;

    private AlertDialog.Builder alertbuilder;

    int arrowsize = 200;
    Point goalposition = new Point(500,500);
    Point arrowstartposition = new Point(500,500);

    private int screenWidth, screenHeight;

    int currentarrowdirection;
    int currentarrowposition;
    int currentarrowdistance;

    private Drawable arrowimage;

    private Drawable arrowup;
    private Drawable arrowdown;
    private Drawable arrowleft;
    private Drawable arrowright;

    private GestureDetectorCompat mDetector;


    int speed = 10;


    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Log.d(TAG, Integer.toString(intent.getIntExtra("gesture_id", 0)));

                // TODO: Implement input handling here.
                // For direction codes see https://developer.android.com/reference/android/accessibilityservice/FingerprintGestureController#FINGERPRINT_GESTURE_SWIPE_DOWN
            } catch (Exception e){
                Log.d(TAG, e.toString());
            }
        }
    };

    Integer subjectNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ddr_scrolling);

        id = getIntent().getIntExtra("id", 0);
        usefingerprintgestures = getIntent().getBooleanExtra("useFingerprint", false);
        usescreengestures = !usefingerprintgestures;

        database = FirebaseDatabase.getInstance();
        databasereference = database.getReference();

        alertbuilder = new AlertDialog.Builder(this);


        int resourceid = getResources().getIdentifier("graphic_arrow_down", "drawable", this.getPackageName());

        arrowimage = getResources().getDrawable(resourceid);
        arrowdown = getResources().getDrawable(resourceid);

        resourceid = getResources().getIdentifier("graphic_arrow_up", "drawable", this.getPackageName());

        arrowup = getResources().getDrawable(resourceid);

        resourceid = getResources().getIdentifier("graphic_arrow_left", "drawable", this.getPackageName());

        arrowleft = getResources().getDrawable(resourceid);

        resourceid = getResources().getIdentifier("graphic_arrow_right", "drawable", this.getPackageName());

        arrowright = getResources().getDrawable(resourceid);


        if(usefingerprintgestures) alertbuilder.setMessage("Message fp.");
        else alertbuilder.setMessage("Message screen.");
        alertbuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();

                View imageview = findViewById(R.id.DDR_imageview);

                new CountDownTimer(3600000, 10) {

                    public void onTick(long millisUntilFinished) {
                        draw((ImageView) findViewById(R.id.DDR_imageview));
                    }

                    public void onFinish() {

                    }
                }.start();

            }
        });

        AlertDialog alert11 = alertbuilder.create();
        alert11.show();


        mDetector = new GestureDetectorCompat(this,this);


    }

    protected void onResume() {
        super.onResume();

        registerReceiver(receiver, new IntentFilter("FINGERPRINT_GESTURE_DETECTED"));
    }

    protected void onPause() {
        super.onPause();

        Log.d(TAG, "Unregistering receiver.");
        unregisterReceiver(receiver);
    }

    public void draw(ImageView view) {
        update();

        if(screenWidth == 0) {
            screenWidth = view.getWidth();
            screenHeight = view.getHeight();

            goalposition = new Point((screenWidth / 2) - (arrowsize / 2), (int) (screenHeight * 0.66));
            arrowstartposition = new Point((screenWidth / 2) - (arrowsize / 2), (int) (screenHeight * 0.1));

            if (screenHeight == 0 || screenWidth == 0) return;


            bitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);

            view.setImageBitmap(bitmap);
            canvas = new Canvas(bitmap);
        }

        //Draw the background
        canvas.drawColor(Color.rgb(90, 90, 90));

        //Draw the goal
        Rect goal = new Rect(goalposition.x, goalposition.y, goalposition.x + arrowsize, goalposition.y + arrowsize);
        canvas.drawRect(goal, paint);

        //Draw the arrow
        paint.setColor(Color.rgb(0,255,0));
        Point finishpixelpos = new Point(200, 200);

        Rect imageBounds = new Rect(goalposition.x, arrowstartposition.y + currentarrowposition, goalposition.x + arrowsize, arrowstartposition.y + arrowsize + currentarrowposition);
        //imageBounds = new Rect(0, 0, 50, 50);
        Rect imageBoundscanvas = canvas.getClipBounds();

        Log.d("MW", "imagebounds: " + imageBounds);
        Log.d("MW", "imagesboundscanvas: " + imageBoundscanvas);


        arrowimage.setBounds(imageBounds);
        arrowimage.draw(canvas);

        currentarrowdistance = arrowstartposition.y + currentarrowposition - goalposition.y;

        if(arrowstartposition.y + currentarrowposition > screenHeight){
            startnewarrow();
        }


        paint.setTextSize(100);
        canvas.drawText("" + currentarrowdistance, 750, 120, paint);


        view.invalidate();

    }

    private void update(){
        currentarrowposition += speed;
    }

    private void startnewarrow(){
        //TODO: log result here
        Random random = new Random();
        int direction = random.nextInt(4);

        currentarrowdirection = direction;

        if(direction == 0){
            arrowimage = arrowup;
        }
        if(direction == 1){
            arrowimage = arrowdown;
        }
        if(direction == 2){
            arrowimage = arrowleft;
        }
        if(direction == 3){
            arrowimage = arrowright;
        }

        currentarrowposition = 0;
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
            startnewarrow();
        }
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

}
