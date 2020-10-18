package com.valiska.news_analysis.services;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rometools.rome.feed.synd.SyndFeed;
import com.valiska.news_analysis.exceptions.AtLeastTwoSeparateFeedsExpectedException;
import com.valiska.news_analysis.helper_objects.Topic;
import com.valiska.news_analysis.persistence.Analysis;

import java.io.IOException;

@Component
public class AnalysisMaker {
	
	@Autowired
	private RSSFeedFetcher rssFeedFetcher;
	
	@Autowired
	private TopNewsPicker topNewsPicker;
	
	@Autowired
	private AnalysisService analysisService;
	
	/**
	 * Checks the urls (at least 2 allowed), remove duplicate urls, fetches the remote RSS feeds, picks 3 hottest topics
	 * and saves into the database.
	 * 
	 * @param urls
	 * @return
	 * @throws IOException
	 */
	public Analysis createAnalysis(List<String> urls) throws IOException {
		Set<String> uniqueUrls = removeDuplicities(urls);
		if (checkUrlsCount(uniqueUrls)) {
			List<SyndFeed> feeds = rssFeedFetcher.fetch(uniqueUrls);
			List<Topic> top3Topics = topNewsPicker.pickTop3Topics(feeds);
			return analysisService.createAnalysis(top3Topics);
		}
		throw new AtLeastTwoSeparateFeedsExpectedException();
	}
	
	private Set<String> removeDuplicities(List<String> urls) {
		return urls.stream()
				.distinct()
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}
	
	private boolean checkUrlsCount(Set<String> urls) {
		return urls.size() >= 2;
	}
	
}
