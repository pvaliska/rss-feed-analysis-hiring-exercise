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
import com.valiska.news_analysis.helper_objects.Topic;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class TopNewsPickerTests extends AbstractAnalysisTest{
	
	@Autowired
	private TopNewsPicker topNewsPicker;

	/**
	 * According to overlaps and Stop words, only single topic should be found.
	 * 
	 * @throws IOException
	 */
	@Test
	@DisplayName("Should find single relevant topic")
	void shouldReturnSingleRecord() throws IOException {
		SyndFeed feed1 = createTestFeed(List.of(
				"To Democrats, Donald Trump Is No Longer a Laughing Matter",
				"Burundi military1 sites attacked, 12 insurgents killed",
				"San Bernardino divers return to lake seeking electronic evidence"
				));
		SyndFeed feed2 = createTestFeed(List.of(
				"Attacks on Military Camps in Burundi Kill Eight",
				"Saudi Women to Vote for First Time",
				"Platini Dealt Further Blow in FIFA Presidency Bid"
				));
				
		List<Topic> top3Topics = topNewsPicker.pickTop3Topics(List.of(feed1, feed2));
		assertThat(top3Topics).hasSize(1);
		assertTopic(top3Topics.get(0), "burundi", 2, 2, "Burundi military1 sites attacked, 12 insurgents killed");
		
	}

	/**
	 * Checks if "blocking" is not found instead of "king" and vice versa.
	 * 
	 * @throws IOException
	 */
	@Test
	@DisplayName("Topic name is within another topic name")
	void shouldExtractWholeWords() throws IOException {
		SyndFeed feed1 = createTestFeed(List.of(
				"something blocking",
				"Sentence with king 1"
				));
		SyndFeed feed2 = createTestFeed(List.of(
				"somethingsomething king 2"
				));
		
		List<Topic> top3Topics = topNewsPicker.pickTop3Topics(List.of(feed1, feed2));
		assertThat(top3Topics).hasSize(1);
		assertTopic(top3Topics.get(0), "king", 2, 2, "Sentence with king 1");
		
	}
	
	/**
	 * According to overlaps and Stop words, three topic should be found.
	 * Checks the frequency, overlaps and exact order of the topics.
	 * 
	 * @throws IOException
	 */
	@Test
	@DisplayName("Should check frequency and exact order of topics")
	void shouldReturn3RecordsInExactOrder() throws IOException {
		SyndFeed feed1 = createTestFeed(List.of(
				"To Democrats, Donald Trump Is No Longer in a Laughing Matter",
				"Burundi a military sites attacked, 12 insurgents killed",
				"San Bernardino divers return to lake in seeking electronic evidence Attacks"
				));
		SyndFeed feed2 = createTestFeed(List.of(
				"Attacks a on Donald Camps in Burundi Kill Eight",
				"Saudi Women to Vote for First Time",
				"Donald Dealt a Further Blow in FIFA Presidency"
				));
				
		List<Topic> top3Topics = topNewsPicker.pickTop3Topics(List.of(feed1, feed2));
		assertThat(top3Topics).hasSize(3);
		assertTopic(top3Topics.get(0), "donald", 3, 2, "To Democrats, Donald Trump Is No Longer in a Laughing Matter");
		assertTopic(top3Topics.get(1), "burundi", 2, 2, "Burundi a military sites attacked, 12 insurgents killed");
		assertTopic(top3Topics.get(2), "attacks", 2, 2, "San Bernardino divers return to lake in seeking electronic evidence Attacks");
	}
	
	/**
	 * According to overlaps and Stop words, no topic should be found.
	 * 
	 * @throws IOException
	 */
	@Test
	@DisplayName("Should not find any topic")
	void thereIsNoHotTopic() throws IOException {
		SyndFeed feed1 = createTestFeed(List.of(
				"To Democrats, Donald Donald Donald Trump Is No Longer a Laughing Matter",
				"Bangladesh Donald Donald Donald military1 sites attacked, 12 insurgents killed",
				"San Bernardino divers return to lake seeking electronic evidence"
				));
		SyndFeed feed2 = createTestFeed(List.of(
				"Attacks on Military Camps in Burundi Kill Eight",
				"Saudi Women to Vote for First Time",
				"Platini Dealt Further Blow in FIFA Presidency Bid"
				));
				
		List<Topic> top3Topics = topNewsPicker.pickTop3Topics(List.of(feed1, feed2));
		assertThat(top3Topics).hasSize(0);
	}
	
	private void assertTopic(Topic topic, String text, int frequency, int overlaps, String title) {
		assertThat(topic.getKeyword()).isEqualTo(text);
		assertThat(topic.getFrequency()).isEqualTo(frequency);
		assertThat(topic.getOverlaps()).isEqualTo(overlaps);
		assertThat(topic.getFeedEntries().iterator().next().getHeader()).isEqualTo(title);
	}

}
