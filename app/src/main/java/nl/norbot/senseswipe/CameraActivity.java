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
import com.wonderkiln.camerakit.CameraKitController;
import com.wonderkiln.camerakit.CameraView;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = CameraActivity.class.getSimpleName();
    private float zoom = 1.0f;
    private float maxZoom = 10.0f;
    private float minZoom = 1.0f;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                int swipe = intent.getIntExtra("gesture_id", 0);
                Log.d(TAG, Integer.toString(swipe));

                // TODO: Implement input handling here.
                // For direction codes see https://developer.android.com/reference/android/accessibilityservice/FingerprintGestureController#FINGERPRINT_GESTURE_SWIPE_DOWN
                switch (swipe)
                {
                    case (1):
                        Log.d(TAG, "Swipe right.");
                        break;
                    case (2):
                        Log.d(TAG, "Swipe left.");
                        break;
                    case (4):
                        // Max zoom (Dimas' phone) front: 4.0, back: 8.0
                        // Log.d(TAG, "Swipe up.");
                        zoom += .5f;
                        zoom = Math.max(minZoom, Math.min(maxZoom, zoom));
                        cameraView.setZoom(zoom);
                        Log.d(TAG, "Zoom: " + zoom);
                        break;
                    case (8):
                        // Log.d(TAG, "Swipe down.");
                        zoom -= 0.5f;
                        zoom = Math.max(minZoom, Math.min(maxZoom, zoom));
                        cameraView.setZoom(zoom);
                        Log.d(TAG, "Zoom: " + zoom);
                        break;
                    default:
                        break;
                }

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
        //cameraView.setZoom();

    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();

        Log.d(TAG, "Unregistering receiver.");
        unregisterReceiver(receiver);

    }
}
