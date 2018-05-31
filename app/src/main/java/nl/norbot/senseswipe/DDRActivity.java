package nl.norbot.senseswipe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class DDRActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                int swipe = intent.getIntExtra("gesture_id", 0);

                // TODO: Implement input handling here.
                // For direction codes see https://developer.android.com/reference/android/accessibilityservice/FingerprintGestureController#FINGERPRINT_GESTURE_SWIPE_DOWN
                Log.d(TAG, "Swipe value: " + Integer.toString(swipe));
                switch(swipe){

                    case 1:
                        // RIGHT
                        if (currentArrow.equals( "RIGHT"))
                        {
                            Log.d(TAG, "Direction correct: RIGHT");
                            showNextArrow();
                        }
                        break;
                    case 2:
                        // LEFT
                        if (currentArrow.equals( "LEFT"))
                        {
                            Log.d(TAG, "Direction correct: LEFT");
                            showNextArrow();
                        }
                        break;
                    case 4:
                        // UP
                        if (currentArrow.equals( "UP"))
                        {
                            Log.d(TAG, "Direction correct: UP");
                            showNextArrow();
                        }
                        break;
                    case 8:
                        // DOWN
                        if (currentArrow.equals( "DOWN"))
                        {
                            Log.d(TAG, "Direction correct: DOWN");
                            showNextArrow();
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e){
                Log.d(TAG, e.toString());
            }
        }
    };

    private ImageView arrowUp, arrowDown, arrowLeft, arrowRight;
    private List<DDRSequenceItem> sequence;
    private int arrowIndex = 0;
    private String currentArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ddr);
        arrowUp = findViewById(R.id.arrow_up);
        arrowDown = findViewById(R.id.arrow_down);
        arrowLeft = findViewById(R.id.arrow_left);
        arrowRight = findViewById(R.id.arrow_right);
        hideArrows();

        sequence = setSequence();

        /*Handler handler = new Handler(Looper.getMainLooper());
        for (int i = 0; i < sequence.size(); i++)
        {
            arrowIndex = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Log.d(TAG, "WOOOO");
                    showArrow(sequence.get(arrowIndex).direction);
                }
            }, (long)sequence.get(i).startDelay);
        }*/
        showArrow(sequence.get(0).direction);
        currentArrow = sequence.get(0).direction;

    }

    private void showNextArrow()
    {
        hideArrows();
        String direction = sequence.get(arrowIndex).direction;
        showArrow(direction);
        currentArrow = direction;
        Log.d(TAG, "Next arrow: " + direction);
        arrowIndex++;
    }

    private void showArrow(String direction)
    {
        Log.d(TAG, "Showing arrow " + direction);
        switch (direction)
        {
            case "UP":
                arrowUp.setVisibility(View.VISIBLE);
                break;
            case "DOWN":
                arrowDown.setVisibility(View.VISIBLE);
                break;
            case "LEFT":
                arrowLeft.setVisibility(View.VISIBLE);
                break;
            case "RIGHT":
                arrowRight.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private void hideArrows() {
        arrowUp.setVisibility(View.INVISIBLE);
        arrowDown.setVisibility(View.INVISIBLE);
        arrowLeft.setVisibility(View.INVISIBLE);
        arrowRight.setVisibility(View.INVISIBLE);
    }

    private List<DDRSequenceItem> setSequence() {
        List<DDRSequenceItem> list = new ArrayList<DDRSequenceItem>();

        list.add(new DDRSequenceItem(0, "UP"));
        list.add(new DDRSequenceItem(2000, "RIGHT"));
        list.add(new DDRSequenceItem(2000, "DOWN"));
        list.add(new DDRSequenceItem(2000, "LEFT"));

        return list;
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(receiver, new IntentFilter("FINGERPRINT_GESTURE_DETECTED"));
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "Unregistering receiver.");
        unregisterReceiver(receiver);

    }
}




