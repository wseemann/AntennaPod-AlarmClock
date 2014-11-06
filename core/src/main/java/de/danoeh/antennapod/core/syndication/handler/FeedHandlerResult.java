package de.danoeh.antennapod.core.syndication.handler;

import java.util.Map;

import de.danoeh.antennapod.core.feed.Feed;

/**
 * Container for results returned by the Feed parser
 */
public class FeedHandlerResult {

    public Feed feed;
    public Map<String, String> alternateFeedUrls;
    public String nextPage;
    public boolean nextPageIsLast;

    public FeedHandlerResult(Feed feed, Map<String, String> alternateFeedUrls, String nextPage, boolean nextPageIsLast) {
        this.feed = feed;
        this.alternateFeedUrls = alternateFeedUrls;
        this.nextPage = nextPage;
        this.nextPageIsLast = nextPageIsLast;
    }
}
