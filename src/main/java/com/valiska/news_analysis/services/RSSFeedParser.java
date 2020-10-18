package com.valiska.news_analysis.services;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.valiska.news_analysis.helper_objects.Feed;
import com.valiska.news_analysis.helper_objects.FeedEntry;

@Component
public class RSSFeedParser {
	
	/**
	 * Reads "Stop words" from the file. Stop words are used to ignore non relevant words as "The", "is", "not" etc.
	 * Then using Stop words, defines the relevant topic keywords in each feed an converts to {@link Feed} helper objects.
	 * 
	 * @param feeds raw feeds
	 * @return converted {@link Feed} objects widh keywords.
	 * @throws IOException in case of error by reading the Stop words.
	 */
	public List<Feed> convertToFeedItems(List<SyndFeed> feeds) throws IOException{
		List<String> stopWords = Files.readAllLines(ResourceUtils.getFile("classpath:stop-words_english.txt").toPath());
		return feeds.parallelStream()
				.map(feed -> createFeedItem(feed, stopWords))
				.collect(Collectors.toList());
	}
	
	private Feed createFeedItem(SyndFeed feed, List<String> stopWords){
		List<FeedEntry> newsItems = feed.getEntries().stream()
			.map(entry -> createFeedEntryItem(entry, stopWords))
			.collect(Collectors.toList());
		return new Feed(newsItems);
	}
	
	private FeedEntry createFeedEntryItem(SyndEntry entry, List<String> stopWords) {
		FeedEntry result = new FeedEntry(entry.getTitle(), entry.getLink());
		result.setWords(splitByWords(entry.getTitle(), stopWords));
		return result;
	}
	
	/**
	 * Determines if the word is found in Stop words, ignoring Case-sensitivity.
	 * 
	 * @param word searched keyword
	 * @param stopWords the list of non relevant words as "The", "is", "not" etc.
	 * @return true if the word has been found in Stop words
	 */
	private boolean isStopWord(String word, List<String> stopWords) {
		return stopWords.stream()
				.anyMatch(stopWord -> stopWord.equals(word.toLowerCase()));
	}

	/**
	 * Splits the text by word delimiters like empty space, comma and exclamation mark... 
	 * Ignores short 1-letter words, non relevant words (stop words) and numbers.
	 * 
	 * @param text text to be split
	 * @param stopWords the list of non relevant words as "The", "is", "not" etc.
	 * @return relevant keywords
	 */
	private List<String> splitByWords(String text, List<String> stopWords) {
		String[] split = text.split("[\\s,.!?:]+");
		return Arrays.asList(split).stream()
			.filter(word -> word.length() > 1)
			.filter(word -> !isStopWord(word, stopWords))
			.filter(word -> !NumberUtils.isCreatable(word))
			.collect(Collectors.toList());
		
	}
}
