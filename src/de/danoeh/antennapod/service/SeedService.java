package de.danoeh.antennapod.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import de.danoeh.antennapod.AppConfig;
import de.danoeh.antennapod.bittorrent.*;
import de.danoeh.antennapod.feed.BitTorrentFeedMedia;
import de.danoeh.antennapod.feed.EnclosedFeedMedia;
import de.danoeh.antennapod.feed.FeedItem;
import de.danoeh.antennapod.service.download.BitloveUtils;
import de.danoeh.antennapod.service.download.BittorrentSessionProvider;
import de.danoeh.antennapod.service.playback.PlaybackService;
import de.danoeh.antennapod.storage.DBReader;
import de.danoeh.antennapod.util.NetworkUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds episodes that were downloaded over bittorrent or with bitlove.
 * This service is only active if the user has checked the preference to seed while the device is inactive.
 * <p/>
 * Furthermore, this service is only active while the device is charging and is connected to a WiFi network.
 */
public class SeedService extends Service {
    private static final String TAG = "SeedService";

    private static volatile boolean running = false;

    private Thread seedThread = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static boolean isRunning() {
        return running;
    }

    /**
     * Check if this service is still allowed to run.
     */
    private boolean checkRunningConditions() {
        boolean torrentLibAvailable = Session.isTorrentLibAvailable();
        boolean connectedToWifi = NetworkUtils.isConnectedToWifi(this);
        boolean screenOff = !((PowerManager) getSystemService(Context.POWER_SERVICE)).isScreenOn();
        boolean result = torrentLibAvailable && connectedToWifi && screenOff;
        if (!result) {
            if (AppConfig.DEBUG) Log.d(TAG, "Running conditions: " + torrentLibAvailable + "," + connectedToWifi + "," + screenOff);
        }
        return result;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!checkRunningConditions()) {
            if (AppConfig.DEBUG) Log.d(TAG, "Running conditions no longer satisfied, stopping service");
        }
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        seedThread = new Thread(new SeedWorker());
        seedThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (AppConfig.DEBUG) Log.d(TAG, "Service is about to be destroyed");
        unregisterReceiver(batteryReceiver);
        if (seedThread != null && !seedThread.isInterrupted()) {
            seedThread.interrupt();
        }
    }

    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (StringUtils.equals(intent.getAction(), Intent.ACTION_BATTERY_CHANGED)) {
                int batteryStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_DISCHARGING);

                if (batteryStatus != BatteryManager.BATTERY_STATUS_CHARGING
                        && batteryStatus != BatteryManager.BATTERY_STATUS_FULL) {
                    stopSelf();
                }
            }
        }
    };

    private class SeedWorker implements Runnable {
        private static final String TAG = "SeedWorker";
        private static final long WAITING_INTERVAL_MS = 500;


        @Override
        public void run() {
            if (AppConfig.DEBUG) Log.d(TAG, "Started");

            Session session = null;
            List<TorrentHandle> seededTorrents = new ArrayList<TorrentHandle>();
            try {
                BittorrentSessionProvider.incrementRefCount();
                session = BittorrentSessionProvider.getSession();
                List<FeedItem> torrentItems = DBReader.getDownloadedBittorrentFeedItems(SeedService.this);

                // add torrent handles to session

                for (FeedItem item : torrentItems) {
                    String savePath = new File(item.getMedia().getLocalMediaUrl()).getParent();
                    String torrentUrl;
                    if (item.getMedia() instanceof BitTorrentFeedMedia) {
                        torrentUrl = (item.getMedia()).getDownload_url();
                    } else if (item.getMedia() instanceof EnclosedFeedMedia) {
                        torrentUrl = BitloveUtils.isAvailableOnBitlove(SeedService.this, (EnclosedFeedMedia) item.getMedia());
                    } else {
                        torrentUrl = null;
                    }

                    if (torrentUrl != null) {
                        TorrentHandle handle = session.addTorrent(savePath, torrentUrl, Session.ADD_TORRENT_SEED_MODE);
                        if (handle != null) {
                            seededTorrents.add(handle);
                        }
                    }
                }

                if (AppConfig.DEBUG) Log.d(TAG, "Seeding " + seededTorrents.size() + " torrents");
                while (!Thread.interrupted() && checkRunningConditions()) {
                    Thread.sleep(WAITING_INTERVAL_MS);
                    Alert alert = session.popAlert();
                    while (alert != null) {
                        if (AppConfig.DEBUG) Log.d(TAG, alert.toString());
                        alert = session.popAlert();
                    }
                }

            } catch (LibtorrentException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (session != null) {
                    session.shutdown();
                }
            }

            SeedService.this.stopSelf();
            if (AppConfig.DEBUG) Log.d(TAG, "Finished");
        }
    }
}
