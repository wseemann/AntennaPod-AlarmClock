package de.danoeh.antennapod.bittorrent;

import java.io.File;

public class TorrentHandle {

    private long nativeObj;

    public TorrentHandle(long nativeObj) {
        this.nativeObj = nativeObj;
    }

    @Override
    protected void finalize() throws Throwable{
        n_delete();
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == TorrentHandle.class) {
            return nativeObj == ((TorrentHandle)other).getNativeObj();
        }
        return false;
    }

    public long getNativeObj() {
        return nativeObj;
    }

    public String getName() {
        return n_get_name();
    }

    public void setSequential(boolean sd) {
        n_set_sequential(sd);
    }

    public void setUploadMode(boolean m) {
        n_set_upload_mode(m);
    }

    public void setShareMode(boolean m) {
        n_set_share_mode(m);
    }

    public void setPriority(int priority) {
        n_set_priority(priority);
    }

    public boolean isValid() {
        return n_is_valid();
    }

    public TorrentStatus getStatus() {
        return new TorrentStatus(n_get_status()); 
    }

    public int getNumFiles() {
        return n_num_files();
    }

    public File getFileAt(int index) {
        return new File(n_file_at(index));
    }

    public void renameFile(int index, String newFileName) {
        n_rename_file(index, newFileName);
    }

    private native String n_get_name();
    private native void n_set_sequential(boolean sd);
    private native void n_set_upload_mode(boolean m);
    private native void n_set_share_mode(boolean m);
    private native void n_set_priority(int priority);
    private native boolean n_is_valid();
    private native long n_get_status();
    private native void n_delete();
    private native int n_num_files();
    private native String n_file_at(int index);
    private native void n_rename_file(int index, String new_name);
}
