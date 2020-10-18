package com.valiska.news_analysis.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.valiska.news_analysis.persistence.Analysis;
import com.valiska.news_analysis.persistence.AnalysisElement;
import com.valiska.news_analysis.services.AnalysisMaker;
import com.valiska.news_analysis.services.AnalysisService;


@RestController
public class AnalysisController {
	
	@Autowired
	private AnalysisMaker analysisMaker;
	
	@Autowired
	private AnalysisService analysisService;
	
	
	/**
	 * Fetches the RSS feeds on given urls and creates {@link Analysis} with {@link AnalysisElement}s.
	 * 
	 * @param urls at least 2 unique urls are required 
	 * @return created {@link Analysis}
	 * @throws IOException 
	 */
	@RequestMapping(value="/analysis/new", method=RequestMethod.POST, 
			consumes=MediaType.APPLICATION_JSON_VALUE, 
			produces=MediaType.APPLICATION_JSON_VALUE)
	Analysis newAnalysis(@RequestBody List<String> urls) throws IOException {
		return analysisMaker.createAnalysis(urls);
	}
	
	/**
	 * Fetches analysis results {@link AnalysisElement} based on analysis ID
	 * @param id ID of the {@link Analysis}
	 * @return top 3 hot topics in news
	 */
	@GetMapping("/frequency/{id}")
	List<AnalysisElement> getFrequency(@PathVariable long id) {
		return analysisService.findAnalysisElementsByAnalysisId(id);
	}

}
