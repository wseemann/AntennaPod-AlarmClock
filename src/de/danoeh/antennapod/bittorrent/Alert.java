package de.danoeh.antennapod.bittorrent;

public class Alert {
    public static final int
        TYPE_UNKOWN = -1,
        TYPE_TORRENT_ADDED = 0,
        TYPE_ADD_TORRENT = 1,
        TYPE_TORRENT_REMOVED = 2,
        TYPE_READ_PIECE = 3,
        TYPE_EXTERNAL_IP = 4,
        TYPE_LISTEN_FAILED = 5,
        TYPE_LISTEN_SUCCEEDED = 6,
        TYPE_PORTMAP_ERROR = 7,
        TYPE_PORTMAP = 8,
        TYPE_PORTMAP_LOG = 9,
        TYPE_FILE_ERROR = 10,
        TYPE_TORRENT_ERROR = 11,
        TYPE_FILE_RENAMED = 12,
        TYPE_FILE_RENAME_FAILED = 13,
        TYPE_TRACKER_ANNOUNCE = 14,
        TYPE_TRACKER_ERROR = 15,
        TYPE_TRACKER_REPLY = 16,
        TYPE_TRACKER_WARNING = 17,
        TYPE_SCRAPE_REPLY= 18,
        TYPE_SCRAPE_FAILED = 19,
        TYPE_URL_SEED = 20,
        TYPE_HASH_FAILED = 21,
        TYPE_PEER = 22,
        TYPE_PEER_CONNECT = 23,
        TYPE_PEER_BAN = 24,
        TYPE_PEER_SNUBBED = 25,
        TYPE_PEER_UNSNUBBED = 26,
        TYPE_PEER_ERROR = 27,
        TYPE_PEER_DISCONNECTED = 29,
        TYPE_INVALID_REQUEST = 30,
        TYPE_REQUEST_DROPPED = 31,
        TYPE_BLOCK_TIMEOUT = 32,
        TYPE_BLOCK_FINISHED = 33,
        TYPE_LSD_PEER = 34,
        TYPE_FILE_COMPLETED = 35,
        TYPE_BLOCK_DOWNLOADING = 36,
        TYPE_UNWANTED_BLOCK = 37,
        TYPE_TORRENT_DELETE_FAILED = 38,
        TYPE_TORRENT_DELETED = 39,
        TYPE_TORRENT_FINISHED = 40,
        TYPE_PERFORMANCE = 41;


    protected int type;
    protected String message;

    public Alert(int type, String message) {
        this.type = type;
        this.message = message;
    }

    @Override
    public String toString() {
        return "Alert[type: " + type
                + "; message: " + message + "]";
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
