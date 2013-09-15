package de.danoeh.antennapod.bittorrent;

public class TorrentAlert extends Alert {

    protected TorrentHandle handle;

    public TorrentAlert(int type, String message, long handleAddr) {
        super(type, message);
        this.handle = new TorrentHandle(handleAddr);
    }

    public TorrentHandle getHandle() {
        return handle;
    }
}
