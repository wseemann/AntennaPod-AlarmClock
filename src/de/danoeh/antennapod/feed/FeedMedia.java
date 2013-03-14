package de.danoeh.antennapod.feed;

import java.io.File;
import java.util.Date;
import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcel;
import android.os.Parcelable;
import de.danoeh.antennapod.PodcastApp;
import de.danoeh.antennapod.preferences.PlaybackPreferences;
import de.danoeh.antennapod.util.playback.Playable;

/**
 * Represents a media file that is attached to a feeditem. The download URL
 * attribute from FeedFile points to the resource set in the feed. The file url
 * points to the location where the resource from the feed has been saved.
 */
public abstract class FeedMedia extends FeedFile implements Playable {

	public static final int FEEDFILETYPE_FEEDMEDIA = 2;
	public static final String PREF_MEDIA_ID = "FeedMedia.PrefMediaId";
	public static final String PREF_FEED_ID = "FeedMedia.PrefFeedId";
	protected int duration;
	protected int position;
	protected long size;
	protected String mime_type;
	protected FeedItem item;
	protected Date playbackCompletionDate;

	/** Points to a local media file that can be played by the playback service. */
	protected String localFileUrl;

	/**
	 * Points to a remote resource that can be streamed by the playback service.
	 */
	protected String streamUrl;

	public FeedMedia(FeedItem i, String download_url, long size,
			String mime_type) {
		super(null, download_url, false);
		this.item = i;
		this.size = size;
		this.mime_type = mime_type;
	}

	public FeedMedia(long id, FeedItem item, int duration, int position,
			long size, String mime_type, String file_url, String download_url,
			boolean downloaded, Date playbackCompletionDate,
			String localFileUrl, String streamUrl) {
		super(file_url, download_url, downloaded);
		this.id = id;
		this.item = item;
		this.duration = duration;
		this.position = position;
		this.size = size;
		this.mime_type = mime_type;
		this.playbackCompletionDate = playbackCompletionDate;
		this.localFileUrl = localFileUrl;
		this.streamUrl = streamUrl;
	}

	public FeedMedia(long id, FeedItem item) {
		super();
		this.id = id;
		this.item = item;
	}

	@Override
	public String getHumanReadableIdentifier() {
		if (item != null && item.getTitle() != null) {
			return item.getTitle();
		} else {
			return download_url;
		}
	}

	/** Uses mimetype to determine the type of media. */
	public abstract MediaType getMediaType();

	public void updateFromOther(FeedMedia other) {
		super.updateFromOther(other);
		if (other.size > 0) {
			size = other.size;
		}
		if (other.mime_type != null) {
			mime_type = other.mime_type;
		}
	}

	public boolean compareWithOther(FeedMedia other) {
		if (super.compareWithOther(other)) {
			return true;
		}
		if (other.mime_type != null) {
			if (mime_type == null || !mime_type.equals(other.mime_type)) {
				return true;
			}
		}
		if (other.size > 0 && other.size != size) {
			return true;
		}
		return false;
	}

	@Override
	public int getTypeAsInt() {
		return FEEDFILETYPE_FEEDMEDIA;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getMime_type() {
		return mime_type;
	}

	public void setMime_type(String mime_type) {
		this.mime_type = mime_type;
	}

	public FeedItem getItem() {
		return item;
	}

	public void setItem(FeedItem item) {
		this.item = item;
	}

	public Date getPlaybackCompletionDate() {
		return playbackCompletionDate;
	}

	public void setPlaybackCompletionDate(Date playbackCompletionDate) {
		this.playbackCompletionDate = playbackCompletionDate;
	}

	public boolean isInProgress() {
		return (this.position > 0);
	}

	public FeedImage getImage() {
		if (item != null && item.getFeed() != null) {
			return item.getFeed().getImage();
		}
		return null;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(item.getFeed().getId());
		dest.writeLong(item.getId());
	}

	@Override
	public void writeToPreferences(Editor prefEditor) {
		prefEditor.putLong(PREF_FEED_ID, item.getFeed().getId());
		prefEditor.putLong(PREF_MEDIA_ID, id);
	}

	@Override
	public String getEpisodeTitle() {
		if (getItem().getTitle() != null) {
			return getItem().getTitle();
		} else {
			return getItem().getIdentifyingValue();
		}
	}

	@Override
	public List<Chapter> getChapters() {
		return getItem().getChapters();
	}

	@Override
	public String getWebsiteLink() {
		return getItem().getLink();
	}

	@Override
	public String getFeedTitle() {
		return getItem().getFeed().getTitle();
	}

	@Override
	public String getImageFileUrl() {
		if (getItem().getFeed().getImage() != null) {
			return getItem().getFeed().getImage().getFile_url();
		} else {
			return null;
		}
	}

	@Override
	public Object getIdentifier() {
		return id;
	}

	@Override
	public String getLocalMediaUrl() {
		return localFileUrl;
	}

	@Override
	public String getStreamUrl() {
		return streamUrl;
	}

	@Override
	public boolean localFileAvailable() {
		return isDownloaded() && getLocalMediaUrl() != null;
	}

	@Override
	public boolean streamAvailable() {
		return streamUrl != null;
	}

	@Override
	public void saveCurrentPosition(SharedPreferences pref, int newPosition) {
		position = newPosition;
		FeedManager.getInstance().setFeedMedia(PodcastApp.getInstance(), this);
	}

	@Override
	public void onPlaybackStart() {
	}

	@Override
	public void onPlaybackCompleted() {

	}

	@Override
	public void setChapters(List<Chapter> chapters) {
		getItem().setChapters(chapters);
	}

	@Override
	public String getPaymentLink() {
		return getItem().getPaymentLink();
	}

	@Override
	public void loadShownotes(final ShownoteLoaderCallback callback) {
		String contentEncoded = item.getContentEncoded();
		if (item.getDescription() == null || contentEncoded == null) {
			FeedManager.getInstance().loadExtraInformationOfItem(
					PodcastApp.getInstance(), item,
					new FeedManager.TaskCallback<String[]>() {
						@Override
						public void onCompletion(String[] result) {
							if (result[1] != null) {
								callback.onShownotesLoaded(result[1]);
							} else {
								callback.onShownotesLoaded(result[0]);

							}

						}
					});
		} else {
			callback.onShownotesLoaded(contentEncoded);
		}
	}

	public static final Parcelable.Creator<FeedMedia> CREATOR = new Parcelable.Creator<FeedMedia>() {
		public FeedMedia createFromParcel(Parcel in) {
			long feedId = in.readLong();
			long itemId = in.readLong();
			FeedItem item = FeedManager.getInstance().getFeedItem(itemId,
					feedId);
			if (item != null) {
				return item.getMedia();
			} else {
				return null;
			}
		}

		public FeedMedia[] newArray(int size) {
			return new FeedMedia[size];
		}
	};

	public boolean isPlaying() {
		return PlaybackPreferences.getCurrentlyPlayingMedia() == getPlayableType()
				&& PlaybackPreferences.getCurrentlyPlayingFeedMediaId() == id;
	}

	/** Returns true if the local media file exists. */
	public boolean localMediaFileExists() {
		if (getLocalMediaUrl() == null) {
			return false;
		} else {
			File f = new File(getLocalMediaUrl());
			return f.exists();
		}
	}

	public void setLocalFileUrl(String localFileUrl) {
		this.localFileUrl = localFileUrl;
	}

	public void setStreamUrl(String streamUrl) {
		this.streamUrl = streamUrl;
	}

}
