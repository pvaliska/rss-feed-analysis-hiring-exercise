package com.valiska.news_analysis.services;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.valiska.news_analysis.exceptions.BadRSSfeedException;


@Component
public class RSSFeedFetcher {
	
	/**
	 * Connects to urls and collects valid RSS feeds. Throws {@link BadRSSfeedException} if the url or RSS feed is not valid.
	 * 
	 * @param urls urls to RSS feeds
	 * @return fetched RSS feeds as {@link SyndFeed}
	 */
	public List<SyndFeed> fetch(Set<String> urls) {
		return urls.parallelStream()
			.map(url -> fetch(url))
			.collect(Collectors.toList());
	}
	
	private SyndFeed fetch(String url) {
		SyndFeedInput input = new SyndFeedInput();
		try {
			return input.build(new XmlReader(new URL(url)));
		} catch (IllegalArgumentException | FeedException | IOException e) {
			throw new BadRSSfeedException();
		} 
	}

}
