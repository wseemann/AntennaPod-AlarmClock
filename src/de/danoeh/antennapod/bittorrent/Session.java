package de.danoeh.antennapod.bittorrent;

import java.io.File;

public class Session {
    
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
        ADD_TORRENT_UPDATE_SUBSCRIBE = 0x200;
    
    private long nativeObj;

    public Session() {
        nativeObj = n_session();
    }

    public void listenOn(int rangeStart, int rangeEnd) throws LibtorrentException {
       n_listen_on(rangeStart, rangeEnd); 
    }

    public TorrentHandle addTorrent(String savePath, String url, int flags) throws LibtorrentException {
        return new TorrentHandle(n_add_torrent_url(savePath, url, flags)); 
    }

    public TorrentHandle addTorrent(String savePath, File torrentInfo, int flags) throws LibtorrentException {
        return new TorrentHandle(n_add_torrent_file(savePath, torrentInfo.getAbsolutePath(), flags));
    }

    public void removeTorrent(TorrentHandle handle) {
        n_remove_torrent(handle.getNativeObj());
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

    public Alert popAlert() {
        return n_pop_alert(); 
    }

    public Alert waitForAlert(long maxWait) {
        return n_wait_for_alert(maxWait);
    }

    public void shutdown() {
        n_shutdown();
    }

    @Override
    protected void finalize() throws Throwable {
        n_delete(); 
    }

    private native long n_session();
    private native void n_listen_on(int range_start, int range_end) throws LibtorrentException;
    private native long n_add_torrent_url(String save_path, String url, int flags) throws LibtorrentException;
    private native long n_add_torrent_file(String save_path, String torrent_info_path, int flags) throws LibtorrentException;
    private native void n_remove_torrent(long handle_addr);
    private native int[] n_get_torrents();
    private native void n_shutdown();
    private native void n_delete();
    private native Alert n_pop_alert();
    private native Alert n_wait_for_alert(long max_wait); 

}
