package nl.norbot.senseswipe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class TypingActivity extends AppCompatActivity {
    private static final String TAG = TypingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_typing);
        Log.d(TAG, "Typing Actiity created.");
    }
}
