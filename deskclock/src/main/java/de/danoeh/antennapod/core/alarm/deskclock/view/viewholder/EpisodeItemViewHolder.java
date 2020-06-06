package de.danoeh.antennapod.core.alarm.deskclock.view.viewholder;

import android.content.Context;
import android.os.Build;
import android.text.Layout;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import de.danoeh.antennapod.core.alarm.deskclock.R;
import de.danoeh.antennapod.core.alarm.deskclock.adapter.CoverLoader;
import de.danoeh.antennapod.core.alarm.deskclock.ringtone.PodcastHolder;
import de.danoeh.antennapod.core.alarm.deskclock.ringtone.RingtoneHolder;
import de.danoeh.antennapod.core.alarm.deskclock.ringtone.RingtoneViewHolder;
import de.danoeh.antennapod.core.event.PlaybackPositionEvent;
import de.danoeh.antennapod.core.feed.FeedItem;
import de.danoeh.antennapod.core.feed.FeedMedia;
import de.danoeh.antennapod.core.feed.MediaType;
import de.danoeh.antennapod.core.feed.util.ImageResourceUtils;
import de.danoeh.antennapod.core.util.Converter;
import de.danoeh.antennapod.core.util.DateUtils;
import de.danoeh.antennapod.core.util.NetworkUtils;
import de.danoeh.antennapod.core.util.ThemeUtils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Holds the view which shows FeedItems.
 */
public class EpisodeItemViewHolder extends RingtoneViewHolder {
    private static final String TAG = "EpisodeItemViewHolder";

    private final View container;
    private final TextView placeholder;
    private final ImageView cover;
    private final TextView title;
    private final TextView pubDate;
    private final TextView position;
    private final TextView duration;
    private final TextView size;
    public final TextView isNew;
    public final ImageView isInQueue;
    private final ImageView isVideo;
    public final ImageView isFavorite;
    private final ProgressBar progressBar;
    private final TextView separatorIcons;
    private final View leftPadding;
    private final View selectedView;
    public final CardView coverHolder;

    private final Context context;
    private FeedItem item;

