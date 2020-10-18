package com.valiska.news_analysis.helper_objects;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Helper class that is used to store relevant topic keywords and references to headlines - {@link FeedEntry} and feeds - {@link Feed}.
 * The references are used to determine topic frequency and overlaps between feeds.
 *
 */
public class Topic {
	
	private String keyword;
	
	private Set<FeedEntry> feedEntries = new LinkedHashSet<>();
	private Set<Feed> feeds = new LinkedHashSet<>();
	
	public Topic(String keyword) {
		this.keyword = keyword;
	}
	
	/**
	 * Adds a reference to a {@link FeedEntry} (headline). The number of referenced headlines is then used to determine
	 * frequency of usage of this topic across feeds.
	 * 
	 * @param feedEntry referenced {@link FeedEntry}
	 */
	public void addFeedEntry(FeedEntry feedEntry) {
		feedEntries.add(feedEntry);
	}
	
	/**
	 * Adds a reference to a {@link Feed} (RSS feed). The number of referenced feeds is then used to determine
	 * the number of overlaps of this topic across feeds.
	 * 
	 * @param feed referenced {@link Feed}
	 */
	public void addFeed(Feed feed) {
		feeds.add(feed);
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	/**
	 * How many times is this topic referenced in headlines.
	 * 
	 * @return number of references
	 */
	public int getFrequency() {
		return feedEntries.size();
	}

	/**
	 * In how many RSS feeds is this topic referenced.
	 * 
	 * @return number of overlaps between feeds
	 */
	public int getOverlaps() {
		return feeds.size();
	}

	public Set<FeedEntry> getFeedEntries() {
		return Collections.unmodifiableSet(feedEntries);
	}

	public Set<Feed> getFeeds() {
		return Collections.unmodifiableSet(feeds);
	}

	@Override
	public String toString() {
		return keyword + ": " + getFrequency();
	}
}
