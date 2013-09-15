package de.danoeh.antennapod.bittorrent;

public class TorrentStatus {

    private long nativeObj;

    public TorrentStatus(long nativeObj) {
        this.nativeObj = nativeObj;
    }

    @Override
        protected void finalize() throws Throwable {
            n_delete();
        }

    @Override
        public String toString() {
            return "TorrentStatus[paused: " + paused()
                + "; seeding: " + seeding()
                + "; seq. download: " + sequentialDownload()
                + "; progress: " + progress()
                + "; finished: " + finished() + "]";
        }

    public TorrentStatus.State state() {
        return TorrentStatus.State.fromInteger(n_state());
    }

    public boolean paused() {
        return n_paused();
    }

    public boolean autoManaged() {
        return n_auto_managed();
    }

    public boolean sequentialDownload() {
        return n_sequential_download();
    }

    public boolean seeding() {
        return n_seeding();
    }

    public boolean finished() {
        return n_finished();
    }

    public float progress() {
        return n_progress();
    }

    public long totalDownload() {
        return n_total_download();
    }

    public long totalUpload() {
        return n_total_upload();
    }

    public int downloadRate() {
        return n_download_rate();
    }

    public int uploadRate() {
        return n_upload_rate();
    }

    public long totalDone() {
        return n_total_done();
    }

    public long totalWantedDone() {
        return n_total_wanted_done();
    }

    public long totalWanted() {
        return n_total_wanted();
    }

    private native int n_state();
    private native boolean n_paused();
    private native boolean n_auto_managed();
    private native boolean n_sequential_download();
    private native boolean n_seeding();
    private native boolean n_finished();
    private native float n_progress();
    private native long n_total_download();
    private native long n_total_upload();
    private native int n_download_rate();
    private native int n_upload_rate();
    private native long n_total_done();
    private native long n_total_wanted_done();
    private native long n_total_wanted();
    private native void n_delete();


    public static enum State {
        QUEUED_FOR_CHECKING,
            CHECKING_FILES,
            DOWNLOADING_METADATA,
            DOWNlOADING,
            FINISHED,
            SEEDING,
            ALLOCATING,
            CHECKING_RESUME_DATA;

        public static State fromInteger(int i) {
            switch(i) {
                case 0:
                    return QUEUED_FOR_CHECKING;
                case 1:
                    return CHECKING_FILES;
                case 2:
                    return DOWNLOADING_METADATA;
                case 3:
                    return DOWNlOADING;
                case 4:
                    return FINISHED;
                case 5:
                    return SEEDING;
                case 6:
                    return ALLOCATING;
                case 7:
                    return CHECKING_RESUME_DATA;
                default:
                    return null;
            }
        }
    }
}
