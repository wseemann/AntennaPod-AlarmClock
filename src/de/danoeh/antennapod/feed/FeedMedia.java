package de.danoeh.antennapod.feed;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcel;
import android.os.Parcelable;
import de.danoeh.antennapod.PodcastApp;
import de.danoeh.antennapod.preferences.PlaybackPreferences;
import de.danoeh.antennapod.storage.DBReader;
import de.danoeh.antennapod.storage.DBWriter;
import de.danoeh.antennapod.util.ChapterUtils;
import de.danoeh.antennapod.util.playback.Playable;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Represents a media file that is attached to a feeditem. The download URL
 * attribute from FeedFile points to the resource set in the feed. The file url
 * points to the location where the resource from the feed has been saved.
 */
public abstract class FeedMedia extends FeedFile implements Playable {

    public static final int FEEDFILETYPE_FEEDMEDIA = 2;
    public static final int PLAYABLE_TYPE_FEEDMEDIA = 1;

    public static final String PREF_MEDIA_ID = "FeedMedia.PrefMediaId";
    public static final String PREF_FEED_ID = "FeedMedia.PrefFeedId";

    protected int duration;
    protected int position; // Current position in file
    protected long size; // File size in Byte
    protected int played_duration; // How many ms of this file have been played (for autoflattring)
    protected String mime_type;
    protected volatile FeedItem item;
    protected Date playbackCompletionDate;

    /**
     * Points to a local media file that can be played by the playback service.
     */
    protected String localFileUrl;

    /**
     * Points to a remote resource that can be streamed by the playback service.
     */
    protected String streamUrl;


