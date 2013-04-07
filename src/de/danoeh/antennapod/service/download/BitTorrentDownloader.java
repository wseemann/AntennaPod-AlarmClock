package de.danoeh.antennapod.service.download;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import android.util.Log;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;

import de.danoeh.antennapod.AppConfig;
import de.danoeh.antennapod.asynctask.DownloadStatus;
import de.danoeh.antennapod.feed.BitTorrentFeedMedia;
import de.danoeh.antennapod.util.DownloadError;

public class BitTorrentDownloader extends Downloader implements Observer {
	private static final String TAG = "TorrentDownloader";

	public BitTorrentDownloader(DownloaderCallback downloaderCallback,
			DownloadStatus status) {
		super(downloaderCallback, status);
		if (status.getFeedFile() == null
				|| !(status.getFeedFile() instanceof BitTorrentFeedMedia)) {
			throw new IllegalArgumentException(
					"FeedFile must be non-null and of type BitTorrentFeedMedia");
		}
	}

	@Override
	protected void download() {
		// Delete .torrent if already exists
		FileUtils.deleteQuietly(new File(status.getFeedFile().getFile_url()));

		// Download .torrent via HttpDownloader
		DownloaderCallback httpDownloaderCallback = new DownloaderCallback() {

			@Override
			public void onDownloadCompleted(Downloader downloader) {
			}
		};
		DownloadStatus tmpStatus = new DownloadStatus(status.getFeedFile(),
				"tmp");
		Downloader tmpDownloader = new HttpDownloader(httpDownloaderCallback,
				tmpStatus);
		if (AppConfig.DEBUG)
			Log.d(TAG, "Downloading .torrent file");
		tmpDownloader.start();
		try {
			tmpDownloader.join();

			if (!tmpDownloader.getStatus().isSuccessful()) {
				onFail(tmpDownloader.getStatus().getReason(), tmpDownloader
						.getStatus().getReasonDetailed());
				return;
			}
			if (AppConfig.DEBUG)
				Log.d(TAG, "Downloaded .torrent file");

		} catch (InterruptedException e) {
			e.printStackTrace();
			onFail(DownloadError.ERROR_DOWNLOAD_CANCELLED, e.getMessage());
			return;
		}
		// Start client
		File torrentFile = new File(status.getFeedFile().getFile_url());
		try {
			SharedTorrent sharedTorrent = SharedTorrent.fromFile(torrentFile,
					torrentFile.getParentFile());
			Client client = new Client(InetAddress.getLocalHost(),
					sharedTorrent);
			client.addObserver(this);
			// Set local file url
			if (sharedTorrent.getFilenames().size() > 0) {
				BitTorrentFeedMedia torrentMedia = (BitTorrentFeedMedia) status
						.getFeedFile();
				torrentMedia.setLocalFileUrl(new File(torrentFile
						.getParentFile(), sharedTorrent.getFilenames().get(0))
						.toString());
				if (AppConfig.DEBUG)
					Log.d(TAG, "starting torrent download");
				status.setSize(sharedTorrent.getSize());
				client.download();

				while (client.getState() != Client.ClientState.DONE
						&& client.getState() != Client.ClientState.ERROR
						&& !client.getTorrent().isComplete()) {
					if (cancelled) {
						client.stop();
						onCancelled();
						return;
					}
					// Update progress
					status.setSoFar(sharedTorrent.getSize() - sharedTorrent.getLeft());
					status.setProgressPercent((int) sharedTorrent
							.getCompletion());
					if (status.getProgressPercent() == 100) {
						client.stop();
						break;
					}
					try {
						sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
						client.stop();
						onCancelled();
						return;
					}
				}
				if (client.getState() == Client.ClientState.ERROR) {
					onFail(0, null);
				} else {
					onSuccess();
				}
				client.stop();
			} else {
				onFail(DownloadError.ERROR_UNSUPPORTED_TYPE, "");
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			onFail(0, "");
			return;
		} catch (IOException e) {
			e.printStackTrace();
			onFail(DownloadError.ERROR_IO_ERROR, e.getMessage());
			return;
		}
	}

	private void onSuccess() {
		if (AppConfig.DEBUG)
			Log.d(TAG, "Download was successful");
		status.setSuccessful(true);
		status.setDone(true);
	}

	private void onCancelled() {
		if (AppConfig.DEBUG)
			Log.d(TAG, "Download was cancelled");
		status.setReason(DownloadError.ERROR_DOWNLOAD_CANCELLED);
		status.setDone(true);
		status.setSuccessful(false);
		status.setCancelled(true);
	}

	private void onFail(int reason, String reasonDetailed) {
		status.setReason(reason);
		status.setReasonDetailed(reasonDetailed);
		status.setDone(true);
		status.setSuccessful(false);
	}

	@Override
	public void update(Observable observable, Object data) {
		if (observable instanceof Client) {
			Client client = (Client) observable;
			if (AppConfig.DEBUG)
				Log.d(TAG, "State of client: " + client.getState());
		}
	}

}
