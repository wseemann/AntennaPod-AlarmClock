package de.danoeh.antennapod.feed;

import java.util.Date;

import de.danoeh.antennapod.util.ChapterUtils;
import de.danoeh.antennapod.util.playback.Playable;

/**
 * FeedMedia object whose media file is contained directly in the enclosure in
 * the feed, i.e. the download URL of the enclosure's resource and the stream
 * URL as well as the local media file URL and the file URL are treated equally
 * respectively.
 */
public class EnclosedFeedMedia extends FeedMedia implements Playable {
	public static final int PLAYABLE_TYPE_ENCLOSED_FEEDMEDIA = 1;

	/** Points to originalEnclosureLink if feedburner was used. */
	private String originalEnclosureLink;

	public EnclosedFeedMedia(FeedItem i, String download_url, long size,
			String mime_type) {
		super(i, download_url, size, mime_type);
		streamUrl = download_url;
	}

	public EnclosedFeedMedia(long id, FeedItem item, int duration,
			int position, long size, String mime_type, String file_url,
			String download_url, boolean downloaded,
			Date playbackCompletionDate, String originalEnclosureLink) {
		super(id, item, duration, position, size, mime_type, file_url,
				download_url, downloaded, playbackCompletionDate, file_url,
				download_url);
		this.originalEnclosureLink = originalEnclosureLink;
	}

	public EnclosedFeedMedia(long id, FeedItem item) {
		super(id, item);
	}

	public MediaType getMediaType() {
		if (mime_type == null || mime_type.isEmpty()) {
			return MediaType.UNKNOWN;
		} else {
			if (mime_type.startsWith("audio")) {
				return MediaType.AUDIO;
			} else if (mime_type.startsWith("video")) {
				return MediaType.VIDEO;
			} else if (mime_type.equals("application/ogg")) {
				return MediaType.AUDIO;
			}
		}
		return MediaType.UNKNOWN;
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

	@Override
	public String getLocalMediaUrl() {
		return file_url;
	}

	@Override
	public String getStreamUrl() {
		return download_url;
	}

	@Override
	public void setStreamUrl(String streamUrl) {
		super.setStreamUrl(streamUrl);
		download_url = streamUrl;
	}

	@Override
	public void setDownload_url(String download_url) {
		super.setDownload_url(download_url);
		streamUrl = download_url;
	}

	@Override
	public void updateFromOther(FeedMedia other) {
		if (other instanceof EnclosedFeedMedia) {
			updateFromOther((EnclosedFeedMedia) other);
		} else {
			super.updateFromOther(other);
		}
	}

	@Override
	public boolean compareWithOther(FeedMedia other) {
		if (other instanceof EnclosedFeedMedia) {
			return compareWithOther((EnclosedFeedMedia) other);
		} else {
			return super.compareWithOther(other);
		}
	}

	public void updateFromOther(EnclosedFeedMedia other) {
		super.updateFromOther(other);
		if (other.originalEnclosureLink != null) {
			originalEnclosureLink = other.originalEnclosureLink;
		}
	}

	public boolean compareWithOther(EnclosedFeedMedia other) {
		if (super.compareWithOther(other)) {
			return true;
		}
		if (other.originalEnclosureLink != null) {
			if (originalEnclosureLink == null
					|| !originalEnclosureLink
							.equals(other.originalEnclosureLink)) {
				return true;
			}
		}
		if (other.originalEnclosureLink == null
				&& originalEnclosureLink != null) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the download URL to the original media file. The returned value
	 * will only be different from the download url if feedburner was used in
	 * the feed.
	 */
	public String getOriginalEnclosureLink() {
		if (originalEnclosureLink != null) {
			return originalEnclosureLink;
		}
		return download_url;
	}

	public void setOriginalEnclosureLink(String originalEnclosureLink) {
		this.originalEnclosureLink = originalEnclosureLink;
	}

}
