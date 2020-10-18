package com.valiska.news_analysis.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.junit.jupiter.MockServerSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.valiska.news_analysis.AbstractAnalysisTest;
import com.valiska.news_analysis.exceptions.AtLeastTwoSeparateFeedsExpectedException;
import com.valiska.news_analysis.exceptions.BadRSSfeedException;
import com.valiska.news_analysis.persistence.AnalysisElement;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(MockServerExtension.class)
@MockServerSettings(ports = {8787, 8888})
class AnalysisMakerTests extends AbstractAnalysisTest {
	
	@Autowired
	private AnalysisMaker analysisMaker;
	
	@Autowired
	private AnalysisService analysisService;

	/**
	 * Mocks the server client to return RSS feed files if the specified urls are called.
	 * Checks if the exact number of elements have been saved, according to feed topics.
	 * 
	 * @param client Mock Server Client
	 * @throws IOException
	 */
	@Test
	@DisplayName("Create analysis of simplified rss feeds on Mock Server and check the size of returned elements")
	void testMockServerSimplified(MockServerClient client) throws IOException {
        
		mockServerClientWithFile(client, "classpath:com/valiska/news_analysis/google_rss_simplified.xml", "/google_news_simplified");
        
		mockServerClientWithFile(client, "classpath:com/valiska/news_analysis/msn_rss_simplified.xml", "/msn_news_simplified");
		 
		long analysisId = analysisMaker.createAnalysis(List.of("http://localhost:8787/google_news_simplified", "http://localhost:8787/msn_news_simplified")).getId();
		List<AnalysisElement> elements = analysisService.findAnalysisElementsByAnalysisId(analysisId);
		 
		assertThat(elements).hasSize(2);
	}
	
	/**
	 * Invalid RSS is a file with non valid rss format
	 * 
	 * @param client Mock Server Client
	 * @throws IOException
	 */
	@Test
	@DisplayName("Try to create analysis of invalid rss feeds on Mock Server and check the exception")
	void testMockServerInvalid(MockServerClient client) throws IOException {
        
		mockServerClientWithFile(client, "classpath:com/valiska/news_analysis/google_rss_simplified.xml", "/google_news_simplified");

		mockServerClientWithFile(client, "classpath:com/valiska/news_analysis/invalid_rss.xml", "/invalid_rss");
		 
        assertThrows(BadRSSfeedException.class, ()-> analysisMaker.createAnalysis(List.of("http://localhost:8787/google_news_simplified", "http://localhost:8787/invalid_rss")));
	}
	
	/**
	 * Mocks the server client to return RSS feed files if the specified urls are called.
	 * Checks if the exact number of elements have been saved, according to feed topics.
	 * 
	 * @param client Mock Server Client
	 * @throws IOException
	 */
	@Test
	@DisplayName("Create analysis of real rss feeds on Mock Server and check the size of returned elements")
	void testMockserverReal(MockServerClient client) throws IOException {
        
		mockServerClientWithFile(client, "classpath:com/valiska/news_analysis/google_rss.xml", "/google_news");
        
		mockServerClientWithFile(client, "classpath:com/valiska/news_analysis/msn_rss.xml", "/msn_news");
		 
		long analysisId = analysisMaker.createAnalysis(List.of("http://localhost:8787/google_news", "http://localhost:8787/msn_news")).getId();
		List<AnalysisElement> elements = analysisService.findAnalysisElementsByAnalysisId(analysisId);
		 
		assertThat(elements).hasSize(3);
	}
	
	
	/**
	 * Checks if the uniqueness of the links works.
	 * 
	 * @param client Mock Server Client
	 * @throws IOException
	 */
	@Test
	@DisplayName("Should not consider the same urls as 2 separate RSS feeds.")
	void testTheSameFeeds(MockServerClient client) throws IOException {
		
		mockServerClientWithFile(client, "classpath:com/valiska/news_analysis/google_rss.xml", "/google_news");
		
		assertThrows(AtLeastTwoSeparateFeedsExpectedException.class, ()-> analysisMaker.createAnalysis(List.of("http://localhost:8787/google_news", "http://localhost:8787/google_news")));
	}

}
