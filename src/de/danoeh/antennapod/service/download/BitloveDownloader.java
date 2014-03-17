package de.danoeh.antennapod.service.download;

import android.content.Context;
import android.util.Log;
import de.danoeh.antennapod.AppConfig;
import de.danoeh.antennapod.bittorrent.Session;
import de.danoeh.antennapod.feed.BitTorrentFeedMedia;
import de.danoeh.antennapod.feed.EnclosedFeedMedia;
import de.danoeh.antennapod.feed.FeedMedia;
import de.danoeh.antennapod.storage.DBReader;

/**
 * Uses the BittorrentDownloader if the given file is available on Bitlove and the HTTP downloader otherwise
 */
public class BitloveDownloader extends Downloader {
    private static final String TAG = "BitloveDownloader";

    private final Context context;
    private final Session session;

    private volatile Downloader currentDownloader = null;

    public BitloveDownloader(DownloadRequest request, Session session, Context context) {
        super(request);
        this.context = context;
        this.session = session;
        if (request.getFeedfileType() != FeedMedia.FEEDFILETYPE_FEEDMEDIA)
            throw new IllegalArgumentException("Feedfile type must be FeedMedia");
    }

    @Override
    protected void download() {
        final FeedMedia media = DBReader.getFeedMedia(context, getDownloadRequest().getFeedfileId());
        final String torrentUrl;

        if (media instanceof BitTorrentFeedMedia) {
            torrentUrl = request.getSource();
        } else if (media instanceof EnclosedFeedMedia) {
            torrentUrl = BitloveUtils.isAvailableOnBitlove(context, (EnclosedFeedMedia) media);
            if (AppConfig.DEBUG) Log.d(TAG, "Download is available on Bitlove. Using bittorrent downloader");
        } else {
            torrentUrl = null;
            if (AppConfig.DEBUG) Log.d(TAG, "Download is not available on Bitlove. Using http downloader");
        }

        boolean useHttp = false;
        if (torrentUrl != null && !cancelled) {
            final String originalSource = request.getSource();
            request.setSource(torrentUrl);
            currentDownloader = new BitTorrentDownloader(request, session);
            currentDownloader.download();
            if (cancelled) {    // if cancel was called without a downloader
                currentDownloader.cancel();
            }
            if (currentDownloader.getResult().isSuccessful() || currentDownloader.cancelled) {
                result = currentDownloader.getResult();
            } else {
                useHttp = true;
            }
            request.setSource(originalSource);
        } else if (cancelled) {
            getResult().setCancelled();
        } else {
            useHttp = true;
        }

        if (useHttp) {
            currentDownloader = new HttpDownloader(request);
            currentDownloader.download();
            if (cancelled) {    // if cancel() was called on the old downloader
                currentDownloader.cancel();
            }
            result = currentDownloader.getResult();
        }
    }

    @Override
    public void cancel() {
        super.cancel();
        if (currentDownloader != null) {
            currentDownloader.cancel();
        }
    }

    @Override
    public int getTypeStringAsResource() {
        if (currentDownloader != null) {
            return currentDownloader.getTypeStringAsResource();
        } else {
            return 0;
        }
    }
}

