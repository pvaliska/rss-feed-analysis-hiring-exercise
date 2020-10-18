package com.valiska.news_analysis;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.valiska.news_analysis.controllers.AnalysisController;

@SpringBootTest
class NewsAnalysisApplicationTests {
	
	@Autowired
	private AnalysisController analysisController;

	@Test
	void contextLoads() {
		assertThat(analysisController).isNotNull();
	}

}