    /* Used for loading item when restoring from parcel. */
    private long itemID;

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
                     String localFileUrl, String streamUrl, int played_duration) {
        super(file_url, download_url, downloaded);
        this.id = id;
        this.item = item;
        this.duration = duration;
        this.position = position;
        this.size = size;
        this.mime_type = mime_type;
        this.played_duration = played_duration;
        this.localFileUrl = localFileUrl;
        this.streamUrl = streamUrl;
        this.playbackCompletionDate = playbackCompletionDate == null
                ? null : (Date) playbackCompletionDate.clone();
    }

    public FeedMedia(long id, FeedItem item) {
        super();
        this.id = id;
        this.item = item;
    }

    public static FeedMedia createFromParcel(long id, FeedItem item, int duration, int position,
                                             long size, String mime_type, String file_url, String download_url,
                                             boolean downloaded, Date playbackCompletionDate,
                                             String localFileUrl, String streamUrl, int played_duration, int playableType, Parcel p) {
        switch (playableType) {
            case EnclosedFeedMedia.PLAYABLE_TYPE_ENCLOSED_FEEDMEDIA:
                return new EnclosedFeedMedia(id, item, duration, position, size, mime_type, file_url, download_url, downloaded, playbackCompletionDate, p.readString(), played_duration);
            case BitTorrentFeedMedia.PLAYABLE_TYPE_BITTORRENT_MEDIA:
                return new BitTorrentFeedMedia(id, item, duration, position, size, mime_type, file_url, download_url, downloaded, playbackCompletionDate, localFileUrl, streamUrl, played_duration);
            default:
                return null;
        }
    }

    @Override
    public String getHumanReadableIdentifier() {
        if (item != null && item.getTitle() != null) {
            return item.getTitle();
        } else {
            return download_url;
        }
    }

    /**
     * Uses mimetype to determine the type of media.
     */
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

    /**
     * Reads playback preferences to determine whether this FeedMedia object is
     * currently being played.
     */
    public boolean isPlaying() {
        return PlaybackPreferences.getCurrentlyPlayingMedia() == FeedMedia.PLAYABLE_TYPE_FEEDMEDIA
                && PlaybackPreferences.getCurrentlyPlayingFeedMediaId() == id;
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

    public int getPlayedDuration() {
        return played_duration;
    }

    public void setPlayedDuration(int played_duration) {
        this.played_duration = played_duration;
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

    /**
     * Sets the item object of this FeedMedia. If the given
     * FeedItem object is not null, it's 'media'-attribute value
     * will also be set to this media object.
     */
    public void setItem(FeedItem item) {
        this.item = item;
        if (item != null && item.getMedia() != this) {
            item.setMedia(this);
        }
    }

    public Date getPlaybackCompletionDate() {
        return playbackCompletionDate == null
                ? null : (Date) playbackCompletionDate.clone();
    }

    public void setPlaybackCompletionDate(Date playbackCompletionDate) {
        this.playbackCompletionDate = playbackCompletionDate == null
                ? null : (Date) playbackCompletionDate.clone();
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
        dest.writeLong(id);
        dest.writeLong(item.getId());

        dest.writeInt(duration);
        dest.writeInt(position);
        dest.writeLong(size);
        dest.writeString(mime_type);
        dest.writeString(file_url);
        dest.writeString(download_url);
        dest.writeByte((byte) ((downloaded) ? 1 : 0));
        dest.writeLong((playbackCompletionDate != null) ? playbackCompletionDate.getTime() : 0);
        dest.writeString(localFileUrl);
        dest.writeString(streamUrl);
        dest.writeInt(played_duration);
        dest.writeInt(getPlayableType());
    }

    @Override
    public void writeToPreferences(Editor prefEditor) {
        prefEditor.putLong(PREF_FEED_ID, item.getFeed().getId());
        prefEditor.putLong(PREF_MEDIA_ID, id);
    }

    @Override
    public void loadMetadata() throws PlayableException {
        if (item == null && itemID != 0) {
            item = DBReader.getFeedItem(PodcastApp.getInstance(), itemID);
        }
    }

    @Override
    public void loadChapterMarks() {
        if (getChapters() == null && !localFileAvailable()) {
            ChapterUtils.loadChaptersFromStreamUrl(this);
            if (getChapters() != null && item != null) {
                DBWriter.setFeedItem(PodcastApp.getInstance(),
                        item);
            }
        }

    }

    @Override
    public String getEpisodeTitle() {
        if (item == null) {
            return null;
        }
        if (getItem().getTitle() != null) {
            return getItem().getTitle();
        } else {
            return getItem().getIdentifyingValue();
        }
    }

    @Override
    public List<Chapter> getChapters() {
        if (item == null) {
            return null;
        }
        return getItem().getChapters();
    }

    @Override
    public String getWebsiteLink() {
        if (item == null) {
            return null;
        }
        return getItem().getLink();
    }

    @Override
    public String getFeedTitle() {
        if (item == null) {
            return null;
        }
        return getItem().getFeed().getTitle();
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
    public String getPaymentLink() {
        if (item == null) {
            return null;
        }
        return getItem().getPaymentLink();
    }

    @Override
    public boolean localFileAvailable() {
        return isDownloaded() && localFileUrl != null;
    }

    @Override
    public boolean streamAvailable() {
        return getStreamUrl() != null;
    }

    @Override
    public void saveCurrentPosition(SharedPreferences pref, int newPosition) {
        setPosition(newPosition);
        DBWriter.setFeedMediaPlaybackInformation(PodcastApp.getInstance(), this);
    }

    @Override
    public void onPlaybackStart() {
    }

    @Override
    public void onPlaybackCompleted() {

    }

    @Override
    public int getPlayableType() {
        return PLAYABLE_TYPE_FEEDMEDIA;
    }

    @Override
    public void setChapters(List<Chapter> chapters) {
        getItem().setChapters(chapters);
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public String getLocalFileUrl() {
        return localFileUrl;
    }

    public void setLocalFileUrl(String localFileUrl) {
        this.localFileUrl = localFileUrl;
    }

    @Override
    public Callable<String> loadShownotes() {
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                if (item == null) {
                    item = DBReader.getFeedItem(PodcastApp.getInstance(), itemID);
                }
                if (item.getContentEncoded() == null || item.getDescription() == null) {
                    DBReader.loadExtraInformationOfFeedItem(PodcastApp.getInstance(), item);

                }
                return (item.getContentEncoded() != null) ? item.getContentEncoded() : item.getDescription();
            }
        };
    }

    public static final Parcelable.Creator<FeedMedia> CREATOR = new Parcelable.Creator<FeedMedia>() {
        public FeedMedia createFromParcel(Parcel in) {
            final long id = in.readLong();
            final long itemID = in.readLong();
            FeedMedia result = FeedMedia.createFromParcel(id, null, in.readInt(), in.readInt(), in.readLong(), in.readString(), in.readString(),
                    in.readString(), in.readByte() != 0, new Date(in.readLong()), in.readString(), in.readString(), in.readInt(), in.readInt(), in);
            result.itemID = itemID;
            return result;
        }

        public FeedMedia[] newArray(int size) {
            return new FeedMedia[size];
        }
    };

    @Override
    public InputStream openImageInputStream() {
        InputStream out = new Playable.DefaultPlayableImageLoader(this)
                .openImageInputStream();
        if (out == null) {
            if (item.getFeed().getImage() != null) {
                return item.getFeed().getImage().openImageInputStream();
            }
        }
        return out;
    }

    @Override
    public String getImageLoaderCacheKey() {
        String out = new Playable.DefaultPlayableImageLoader(this)
                .getImageLoaderCacheKey();
        if (out == null) {
            if (item.getFeed().getImage() != null) {
                return item.getFeed().getImage().getImageLoaderCacheKey();
            }
        }
        return out;
    }

    @Override
    public InputStream reopenImageInputStream(InputStream input) {
        if (input instanceof FileInputStream) {
            return item.getFeed().getImage().reopenImageInputStream(input);
        } else {
            return new Playable.DefaultPlayableImageLoader(this)
                    .reopenImageInputStream(input);
        }
    }
}