    public EpisodeItemViewHolder(Context context, ViewGroup parent) {
        super(LayoutInflater.from(context).inflate(R.layout.feeditemlist_itemm, parent, false));
        this.context = context;
        container = itemView.findViewById(R.id.container);
        placeholder = itemView.findViewById(R.id.txtvPlaceholder);
        cover = itemView.findViewById(R.id.imgvCover);
        title = itemView.findViewById(R.id.txtvTitle);
        if (Build.VERSION.SDK_INT >= 23) {
            title.setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_FULL);
        }
        pubDate = itemView.findViewById(R.id.txtvPubDate);
        position = itemView.findViewById(R.id.txtvPosition);
        duration = itemView.findViewById(R.id.txtvDuration);
        progressBar = itemView.findViewById(R.id.progressBar);
        isInQueue = itemView.findViewById(R.id.ivInPlaylist);
        isVideo = itemView.findViewById(R.id.ivIsVideo);
        isNew = itemView.findViewById(R.id.statusUnread);
        isFavorite = itemView.findViewById(R.id.isFavorite);
        size = itemView.findViewById(R.id.size);
        separatorIcons = itemView.findViewById(R.id.separatorIcons);
        coverHolder = itemView.findViewById(R.id.coverHolder);
        leftPadding = itemView.findViewById(R.id.left_padding);
        selectedView = itemView.findViewById(R.id.sound_image_selected);
        itemView.setTag(this);
    }

    @Override
    protected void onBindItemView(RingtoneHolder itemHolder) {
        PodcastHolder holder = (PodcastHolder) itemHolder;
        bind(holder.getFeedItem());
        selectedView.setVisibility(itemHolder.isSelected() ? VISIBLE : GONE);
    }

    public void bind(FeedItem item) {
        this.item = item;
        placeholder.setText(item.getFeed().getTitle());
        title.setText(item.getTitle());
        leftPadding.setContentDescription(item.getTitle());
        pubDate.setText(DateUtils.formatAbbrev(context, item.getPubDate()));
        pubDate.setContentDescription(DateUtils.formatForAccessibility(context, item.getPubDate()));
        isNew.setVisibility(item.isNew() ? View.VISIBLE : View.GONE);
        isFavorite.setVisibility(item.isTagged(FeedItem.TAG_FAVORITE) ? View.VISIBLE : View.GONE);
        isInQueue.setVisibility(item.isTagged(FeedItem.TAG_QUEUE) ? View.VISIBLE : View.GONE);
        itemView.setAlpha(item.isPlayed() ? 0.6f : 1.0f);

        if (item.getMedia() != null) {
            bind(item.getMedia());
        } else {
            isVideo.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            duration.setVisibility(View.GONE);
            position.setVisibility(View.GONE);
        }

        if (coverHolder.getVisibility() == View.VISIBLE) {
            new CoverLoader(context)
                    .withUri(ImageResourceUtils.getImageLocation(item))
                    .withFallbackUri(item.getFeed().getImageLocation())
                    .withPlaceholderView(placeholder)
                    .withCoverView(cover)
                    .load();
        }
    }

    private void bind(FeedMedia media) {
        isVideo.setVisibility(media.getMediaType() == MediaType.VIDEO ? View.VISIBLE : View.GONE);
        duration.setVisibility(media.getDuration() > 0 ? View.VISIBLE : View.GONE);
        duration.setText(Converter.getDurationStringLong(media.getDuration()));
        duration.setContentDescription(context.getString(R.string.chapter_duration,
                Converter.getDurationStringLocalized(context, media.getDuration())));

        if (media.isCurrentlyPlaying()) {
            container.setBackgroundColor(ThemeUtils.getColorFromAttr(context, R.attr.currently_playing_background));
        } else {
            container.setBackgroundResource(ThemeUtils.getDrawableFromAttr(context, R.attr.selectableItemBackground));
        }

        if (item.getState() == FeedItem.State.PLAYING || item.getState() == FeedItem.State.IN_PROGRESS) {
            int progress = (int) (100.0 * media.getPosition() / media.getDuration());
            progressBar.setProgress(progress);
            position.setText(Converter.getDurationStringLong(media.getPosition()));
            position.setContentDescription(context.getString(R.string.position,
                    Converter.getDurationStringLocalized(context, media.getPosition())));
            progressBar.setVisibility(View.VISIBLE);
            position.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            position.setVisibility(View.GONE);
        }

        if (media.getSize() > 0) {
            size.setText(Formatter.formatShortFileSize(context, media.getSize()));
        } else if (NetworkUtils.isEpisodeHeadDownloadAllowed() && !media.checkedOnSizeButUnknown()) {
            size.setText("{fa-spinner}");
            //Iconify.addIcons(size);
            NetworkUtils.getFeedMediaSizeObservable(media).subscribe(
                    sizeValue -> {
                        if (sizeValue > 0) {
                            size.setText(Formatter.formatShortFileSize(context, sizeValue));
                        } else {
                            size.setText("");
                        }
                    }, error -> {
                        size.setText("");
                        Log.e(TAG, Log.getStackTraceString(error));
                    });
        } else {
            size.setText("");
        }
    }

    public FeedItem getFeedItem() {
        return item;
    }

    public boolean isCurrentlyPlayingItem() {
        return item.getMedia() != null && item.getMedia().isCurrentlyPlaying();
    }

    public void notifyPlaybackPositionUpdated(PlaybackPositionEvent event) {
        progressBar.setProgress((int) (100.0 * event.getPosition() / event.getDuration()));
        position.setText(Converter.getDurationStringLong(event.getPosition()));
        duration.setText(Converter.getDurationStringLong(event.getDuration()));
        duration.setVisibility(View.VISIBLE); // Even if the duration was previously unknown, it is now known
    }

    /**
     * Hides the separator dot between icons and text if there are no icons.
     */
    public void hideSeparatorIfNecessary() {
        boolean hasIcons = isNew.getVisibility() == View.VISIBLE
                || isInQueue.getVisibility() == View.VISIBLE
                || isVideo.getVisibility() == View.VISIBLE
                || isFavorite.getVisibility() == View.VISIBLE
                || isNew.getVisibility() == View.VISIBLE;
        separatorIcons.setVisibility(hasIcons ? View.VISIBLE : View.GONE);
    }
}
