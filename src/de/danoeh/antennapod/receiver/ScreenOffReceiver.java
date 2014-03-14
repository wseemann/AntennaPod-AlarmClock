package de.danoeh.antennapod.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import de.danoeh.antennapod.AppConfig;
import de.danoeh.antennapod.preferences.UserPreferences;
import de.danoeh.antennapod.service.SeedService;
import org.apache.commons.lang3.StringUtils;
import org.shredzone.flattr4j.model.User;

/**
 * Called when the screen is turned off.
 */
public class ScreenOffReceiver extends BroadcastReceiver{
    public static final String TAG = "ScreenOffReceiver";

    private static final long INACTIVITY_TRACKER_TIMEOUT_MS = 1000 * 10;
    private static Thread inactivityTrackerThread = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!UserPreferences.isSeedServiceEnabled()) {
            return;
        }

        final Context appContext = context.getApplicationContext();
        if (appContext == null) {
            Log.w(TAG, "Unable to get app context");
            return;
        }

        if (StringUtils.equals(intent.getAction(), Intent.ACTION_SCREEN_OFF)) {
            if (!SeedService.isRunning()
                    && (inactivityTrackerThread == null || inactivityTrackerThread.isInterrupted())) {
                inactivityTrackerThread = new Thread() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(INACTIVITY_TRACKER_TIMEOUT_MS);
                            appContext.startService(new Intent(appContext, SeedService.class));
                        } catch (InterruptedException e) {
                            if (AppConfig.DEBUG) Log.d(TAG, "Inactivity tracker interrupted");
                        }
                    }
                };

                if (AppConfig.DEBUG) Log.d(TAG, "Starting inactivity tracker");
                inactivityTrackerThread.start();
            }

        } else if (StringUtils.equals(intent.getAction(), Intent.ACTION_SCREEN_ON)) {
            if (inactivityTrackerThread != null) {
                inactivityTrackerThread.interrupt();
                inactivityTrackerThread = null;
            }
        }
    }


}
