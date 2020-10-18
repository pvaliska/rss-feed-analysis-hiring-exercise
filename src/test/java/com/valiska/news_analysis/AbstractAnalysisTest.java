package com.valiska.news_analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.HttpStatusCode;
import org.springframework.util.ResourceUtils;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;

public abstract class AbstractAnalysisTest {
	
	/**
	 * In given {@link MockServerClient}, creates a rule that returns file with given file name, 
	 * if the address "http://localhost:8787/urlPath" is called.
	 * 
	 * @param client mock server client
	 * @param fileName name of the file in resources
	 * @param urlPath path used to fetch the file from server
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	protected void mockServerClientWithFile(MockServerClient client, String fileName, String urlPath)
			throws FileNotFoundException, IOException {
		
		File rssFeed = ResourceUtils.getFile(fileName);
        String rssContent = new String(Files.readAllBytes(rssFeed.toPath()));
        
		client
	    	.when(new HttpRequest().withPath(urlPath))
	    	.respond(new HttpResponse()
	            .withStatusCode(HttpStatusCode.OK_200.code())
	            .withBody(rssContent)
	    	);
	}
	
	/**
	 * Creates raw testing feed entry with title. 
	 * 
	 * @param title feed entry title
	 * @return raw feed entry
	 */
	protected SyndEntry createTestEntry(String title) {
		SyndEntry result = new SyndEntryImpl();
		result.setTitle(title);
		return result;
	}
	
	/**
	 * Creates raw testing feed and feed entries with titles.
	 * 
	 * @param titles list of titles to be used to create feed entries
	 * @return raw feed 
	 */
	protected SyndFeed createTestFeed(List<String> titles) {
		SyndFeed result = new SyndFeedImpl();
		List<SyndEntry> entries = titles.stream()
				.map(this::createTestEntry)
				.collect(Collectors.toList());
		result.setEntries(entries);
		return result;
	}

}
