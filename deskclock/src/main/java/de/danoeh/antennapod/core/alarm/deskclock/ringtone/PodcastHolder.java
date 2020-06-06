/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.danoeh.antennapod.core.alarm.deskclock.ringtone;

import android.net.Uri;

import de.danoeh.antennapod.core.feed.FeedItem;

public class PodcastHolder extends RingtoneHolder {

    private FeedItem feedItem;

    PodcastHolder(Uri uri, String name, FeedItem feedItem) {
        super(uri, name);
        this.setFeedItem(feedItem);
    }

    @Override
    public int getItemViewType() {
        return RingtoneViewHolder.VIEW_TYPE_PODCAST;
    }

    public FeedItem getFeedItem() {
        return feedItem;
    }

    public void setFeedItem(FeedItem feedItem) {
        this.feedItem = feedItem;
    }
}
