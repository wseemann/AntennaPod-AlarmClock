package de.danoeh.antennapod.feed;

import de.danoeh.antennapod.syndication.util.SyndTypeUtils;

import java.util.Date;

public class BitTorrentFeedMedia extends FeedMedia {
    public static final int PLAYABLE_TYPE_BITTORRENT_MEDIA = 3;
    public static final String MIME_TYPE_BITTORRENT = "application/x-bittorrent";

    private MediaType mediaType;

    public BitTorrentFeedMedia(FeedItem i, String download_url, long size,
                               String mime_type) {
        super(i, download_url, size, mime_type);
        mediaType = retrieveMediaType();
    }

    public BitTorrentFeedMedia(long id, FeedItem item, int duration,
                               int position, long size, String mime_type, String file_url,
                               String download_url, boolean downloaded,
                               Date playbackCompletionDate, String localFileUrl, String streamUrl, int played_duration) {
        super(id, item, duration, position, size, mime_type, file_url,
                download_url, downloaded, playbackCompletionDate, localFileUrl,
                streamUrl, played_duration);
        mediaType = retrieveMediaType();
    }

    public BitTorrentFeedMedia(long id, FeedItem item) {
        super(id, item);
    }

    @Override
    public void loadMetadata() throws PlayableException {

    }

    @Override
    public int getPlayableType() {
        return PLAYABLE_TYPE_BITTORRENT_MEDIA;
    }

    /**
     * This implementation will try to retrieve the media type from the download
     * URL of the torrent file. This can only work if the URL ends with
     * [suffixOfMediafile].torrent
     */
    @Override
    public MediaType getMediaType() {
        if (mediaType != null) {
            return mediaType;
        }
        return MediaType.UNKNOWN;
    }

    /**
     * Returns download url without the .torrent.
     */
    public String getUrlWithoutTorrentExtension() {
        if (download_url != null) {
            int lastIndex = download_url.lastIndexOf(".torrent");
            if (lastIndex > 0) {
                return download_url.substring(0, lastIndex);
            }
        }
        return null;
    }

    private MediaType retrieveMediaType() {
        String url = getUrlWithoutTorrentExtension();
        if (url != null) {
            String type = SyndTypeUtils.getValidMimeTypeFromUrl(url);
            if (type != null) {
                if (type.startsWith("audio")) {
                    return MediaType.AUDIO;
                } else if (type.startsWith("video")) {
                    return MediaType.VIDEO;
                } else if (type.equals("application/ogg")) {
                    return MediaType.AUDIO;
                }
            }
        }
        return MediaType.UNKNOWN;
    }

}
