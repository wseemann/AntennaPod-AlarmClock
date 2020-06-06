package de.danoeh.antennapod.core.alarm.deskclock;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.service.playback.PlaybackService;
import de.danoeh.antennapod.core.service.playback.PlayerStatus;
import de.danoeh.antennapod.core.storage.DBReader;
import de.danoeh.antennapod.core.util.playback.Playable;
import de.danoeh.antennapod.core.util.playback.PlaybackServiceStarter;

import static android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT;
import static android.media.AudioManager.STREAM_ALARM;

/**
 * <p>This class controls playback of ringtones. Uses {@link Ringtone} or {@link MediaPlayer} in a
 * dedicated thread so that this class can be called from the main thread. Consequently, problems
 * controlling the ringtone do not cause ANRs in the main thread of the application.</p>
 *
 * <p>This class also serves a second purpose. It accomplishes alarm ringtone playback using two
 * different mechanisms depending on the underlying platform.</p>
 *
 * <ul>
 *     <li>Prior to the M platform release, ringtone playback is accomplished using
 *     {@link MediaPlayer}. android.permission.READ_EXTERNAL_STORAGE is required to play custom
 *     ringtones located on the SD card using this mechanism. {@link MediaPlayer} allows clients to
 *     adjust the volume of the stream and specify that the stream should be looped.</li>
 *
 *     <li>Starting with the M platform release, ringtone playback is accomplished using
 *     {@link Ringtone}. android.permission.READ_EXTERNAL_STORAGE is <strong>NOT</strong> required
 *     to play custom ringtones located on the SD card using this mechanism. {@link Ringtone} allows
 *     clients to adjust the volume of the stream and specify that the stream should be looped but
 *     those methods are marked @hide in M and thus invoked using reflection. Consequently, revoking
 *     the android.permission.READ_EXTERNAL_STORAGE permission has no effect on playback in M+.</li>
 * </ul>
 *
 * <p>If either the {@link Ringtone} or {@link MediaPlayer} fails to play the requested audio, an
 * {@link #getFallbackRingtoneUri in-app fallback} is used because playing <strong>some</strong>
 * sort of noise is always preferable to remaining silent.</p>
 */
public class AsyncPreviewRingtonePlayer extends AsyncRingtonePlayer {

    AsyncPreviewRingtonePlayer(Context context) {
        super(context);
    }

    public boolean isPreview() {
        return true;
    }
}
