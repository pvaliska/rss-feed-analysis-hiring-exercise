package com.valiska.news_analysis.services;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.valiska.news_analysis.helper_objects.Feed;
import com.valiska.news_analysis.helper_objects.FeedEntry;
import com.valiska.news_analysis.helper_objects.Topic;
import com.valiska.news_analysis.persistence.AnalysisElement;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AnalysisServiceTests {
	
	@Autowired
	private AnalysisService analysisService;

	/**
	 * Checks if the correct keywords and other attributes are correctly saved.
	 */
	@Test
	@DisplayName("Test single topic fields with one headline")
	void testSingle() {
		FeedEntry feedEntry = new FeedEntry("McDonalds bancrupts", "some.link.com");
		feedEntry.setWords(List.of("McDonalds", "bancrupts"));
		Feed feed = new Feed(List.of(feedEntry));
		
		Topic topic = new Topic("mcdonalds");
		topic.addFeedEntry(feedEntry);
		topic.addFeed(feed);
		
		long analysisId = analysisService.createAnalysis(List.of(topic)).getId();
		
		List<AnalysisElement> elements = analysisService.findAnalysisElementsByAnalysisId(analysisId);
		
		assertThat(elements).hasSize(1);
		assertAnalysisElement(elements.get(0), "McDonalds", 1, "McDonalds bancrupts", "some.link.com");
	}
	
	/**
	 * Checks if the correct keywords and other attributes are correctly saved.
	 */
	@Test
	@DisplayName("Test frequency of the topic in headlines")
	void testMultiple() {
		FeedEntry feedEntry1 = new FeedEntry("McDonalds bancrupts", "some.link.com");
		FeedEntry feedEntry2 = new FeedEntry("No one knows McDonalds", "some.link.com");
		feedEntry1.setWords(List.of("McDonalds", "bancrupts"));
		feedEntry2.setWords(List.of("knows", "McDonalds"));
		Feed feed = new Feed(List.of(feedEntry1, feedEntry2));
		
		Topic topic = new Topic("mcdonalds");
		topic.addFeedEntry(feedEntry1);
		topic.addFeedEntry(feedEntry2);
		topic.addFeed(feed);
		
		long analysisId = analysisService.createAnalysis(List.of(topic)).getId();
		
		List<AnalysisElement> elements = analysisService.findAnalysisElementsByAnalysisId(analysisId);
		
		assertThat(elements).hasSize(1);
		assertAnalysisElement(elements.get(0), "McDonalds", 2, "McDonalds bancrupts", "some.link.com");
	}

	private void assertAnalysisElement(AnalysisElement analysisElement, String topic, int frequency, String header, String link) {
		assertThat(analysisElement.getFrequency()).isEqualTo(frequency);
		assertThat(analysisElement.getHeader()).isEqualTo(header);
		assertThat(analysisElement.getLink()).isEqualTo(link);
		assertThat(analysisElement.getTopic()).isEqualTo(topic);
	}

}
