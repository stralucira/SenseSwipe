package nl.norbot.senseswipe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraView;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = CameraActivity.class.getSimpleName();
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

    CameraView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        cameraView = findViewById(R.id.camera);
        cameraView.setFacing(CameraKit.Constants.FACING_BACK);
        Log.d(TAG, "Camera Activity created.");
      
        /*
        // Taking a picture
        cameraView.captureImage(new CameraKitView.ImageCallback() {
            @Override
            public void onImage(CameraKitView view, byte[] jpeg) {
            }
        });
        */
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(receiver, new IntentFilter("FINGERPRINT_GESTURE_DETECTED"));
        cameraView.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();

        Log.d(TAG, "Unregistering receiver.");
        unregisterReceiver(receiver);

    }
}
