package de.danoeh.antennapod.bittorrent;

import java.io.File;

/**
 * Manages multiple torrent downloads.
 * This class manages a native session object from the libtorrent
 * library.
 */
public class Session {
    public static final int DEFAULT_PORT_RANGE_START = 6881;
    public static final int DEFAULT_PORT_RANGE_END = 6889;

    private static boolean torrentLibAvailable;

    static {
        try {
            System.loadLibrary("torrent");
            torrentLibAvailable = true;
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
            torrentLibAvailable = false;
        }
    }

    /**
     * Returns true if the native bittorrrent library could be loaded.
     * This class will not work if this method returns false.
     */
    public static boolean isTorrentLibAvailable() {
        return torrentLibAvailable;
    }

    public static final int
            ADD_TORRENT_SEED_MODE = 0x001,
            ADD_TORRENT_OVERRIDE_RESUME_DATA = 0x002,
            ADD_TORRENT_UPLOAD_MODE = 0x004,
            ADD_TORRENT_SHARE_MODE = 0x008,
            ADD_TORRENT_APPLY_IP_FILTER = 0x010,
            ADD_TORRENT_PAUSED = 0x020,
            ADD_TORRENT_AUTO_MANAGED = 0x040,
            ADD_TORRENT_DUPLICATE_IS_ERROR = 0x080,
            ADD_TORRENT_MERGE_RESUME_TRACKERS = 0x100,
            ADD_TORRENT_UPDATE_SUBSCRIBE = 0x200,
            ALERTMASK_ERROR = 0x1,
            ALERTMASK_PEER = 0x2,
            ALERTMASK_PORT_MAPPING = 0x4,
            ALERTMASK_STORAGE = 0x8,
            ALERTMASK_TRACKER = 0x10,
            ALERTMASK_DEBUG = 0x20,
            ALERTMASK_STATUS = 0x40,
            ALERTMASK_PROGRESS = 0x80,
            ALERTMASK_IP_BLOCK = 0x100,
            ALERTMASK_PERFORMANCE = 0x200,
            ALERTMASK_STATS = 0x800,
            ALERTMASK_DHT = 0x400,
            ALERTMASK_ALL_CATEGORIES = 0x7fffffff;

    private long nativeObj;

    /**
     * Creates a new Session object with the default libtorrent fingerprint and with
     * an alert mask set to ALERTMASK_ERROR.
     * The Session object will not listen on any ports after this constructor has been called.
     */
    public Session() {
        nativeObj = n_session();
    }

    /**
     * Creates a new Session object with the given fingerprint and with an alertmask
     * set to ALERT_MAKS_ERROR.
     * The Session object will not listen on any ports after this constructor has been called.
     *
     * @param id       The clent id. Must be exactly two characters long.
     * @param major    Major version number. Must be within the range [0,9].
     * @param minor    Minor version number. Must be within the range [0,9].
     * @param revision Revision version number. Must be within range [0,9].
     * @param tag      Version tag. Must be within range [0,9].
     */
    public Session(String id, int major, int minor, int revision, int tag) {
        if (id == null) {
            throw new NullPointerException();
        }
        if (id.length() != 2) {
            throw new IllegalArgumentException("Length of id must be 2.");
        }
        nativeObj = n_session(id, major, minor, revision, tag);
    }

    /**
     * Changes the listen port of this session. See the libtorrent documentation for
     * more details.
     */
    public void listenOn(int rangeStart, int rangeEnd) throws LibtorrentException {
        n_listen_on(rangeStart, rangeEnd);
    }

    /**
     * Adds a new torrent to the session.
     *
     * @param savePath Path to the directory where the downloaded files should be saved.
     * @param url      URL that points to a downloadable .torrent file.
     * @param flags
     * @return A TorrentHandle object that can be used to control the torrent download and
     * retrive status information about it.
     */
    public TorrentHandle addTorrent(String savePath, String url, int flags) throws LibtorrentException {
        return new TorrentHandle(n_add_torrent_url(savePath, url, flags));
    }

    /**
     * Adds a new torrent to the session.
     *
     * @param savePath      Path to the directory where the downloaded files should be saved.
     * @param torrentInfo   A local .torrent file.
     * @param flags
     * @return A TorrentHandle object that can be used to control the torrent download and
     * retrive status information about it.
     */
    public TorrentHandle addTorrent(String savePath, File torrentInfo, int flags) throws LibtorrentException {
        return new TorrentHandle(n_add_torrent_file(savePath, torrentInfo.getAbsolutePath(), flags));
    }

    /**
     * Removes a torrent from the session asyncronously.
     *
     * @param deleteFiles True if all files downloaded by this torrent should be deleted.
     */
    public void removeTorrent(TorrentHandle handle, boolean deleteFiles) {
        n_remove_torrent(handle.getNativeObj(), deleteFiles);
    }

    /*
    public TorrentHandle[] getTorrents() {
        int[] addrs = n_get_torrents();
        TorrentHandle[] result = new TorrentHandle[addrs.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = new TorrentHandle(addrs[i]);
        }
        return result;
    }
    */

    /**
     * Calls the pop_alert method of the session method to receive new alerts.
     *
     * @return A new Alert object or null if no new alerts are available.
     */
    public Alert popAlert() {
        return n_pop_alert();
    }

    /**
     * Blocks until a new alert is available or the timeout has occurred. If an
     * alert is returned, it will NOT be removed from the alert queue until popAlert has
     * been called.
     *
     * @return A new alert object or null if the timout has occurred first.
     */
    public Alert waitForAlert(long maxWait) {
        return n_wait_for_alert(maxWait);
    }

    /**
     * Calls the abort method of the native session object, which will shut down the session
     * asynchronously. This method should always be called as soon as the session object is
     * not needed anymore.
     */
    public void shutdown() {
        n_shutdown();
    }

    /**
     * Changes the alert mask of this session object.
     *
     * @param mask The new alert mask. Use the ALERTMASK_* constant for creating the alert mask.
     */
    public void setAlertMask(int mask) {
        n_set_alert_mask(mask);
    }

    @Override
    protected void finalize() throws Throwable {
        n_delete();
    }

    private native long n_session();

    private native long n_session(String id, int major, int minor, int revision, int tag);

    private native void n_listen_on(int range_start, int range_end) throws LibtorrentException;

    private native long n_add_torrent_url(String save_path, String url, int flags) throws LibtorrentException;

    private native long n_add_torrent_file(String save_path, String torrent_info_path, int flags) throws LibtorrentException;

    private native void n_remove_torrent(long handle_addr, boolean delete_files);

    //private native int[] n_get_torrents();
    private native void n_shutdown();

    private native void n_delete();

    private native Alert n_pop_alert();

    private native Alert n_wait_for_alert(long max_wait);

    private native void n_set_alert_mask(int mask);

}
