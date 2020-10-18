package com.valiska.news_analysis.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.rometools.rome.feed.synd.SyndFeed;
import com.valiska.news_analysis.AbstractAnalysisTest;
import com.valiska.news_analysis.helper_objects.Feed;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RSSFeedParserTests extends AbstractAnalysisTest{
	
	@Autowired
	private RSSFeedParser rssFeedParser;

	/**
	 * Parses the raw feeds into helper objects and checks if the correct relevant topics are created, according to "Stop words" list.
	 * Stop words are the words as "The", "is", "not" etc., that can be ignored when considering relevant topics. 
	 * 
	 * @throws IOException if failed to read Stop words file. 
	 */
	@Test
	@DisplayName("Parse titles into relevant topic names")
	void parseTitles() throws IOException {
		SyndFeed feed1 = createTestFeed(List.of(
				"Burundi I've On military killed in a able x 2018 News news",
				"Military Camps in Burundi Kill"
				));
		SyndFeed feed2 = createTestFeed(List.of(
				"Platini in I've motorcycle: FIFA ",
				"1st!!$12/mo"
				));
		
		List<Feed> feedItems = rssFeedParser.convertToFeedItems(List.of(feed1, feed2));
		
		assertThat(feedItems.get(0).getFeedEntries().get(0).getWords()).hasSameElementsAs(List.of(
				"Burundi", 
				"military", 
				"killed"
				));
		assertThat(feedItems.get(0).getFeedEntries().get(1).getWords()).hasSameElementsAs(List.of(
				"Military",
				"Camps",
				"Burundi", 
				"Kill"
				));
		assertThat(feedItems.get(1).getFeedEntries().get(0).getWords()).hasSameElementsAs(List.of(
				"Platini",
				"motorcycle",
				"FIFA"
				));
		assertThat(feedItems.get(1).getFeedEntries().get(1).getWords()).hasSameElementsAs(List.of(
				"1st",
				"$12/mo"
				));
	}

}
