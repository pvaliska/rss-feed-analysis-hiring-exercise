package com.valiska.news_analysis.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.valiska.news_analysis.helper_objects.FeedEntry;
import com.valiska.news_analysis.helper_objects.Topic;
import com.valiska.news_analysis.persistence.Analysis;
import com.valiska.news_analysis.persistence.AnalysisElement;
import com.valiska.news_analysis.persistence.AnalysisElementRepository;
import com.valiska.news_analysis.persistence.AnalysisRepository;

@Component
public class AnalysisService {
	
	@Autowired
	private AnalysisRepository analysisRepository;
	
	@Autowired
	private AnalysisElementRepository analysisElementRepository;
	
	/**
	 * Converts {@link Topic}s into {@link AnalysisElement}s and saves them as {@link Analysis}
	 * 
	 * @param topics {@link Topic}s to be saved to analysis
	 * @return created {@link Analysis}
	 */
	public Analysis createAnalysis(List<Topic> topics) {
		Analysis analysis = createNewAnalysis();
		createNewAnalysisElements(topics, analysis);
		return analysis;
	}
	
	/**
	 * Finds {@link AnalysisElement}s based on {@link Analysis} id.
	 * 
	 * @param id analysis id
	 * @return found analysis elements
	 */
	public List<AnalysisElement> findAnalysisElementsByAnalysisId(long id){
		return analysisElementRepository.findByAnalysisId(id);
	}
	
	private AnalysisElement createAnalysisElement(int frequency, String topic, FeedEntry entry, long analysisId) {
		return new AnalysisElement(frequency, topic, entry.getHeader(), entry.getLink(), analysisId);
	}

	private void createNewAnalysisElements(List<Topic> topics, Analysis analysis) {
		for (Topic topic: topics) {
			FeedEntry feedEntry = topic.getFeedEntries().iterator().next();
			analysisElementRepository.save(createAnalysisElement(topic.getFrequency(), findWord(feedEntry, topic), feedEntry, analysis.getId()));
		}
	}

	private Analysis createNewAnalysis() {
		return analysisRepository.save(new Analysis());
	}
	
	/**
	 * Finds the exact word in {@link FeedEntry} that matches {@link Topic} keyword. Ignores Case-sensitivity.
	 * 
	 * @param entry RSS feed entry (item)
	 * @param topic topic with keyword
	 * @return found exact word in RSS feed entry
	 */
	private String findWord(FeedEntry entry, Topic topic) {
		return entry.getWords().stream()
				.filter(word -> topic.getKeyword().equals(word.toLowerCase()))
				.findFirst()
				.get();
	}
		

}
