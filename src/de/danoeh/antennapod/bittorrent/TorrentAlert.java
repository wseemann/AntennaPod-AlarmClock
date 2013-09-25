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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        TorrentAlert that = (TorrentAlert) o;

        if (handle != null ? !handle.equals(that.handle) : that.handle != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (handle != null ? handle.hashCode() : 0);
        return result;
    }
}
