package de.danoeh.antennapod.service.download;

import android.util.Log;
import de.danoeh.antennapod.AppConfig;
import de.danoeh.antennapod.R;
import de.danoeh.antennapod.bittorrent.LibtorrentException;
import de.danoeh.antennapod.bittorrent.Session;
import de.danoeh.antennapod.bittorrent.TorrentHandle;
import de.danoeh.antennapod.bittorrent.TorrentStatus;
import de.danoeh.antennapod.util.DownloadError;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.concurrent.TimeoutException;

public class BitTorrentDownloader extends Downloader {
    private static final String TAG = "BittorrentDownloader";

    private Session session;

    private static final long HANDLE_VALID_TIMEOUT = 20000;
    private static final long HANDLE_VALID_CHECK_INTERVAL = 100;

    public BitTorrentDownloader(DownloadRequest request, Session session) {
        super(request);
        if (session == null) throw new IllegalArgumentException("session = null");
        this.session = session;
    }

    @Override
    protected void download() {
        if (AppConfig.DEBUG) Log.d(TAG, "Starting download");
        TorrentHandle handle = null;
        try {
            handle = session.addTorrent(new File(request.getDestination()).getParent(), request.getSource(), Session.ADD_TORRENT_AUTO_MANAGED);
            if (handle == null) {
                onFail(DownloadError.ERROR_BITTORRENT_ERROR, null);
                return;
            }

            waitUntilHandleIsValid(handle, HANDLE_VALID_TIMEOUT, HANDLE_VALID_CHECK_INTERVAL);
            TorrentStatus status = null;
            request.setStatusMsg(R.string.download_running);
            boolean finished = false;
            do {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                status = handle.getStatus();
                if (status == null) {
                    if (AppConfig.DEBUG) Log.d(TAG, "TorrentStatus was null");
                    onFail(DownloadError.ERROR_BITTORRENT_ERROR, null);
                    break;
                }
                if (AppConfig.DEBUG) Log.d(TAG, status.toString());
                request.setSize(status.totalWanted());
                request.setSoFar(status.totalWantedDone());
                request.setProgressPercent((int) (((double) status.totalWantedDone()) / ((double) status.totalWanted()) * 100.0));
                if (request.getProgressPercent() == 100) {
                    finished = true;
                }
            } while (!cancelled && !finished);
            if (cancelled) {
                onCancelled();
            } else {
                if (handle.getNumFiles() <= 0) {
                    if (AppConfig.DEBUG) Log.d(TAG, "TorrentHandle has not enough files: " + handle.getNumFiles());
                } else {
                    String dirPath = new File(request.getDestination()).getParent();
                    File firstFile = new File(dirPath, handle.getFileAt(0).getName());
                    if (AppConfig.DEBUG)
                        Log.d(TAG, "Setting destination of download request to: " + firstFile.getAbsolutePath());
                    request.setDestination(firstFile.getAbsolutePath());
                }
                onSuccess();
            }
        } catch (LibtorrentException e) {
            e.printStackTrace();
            onFail(DownloadError.ERROR_BITTORRENT_ERROR, e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
            if (handle != null) {
                session.removeTorrent(handle, cancelled);
            }
        }
    }

    @Override
    public int getTypeStringAsResource() {
        return R.string.downloader_type_bittorrent;
    }

    private void onSuccess() {
        if (AppConfig.DEBUG)
            Log.d(TAG, "Download was successful");
        result.setSuccessful();
    }

    private void onFail(DownloadError reason, String reasonDetailed) {
        if (AppConfig.DEBUG) {
            Log.d(TAG, "Download failed");
        }
        result.setFailed(reason, reasonDetailed);
    }

    private void onCancelled() {
        if (AppConfig.DEBUG)
            Log.d(TAG, "Download was cancelled");
        result.setCancelled();
    }

    private void waitUntilHandleIsValid(TorrentHandle handle, long timeoutMs, long checkIntervalMs) throws TimeoutException, InterruptedException {
        while (timeoutMs > 0) {
            if (handle.isValid()) {
                if (AppConfig.DEBUG) Log.d(TAG, "Handle is valid");
                return;
            }
            if (AppConfig.DEBUG) Log.d(TAG, "Handle is invalid");
            Thread.sleep(checkIntervalMs);
            timeoutMs -= checkIntervalMs;
        }
        throw new TimeoutException();
    }
}
