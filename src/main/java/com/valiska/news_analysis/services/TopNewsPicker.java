package com.valiska.news_analysis.services;

import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rometools.rome.feed.synd.SyndFeed;
import com.valiska.news_analysis.helper_objects.Feed;
import com.valiska.news_analysis.helper_objects.FeedEntry;
import com.valiska.news_analysis.helper_objects.Topic;

@Component
public class TopNewsPicker {
	
	@Autowired
	private RSSFeedParser rssFeedParser;
	
	/**
	 * Parses the feeds into {@link Feed} helper objects, then extracts all topics from feed entries and finds 3 hottest topics
	 * based on topic frequency and overlaps.
	 *  
	 * @param feeds list of raw feeds
	 * @return list of hottest topics - {@link Topic}
	 * @throws IOException
	 */
	public List<Topic> pickTop3Topics(List<SyndFeed> feeds) throws IOException{
		List<Feed> items = rssFeedParser.convertToFeedItems(feeds);
		return pickTop3HottestTopics(items);
	}
	
	/**
	 * First extracts all topics, then selects those with at least two overlaps between feeds and sorts by frequency.
	 * Then selects top 3.
	 * 
	 * @param feeds list of raw feeds
	 * @return list of 3 hottest topics - {@link Topic}
	 * @throws IOException
	 */
	private List<Topic> pickTop3HottestTopics(List<Feed> feeds) throws IOException {
		List<Topic> allTopics = extractAllTopics(feeds);
		return allTopics.stream()
				.filter(topic -> topic.getOverlaps() > 1)
				.sorted(comparing(Topic::getFrequency, reverseOrder()))
				.limit(3)
				.collect(toList());
	}
	
	private List<Topic> extractAllTopics(List<Feed> feeds){
		List<Topic> topics = new ArrayList<>();
		for (Feed feed: feeds) {
			extractTopics(feed, topics);
		}
		return topics;
	}
	
	private List<Topic> extractTopics(Feed feed, List<Topic> existingTopics){
		for (FeedEntry feedEntries: feed.getFeedEntries()) {
			extractTopics(feed, feedEntries, existingTopics);
		}
		return existingTopics;
	}
	
	/**
	 * From all words in {@link FeedEntry}, creates topics - {@link Topic} with keyword and adds reference to the {@link Feed} and {@link FeedEntry}.
	 * 
	 * @param feed feed to be referenced in {@link Topic}
	 * @param feedEntry feed entry to be referenced in {@link Topic}
	 * @param existingTopics the list of already defined topics
	 * @return the list of all extracted topics
	 */
	private List<Topic> extractTopics(Feed feed, FeedEntry feedEntry, List<Topic> existingTopics){
		for (String word: feedEntry.getWords()) {
			Topic topic = getOrCreateTopic(word, existingTopics);
			topic.addFeedEntry(feedEntry);
			topic.addFeed(feed);
		}
		return existingTopics;
	}

	private Topic getOrCreateTopic(String word, List<Topic> existingTopics) {
		Optional<Topic> existingTopic = find(word, existingTopics);
		if (existingTopic.isPresent()) {
			return existingTopic.get();
		} else {
			Topic topic = new Topic(word.toLowerCase());
			existingTopics.add(topic);
			return topic;
		}
	}
	
	/**
	 * Finds the word in the list of existing topics ignoring Case-sensitivity
	 * 
	 * @param word to be found in topics
	 * @param topics the list of existing topics
	 * @return Optional of the found topic
	 */
	private Optional<Topic> find(String word, List<Topic> topics) {
		return topics.stream()
				.filter(topic -> word.toLowerCase().equals(topic.getKeyword()))
				.findFirst();
	}

}
