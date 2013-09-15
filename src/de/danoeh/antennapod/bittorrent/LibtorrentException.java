package de.danoeh.antennapod.bittorrent;

public class LibtorrentException extends Exception {

    public LibtorrentException() {
        super();
    }

    public LibtorrentException(String msg) {
        super(msg);
    }

    public LibtorrentException(Throwable cause) {
        super(cause);
    }
    
    public LibtorrentException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
