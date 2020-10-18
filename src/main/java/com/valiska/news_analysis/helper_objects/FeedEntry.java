package com.valiska.news_analysis.helper_objects;

import java.util.Collections;
import java.util.List;

/**
 * Helper class that is used to search hot topics in RSS feeds. Represents single RSS feed entry (item).
 *
 */
public class FeedEntry {
	
	private String header;
	private String link;
	private List<String> words;
	
	public FeedEntry(String header, String link) {
		this.header = header;
		this.link = link;
	}

	public List<String> getWords() {
		return Collections.unmodifiableList(words);
	}
	public void setWords(List<String> words) {
		this.words = words;
	}

	public String getHeader() {
		return header;
	}

	public String getLink() {
		return link;
	}

}
