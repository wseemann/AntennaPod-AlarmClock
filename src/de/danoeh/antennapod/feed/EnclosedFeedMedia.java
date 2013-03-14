package de.danoeh.antennapod.feed;

import java.util.Date;

import de.danoeh.antennapod.util.ChapterUtils;
import de.danoeh.antennapod.util.playback.Playable;

/**
 * FeedMedia object whose media file is contained directly in the enclosure in
 * the feed, i.e. the download URL of the enclosure's resource and the stream
 * URL as well as the local media file URL and the file URLthe are treated
 * equally respectively.
 */
public class EnclosedFeedMedia extends FeedMedia implements Playable {
	public static final int PLAYABLE_TYPE_ENCLOSED_FEEDMEDIA = 1;

	public EnclosedFeedMedia(FeedItem i, String download_url, long size,
			String mime_type) {
		super(i, download_url, size, mime_type);
	}

	public EnclosedFeedMedia(long id, FeedItem item, int duration,
			int position, long size, String mime_type, String file_url,
			String download_url, boolean downloaded, Date playbackCompletionDate) {
		super(id, item, duration, position, size, mime_type, file_url,
				download_url, downloaded, playbackCompletionDate, file_url,
				download_url);
	}

	public EnclosedFeedMedia(long id, FeedItem item) {
		super(id, item);
	}

	@Override
	public void loadMetadata() throws PlayableException {
		if (getChapters() == null && !isDownloaded()) {
			ChapterUtils.loadChaptersFromStreamUrl(this);
		}
	}

	@Override
	public int getPlayableType() {
		return PLAYABLE_TYPE_ENCLOSED_FEEDMEDIA;
	}
}
