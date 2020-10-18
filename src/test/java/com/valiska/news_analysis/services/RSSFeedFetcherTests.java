package com.valiska.news_analysis.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.junit.jupiter.MockServerSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.rometools.rome.feed.synd.SyndFeed;
import com.valiska.news_analysis.AbstractAnalysisTest;
import com.valiska.news_analysis.exceptions.BadRSSfeedException;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(MockServerExtension.class)
@MockServerSettings(ports = {8787, 8888})
class RSSFeedFetcherTests extends AbstractAnalysisTest{
	
	@Autowired
	private RSSFeedFetcher rssFeedFetcher;
	
	@Test
	@DisplayName("Should throw exception if RSS feed does not exist")
	void testNonExistingFeed() {
		assertThrows(BadRSSfeedException.class, ()-> rssFeedFetcher.fetch(Set.of("http://localhost:8787/non_existing_feed")));
	}
	
	/**
	 * Mocks the server client to return RSS feed files if the specified urls are called.
	 * Checks if invalid feed is recognized.
	 * 
	 * @param client Mock Server Client
	 * @throws IOException
	 */
	@Test
	@DisplayName("Should throw exception when receiving not valid RSS feed")
	void testInvalidFeed(MockServerClient client) throws IOException {
		
		mockServerClientWithFile(client, "classpath:com/valiska/news_analysis/invalid_rss.xml", "/invalid_feed");
		
		assertThrows(BadRSSfeedException.class, ()-> rssFeedFetcher.fetch(Set.of("http://localhost:8787/invalid_feed")));
	}
	
	/**
	 * Mocks the server client to return RSS feed files if the specified urls are called.
	 * Checks if the correct RSS feeds is fetched.
	 * 
	 * @param client Mock Server Client
	 * @throws IOException
	 */
	@Test
	@DisplayName("Should fetch existing RSS feed from Mock Server")
	void testExistingFeed(MockServerClient client) throws IOException {
		
        mockServerClientWithFile(client, "classpath:com/valiska/news_analysis/google_rss.xml", "/google_news");
        
        List<SyndFeed> fetch = rssFeedFetcher.fetch(Set.of("http://localhost:8787/google_news"));
		assertThat(fetch).hasSize(1);
		assertThat(fetch.get(0).getLink()).startsWith("https://news.google.com");
	}
	
	/**
	 * Mocks the server client to return RSS feed files if the specified urls are called.
	 * Checks if the correct RSS feeds are fetched in correct order.
	 * 
	 * @param client Mock Server Client
	 * @throws IOException
	 */
	@Test
	@DisplayName("Should fetch multiple RSS feeds from Mock Server")
	void testMultipleFeeds(MockServerClient client) throws IOException {
        
		mockServerClientWithFile(client, "classpath:com/valiska/news_analysis/google_rss.xml", "/google_news");
        
		mockServerClientWithFile(client, "classpath:com/valiska/news_analysis/msn_rss.xml", "/msn_news");
		
		Set<String> urls = new LinkedHashSet<>();
		urls.add("http://localhost:8787/google_news");
		urls.add("http://localhost:8787/msn_news");
		
		List<SyndFeed> fetch = rssFeedFetcher.fetch(urls);
		assertThat(fetch).hasSize(2);
		assertThat(fetch.get(0).getLink()).startsWith("https://news.google.com");
		assertThat(fetch.get(1).getLink()).startsWith("https://www.msn.com");
	}

}
