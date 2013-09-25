package instrumentationTest.de.test.antennapod.bittorrent;

import android.test.InstrumentationTestCase;
import de.danoeh.antennapod.bittorrent.Alert;
import de.danoeh.antennapod.bittorrent.LibtorrentException;
import de.danoeh.antennapod.bittorrent.Session;
import de.danoeh.antennapod.bittorrent.TorrentHandle;

import java.io.File;

/**
 * Test class for Session.
 */
public class SessionTest extends InstrumentationTestCase {
    private static final String DIR_NAME = "test";
    private File destFolder;

    private final String TEST_URL = "http://releases.ubuntu.com/13.04/ubuntu-13.04-desktop-amd64.iso.torrent";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        assertTrue(Session.isTorrentLibAvailable());
        destFolder = getInstrumentation().getTargetContext().getExternalFilesDir(DIR_NAME);
        assertNotNull(destFolder);
        assertTrue(destFolder.exists());
        assertTrue(destFolder.canWrite());
        assertTrue(destFolder.canRead());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        assertNotNull(destFolder);
        File[] files = destFolder.listFiles();
        assertNotNull(files);
        for (File f : files) {
            assertTrue(f.delete());
        }
    }

    public void testInit() {
        new Session();
    }

    public void testInitFingerprint() {
        new Session("AP", 0, 9, 8, 0);
    }

    public void testListenOn() throws LibtorrentException {
        Session s = new Session();
        s.listenOn(Session.DEFAULT_PORT_RANGE_START, Session.DEFAULT_PORT_RANGE_END);
    }

    public void testAddTorrentUrl() throws LibtorrentException, InterruptedException {
        Session s = new Session("AP", 0, 9, 8, 0);
        s.listenOn(Session.DEFAULT_PORT_RANGE_START, Session.DEFAULT_PORT_RANGE_END);
        TorrentHandle h = s.addTorrent(destFolder.getAbsolutePath(), TEST_URL, Session.ADD_TORRENT_AUTO_MANAGED);
        assertNotNull(h);
        s.shutdown();
        Thread.sleep(3000);
    }

    public void testRemoveTorrent() throws LibtorrentException, InterruptedException {
        Session s = new Session("AP", 0, 9, 8, 0);
        s.listenOn(Session.DEFAULT_PORT_RANGE_START, Session.DEFAULT_PORT_RANGE_END);
        TorrentHandle h = s.addTorrent(destFolder.getAbsolutePath(), TEST_URL, Session.ADD_TORRENT_AUTO_MANAGED);
        s.removeTorrent(h, false);
        Thread.sleep(3000);
        s.shutdown();
    }

    public void testPopAlert() throws LibtorrentException, InterruptedException {
        Session s = new Session("AP", 0, 9, 8, 0);
        s.listenOn(Session.DEFAULT_PORT_RANGE_START, Session.DEFAULT_PORT_RANGE_END);
        TorrentHandle h = s.addTorrent(destFolder.getAbsolutePath(), TEST_URL, Session.ADD_TORRENT_AUTO_MANAGED);
        s.removeTorrent(h, false);
        Thread.sleep(3000);
        for (int i = 0; i < 20; i++) {
            s.popAlert();
        }
    }

    public void testWaitForAlert() throws LibtorrentException, InterruptedException {
        Session s = new Session("AP", 0, 9, 8, 0);
        s.listenOn(Session.DEFAULT_PORT_RANGE_START, Session.DEFAULT_PORT_RANGE_END);
        TorrentHandle h = s.addTorrent(destFolder.getAbsolutePath(), TEST_URL, Session.ADD_TORRENT_AUTO_MANAGED);
        s.removeTorrent(h, false);
        for (int i = 0; i < 5; i++) {
            Alert a = s.waitForAlert(1000);
            if (a != null) {
                assertTrue(s.popAlert().equals(a));
            }
        }
    }

    public void testSetAlertMask() {
        Session s = new Session();
        s.setAlertMask(Session.ALERTMASK_ERROR | Session.ALERTMASK_STORAGE);
    }
}
