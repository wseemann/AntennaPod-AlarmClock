package de.danoeh.antennapod.service.download;

import de.danoeh.antennapod.bittorrent.LibtorrentException;
import de.danoeh.antennapod.bittorrent.Session;

/**
 * Provides access to a Session singleton. Classes that use this method should also increase the reference count to indicate that the session should
 * not be shut down and call shutdown to indicate that it is no longer used.
 */
public class BittorrentSessionProvider {
    private static volatile Session session;

    private static volatile int refCount = 0;

    public static synchronized Session getSession() throws LibtorrentException {
        if (session == null) {
            session = new Session("AP", 0, 9, 8, 3);
            session.listenOn(Session.DEFAULT_PORT_RANGE_START, Session.DEFAULT_PORT_RANGE_END);
        }
        return session;
    }

    public static synchronized void shutdownSession() {
        if (session != null) {
            if (refCount > 0) {
                refCount--;
            }
            if (refCount == 0) {
                session.shutdown();
                session = null;
            }
        }
    }

    public static synchronized void incrementRefCount() {
        refCount++;
    }
}
