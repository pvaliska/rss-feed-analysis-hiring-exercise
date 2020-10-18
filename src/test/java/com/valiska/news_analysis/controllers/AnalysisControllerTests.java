package com.valiska.news_analysis.controllers;

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
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.valiska.news_analysis.AbstractAnalysisTest;

import static org.springframework.http.MediaType.APPLICATION_JSON;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(MockServerExtension.class)
@MockServerSettings(ports = {8787, 8888})
class AnalysisControllerTests extends AbstractAnalysisTest{
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private WebTestClient webTestClient;
	
	
	/**
	 * Mocks the server client to return RSS feed files if the specified urls are called.
	 * Awaits to receive analysis Id and uses it then to fetch the analysis results.
	 * 
	 * @param client Mock Server Client
	 * @throws IOException
	 */
	@Test
	@DisplayName("Perform POST with rss links as input and then perform GET with returned analysis Id")
	void createAndGetAnalysis(MockServerClient client) throws IOException {
		
		mockServerClientWithFile(client, "classpath:com/valiska/news_analysis/google_rss.xml", "/google_news");
		mockServerClientWithFile(client, "classpath:com/valiska/news_analysis/msn_rss.xml", "/msn_news");
        
        webTestClient.post()
			.uri("/analyse/new")
			.bodyValue(List.of("http://localhost:8787/google_news", "http://localhost:8787/msn_news"))
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(HttpStatus.OK)
			.expectBody()
			.jsonPath("$.id").value(i -> assertAnalysis(i), String.class);
	}
	
	
	private void assertAnalysis(String id) {
		webTestClient.get()
			.uri("/frequency/" + id)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(HttpStatus.OK)
			.expectBody();
	}

	
}
