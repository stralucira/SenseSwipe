package nl.norbot.senseswipe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.view.inputmethod.*;

public class TypingActivity extends AppCompatActivity {

    EditText editText;
    int textLength;
    int cursorOffsetFromEnd = 0;

    private static final String TAG = TypingActivity.class.getSimpleName();

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Log.d(TAG, Integer.toString(intent.getIntExtra("gesture_id", 0)));

                // TODO: Implement input handling here.
                // For direction codes see https://developer.android.com/reference/android/accessibilityservice/FingerprintGestureController#FINGERPRINT_GESTURE_SWIPE_DOWN
                int direction = intent.getIntExtra("gesture_id", 0);

                if(direction == 2){
                    cursorOffsetFromEnd++;
                    moveCursor();
                }
                if(direction == 1){
                    if(cursorOffsetFromEnd>0)
                        cursorOffsetFromEnd--;
                    moveCursor();
                }

            } catch (Exception e){
                Log.d(TAG, e.toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_typing);
        Log.d(TAG, "Typing Activity created.");

        editText = findViewById(R.id.mistyped_word);
        textLength = editText.getText().length();
        editText.setSelection(textLength, textLength);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

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

    public void moveCursor(){
        editText.setSelection(textLength - cursorOffsetFromEnd);
    }
}
