package nl.norbot.senseswipe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MazeActivity extends AppCompatActivity {
    private static final String TAG = MazeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maze);
        Log.d(TAG, "Maze Activity created.");
    }

}
