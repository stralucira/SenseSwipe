package nl.norbot.senseswipe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startMazeActivity(View view) {
        Intent intent = new Intent(this, MazeActivity.class);
        startActivity(intent);
    }

    public void startCameraActivity(View view) {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    public void startTypingActivity(View view) {
        Intent intent = new Intent(this, TypingActivity.class);
        startActivity(intent);
    }
}
