package de.danoeh.antennapod;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.StrictMode;
import android.preference.PreferenceManager;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.MaterialModule;

import de.danoeh.antennapod.core.ApCoreEventBusIndex;
import de.danoeh.antennapod.core.ClientConfig;
import de.danoeh.antennapod.core.alarm.deskclock.LogUtils;
import de.danoeh.antennapod.core.alarm.deskclock.Utils;
import de.danoeh.antennapod.core.alarm.deskclock.controller.Controller;
import de.danoeh.antennapod.core.alarm.deskclock.data.DataModel;
import de.danoeh.antennapod.core.alarm.deskclock.events.LogEventTracker;
import de.danoeh.antennapod.core.alarm.deskclock.uidata.UiDataModel;
import de.danoeh.antennapod.spa.SPAUtil;
import org.greenrobot.eventbus.EventBus;

/** Main application class. */
public class PodcastApp extends Application {

    // make sure that ClientConfigurator executes its static code
    static {
        try {
            Class.forName("de.danoeh.antennapod.config.ClientConfigurator");
        } catch (Exception e) {
            throw new RuntimeException("ClientConfigurator not found", e);
        }
    }

    private static PodcastApp singleton;

    public static PodcastApp getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler(new CrashReportWriter());

        if (BuildConfig.DEBUG) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .penaltyLog()
                    .penaltyDropBox()
                    .detectActivityLeaks()
                    .detectLeakedClosableObjects()
                    .detectLeakedRegistrationObjects();
            StrictMode.setVmPolicy(builder.build());
        }

        singleton = this;

        ClientConfig.initialize(this);

        Iconify.with(new FontAwesomeModule());
        Iconify.with(new MaterialModule());

        SPAUtil.sendSPAppsQueryFeedsIntent(this);
        EventBus.builder()
                .addIndex(new ApEventBusIndex())
                .addIndex(new ApCoreEventBusIndex())
                .logNoSubscriberMessages(false)
                .sendNoSubscriberEvent(false)
                .installDefaultEventBus();

        final SharedPreferences prefs = getDefaultSharedPreferences(getApplicationContext());
        DataModel.getDataModel().init(getApplicationContext(), prefs);
        UiDataModel.getUiDataModel().init(getApplicationContext(), prefs);
        Controller.getController().setContext(getApplicationContext());
        Controller.getController().addEventTracker(new LogEventTracker(getApplicationContext()));
    }

    /**
     * Returns the default {@link SharedPreferences} instance from the underlying storage context.
     */
    @TargetApi(Build.VERSION_CODES.N)
    private static SharedPreferences getDefaultSharedPreferences(Context context) {
        final Context storageContext;
        if (Utils.isNOrLater()) {
            // All N devices have split storage areas. Migrate the existing preferences into the new
            // device encrypted storage area if that has not yet occurred.
            final String name = PreferenceManager.getDefaultSharedPreferencesName(context);
            storageContext = context.createDeviceProtectedStorageContext();
            if (!storageContext.moveSharedPreferencesFrom(context, name)) {
                LogUtils.wtf("Failed to migrate shared preferences");
            }
        } else {
            storageContext = context;
        }
        return PreferenceManager.getDefaultSharedPreferences(storageContext);
    }
}
