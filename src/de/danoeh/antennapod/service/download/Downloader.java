package de.danoeh.antennapod.service.download;

import android.util.Log;
import de.danoeh.antennapod.AppConfig;
import de.danoeh.antennapod.R;
import de.danoeh.antennapod.asynctask.DownloadStatus;
import de.danoeh.antennapod.util.DownloadError;

/** Downloads files */
public abstract class Downloader extends Thread {
	private static final String TAG = "Downloader";
	private DownloaderCallback downloaderCallback;

	protected boolean finished;

	protected volatile boolean cancelled;

	protected volatile DownloadStatus status;

	public Downloader(DownloaderCallback downloaderCallback,
			DownloadStatus status) {
		super();
		this.downloaderCallback = downloaderCallback;
		this.status = status;
		this.status.setStatusMsg(R.string.download_pending);
		this.cancelled = false;
	}

	/**
	 * This method must be called when the download was completed, failed, or
	 * was cancelled
	 */
	protected void finish() {
		if (!finished) {
			finished = true;
			downloaderCallback.onDownloadCompleted(this);
		}
	}

	protected abstract void download();

	@Override
	public final void run() {
		download();
		finish();
	}

	public DownloadStatus getStatus() {
		return status;
	}

	public void cancel() {
		cancelled = true;
		interrupt();
	}

	/**
	 * Sets the appropriate fields of the status object that indicate that a
	 * download succeeded.
	 */
	protected void onSuccess() {
		if (AppConfig.DEBUG)
			Log.d(TAG, "Download was successful");
		status.setSuccessful(true);
		status.setDone(true);
	}

	/**
	 * Sets the appropriate fields of the status object that indicate that a
	 * download failed.
	 */
	protected void onFail(int reason, String reasonDetailed) {
		if (AppConfig.DEBUG) {
			Log.d(TAG, "Download failed");
		}
		status.setReason(reason);
		status.setReasonDetailed(reasonDetailed);
		status.setDone(true);
		status.setSuccessful(false);
		cleanup();
	}

	/**
	 * Sets the appropriate fields of the status object that indicate that a
	 * download was cancelled.
	 */
	protected void onCancelled() {
		if (AppConfig.DEBUG)
			Log.d(TAG, "Download was cancelled");
		status.setReason(DownloadError.ERROR_DOWNLOAD_CANCELLED);
		status.setDone(true);
		status.setSuccessful(false);
		status.setCancelled(true);
		cleanup();
	}

	/**
	 * Deletes files which are not needed anymore. The default implementation
	 * does nothing.
	 */
	protected void cleanup() {

	}

}