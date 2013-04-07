package de.danoeh.antennapod.syndication.namespace;

import org.xml.sax.Attributes;

import de.danoeh.antennapod.feed.EnclosedFeedMedia;
import de.danoeh.antennapod.syndication.handler.HandlerState;

public class NSFeedburner extends Namespace {
	public static final String NSTAG = "feedburner";
	public static final String NSURI = "http://rssnamespace.org/feedburner/ext/1.0";
	
	public static final String ORIG_ENCLOSURE = "origEnclosureLink";
	
	@Override
	public SyndElement handleElementStart(String localName, HandlerState state,
			Attributes attributes) {
		return new SyndElement(localName, this);
	}

	@Override
	public void handleElementEnd(String localName, HandlerState state) {
		if (localName.equals(ORIG_ENCLOSURE)) {
			if (state.getCurrentItem() != null && state.getCurrentItem().hasMedia()) {
				if (state.getCurrentItem().getMedia() instanceof EnclosedFeedMedia) {
					EnclosedFeedMedia media = (EnclosedFeedMedia) state.getCurrentItem().getMedia();
					media.setOriginalEnclosureLink(state.getContentBuf().toString());
				}
			}
		}
	}

}
