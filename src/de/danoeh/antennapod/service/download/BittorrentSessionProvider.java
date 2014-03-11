package de.danoeh.antennapod.service.download;

import de.danoeh.antennapod.bittorrent.LibtorrentException;
import de.danoeh.antennapod.bittorrent.Session;

/**
 * Provides access to a Session singleton
 */
public class BittorrentSessionProvider {
    private static volatile Session session;

    public static synchronized Session getSession() throws LibtorrentException {
        if (session == null) {
            session = new Session("AP", 0, 9, 8, 3);
            session.listenOn(Session.DEFAULT_PORT_RANGE_START, Session.DEFAULT_PORT_RANGE_END);
        }
        return session;
    }

    public static synchronized void shutdownSession() {
        if (session != null) {
            session.shutdown();
            session = null;
        }
    }
}
