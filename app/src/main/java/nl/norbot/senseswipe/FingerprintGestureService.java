package nl.norbot.senseswipe;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.FingerprintGestureController;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class FingerprintGestureService extends AccessibilityService {
    private static final String TAG = FingerprintGestureService.class.getSimpleName();

    public FingerprintGestureService() {
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "onServiceConnected");
        FingerprintGestureController gestureController = getFingerprintGestureController();
        Log.d(TAG, "Is available: " + gestureController.isGestureDetectionAvailable() );

        FingerprintGestureController.FingerprintGestureCallback callback = new
                FingerprintGestureController.FingerprintGestureCallback() {
                    @Override
                    public void onGestureDetectionAvailabilityChanged(boolean available) {
                        super.onGestureDetectionAvailabilityChanged(available);
                        Log.d(TAG, "onGestureDetectionAvailabilityChanged " + available);
                    }

                    @Override
                    public void onGestureDetected(int gesture) {
                        super.onGestureDetected(gesture);
                        Log.d(TAG, "onGestureDetected " + gesture);
                    }
                };

        gestureController.registerFingerprintGestureCallback(callback, new Handler());
    }

    @Override
    public void onInterrupt() {
    }
}
