package com.valiska.news_analysis.helper_objects;

import java.util.Collections;
import java.util.List;

/**
 * Helper class that is used to search hot topics in RSS feeds. Represents single RSS feed.
 *
 */
public class Feed {
	
	private List<FeedEntry> feedEntries;

	public Feed(List<FeedEntry> feedEntries) {
		this.feedEntries = feedEntries;
	}

	public List<FeedEntry> getFeedEntries() {
		return Collections.unmodifiableList(feedEntries);
	}

}
