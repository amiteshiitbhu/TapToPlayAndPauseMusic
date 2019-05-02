package com.visioapps.tappingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.SystemClock;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.visioapps.tappingapp.picidae.IntegratedTapDetector;
import com.visioapps.tappingapp.utils.SharedPreferencesUtils;


public class SideTapManager extends BroadcastReceiver implements
        IntegratedTapDetector.TapListener {

    private static final long MIN_TIME_BETWEEN_TOUCH_AND_TAP_NANOS = 500 * 1000 * 1000;
    private static final long MIN_TIME_BETWEEN_HAPTIC_AND_TAP_NANOS = 500 * 1000 * 1000;
    private static final long DOUBLE_TAP_SPACING_NANOS = 500 * 1000 * 1000;
    private static final long MILIS_PER_NANO = 1000 * 1000;
    private static final int MASK_EVENTS_HANDLED_BY_SIDE_TAP_MANAGER = AccessibilityEventCompat.TYPE_TOUCH_INTERACTION_START;
    private Context mContext;
    private long mLastTouchTime = 0;
    private long mLastHapticTime = 0;
    private IntegratedTapDetector mIntegratedTapDetector;

    public SideTapManager(Context context) {
        mContext = context;
        mIntegratedTapDetector = new IntegratedTapDetector((SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE));
        mIntegratedTapDetector.addListener(this);
        mIntegratedTapDetector.setPostDelayTimeMillis(MIN_TIME_BETWEEN_TOUCH_AND_TAP_NANOS / MILIS_PER_NANO);
        onReloadPreferences();
        mLastTouchTime = System.nanoTime();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null) {
            return;
        }
//        onReloadPreferences();
//        String action = intent.getAction();
//        if (action.equals(Intent.ACTION_SCREEN_ON)) {
//        onReloadPreferences();
//        }
//        if (action.equals(Intent.ACTION_SCREEN_OFF)) {
//            mIntegratedTapDetector.stop();
//        }
    }

    private void onReloadPreferences() {
        boolean enableTapDetection = true;
//        mIntegratedTapDetector.setDoubleTapDetectionQuality(IntegratedTapDetector.TAP_QUALITY_LOW);
        mIntegratedTapDetector.setMaxDoubleTapSpacingNanos(DOUBLE_TAP_SPACING_NANOS);
        mIntegratedTapDetector.setDoubleTapDetectionQuality(IntegratedTapDetector.TAP_QUALITY_LOW);

        if (enableTapDetection) {
            mIntegratedTapDetector.start();
        } else {
            mIntegratedTapDetector.stop();
        }
    }

    /**
     * Stops tap detection.
     */
    public void onSuspendInfrastructure() {
        mIntegratedTapDetector.stop();
    }

    /* Handle a single tap on the side of the device */
    @Override
    public void onSingleTap(long timeStamp) {

//            mGestureController.performAction(prefs.getString(
//                    mContext.getString(R.string.pref_shortcut_single_tap_key),
//                    mContext.getString(R.string.pref_shortcut_single_tap_default)),
//                    eventId);
//        }
    }

    @Override
    public void onDoubleTap(long timestampNanos) {
        boolean tapIsntFromScreenTouch = (Math.abs(timestampNanos - mLastTouchTime) > MIN_TIME_BETWEEN_TOUCH_AND_TAP_NANOS);
        boolean tapIsntFromHaptic = (Math.abs(timestampNanos - mLastHapticTime) > MIN_TIME_BETWEEN_HAPTIC_AND_TAP_NANOS);
        if (mContext != null && tapIsntFromScreenTouch && tapIsntFromHaptic) {
            SharedPreferences prefs = SharedPreferencesUtils.getSharedPreferences(mContext);

            AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);


            if (audioManager != null) {
                if (audioManager.isMusicActive()) {
                    Toast.makeText(mContext, "Media paused", Toast.LENGTH_SHORT).show();
                }
                long eventtime = SystemClock.uptimeMillis();
                Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
                audioManager.dispatchMediaKeyEvent(downEvent);
                Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                KeyEvent upEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
                audioManager.dispatchMediaKeyEvent(upEvent);

            }
        }
    }

    public static IntentFilter getFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        return filter;
    }

    void setIntegratedTapDetector(IntegratedTapDetector itd) {
        mIntegratedTapDetector = itd;
    }
}
