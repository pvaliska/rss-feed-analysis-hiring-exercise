package com.valiska.news_analysis.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PersistenceTests {
	
	@Autowired
	private AnalysisRepository analysisRepository;
	
	@Autowired
	private AnalysisElementRepository analysisElementRepository;

	@Test
	@DisplayName("Save and load analysis")
	void createAnalysis() {
		Analysis analysis = analysisRepository.save(new Analysis());
		Analysis savedAnalysis = analysisRepository.findById(analysis.getId()).get();
		assertEquals(analysis.getId(), savedAnalysis.getId());
	}
	
	@Test
	@DisplayName("Save and load analysis with element")
	void createAnalysisElement() {
		Analysis analysis = analysisRepository.save(new Analysis());
		AnalysisElement analysisElement = new AnalysisElement(2, "Burundi", "Burundi military sites attacked, 12 insurgents killed", "http://localhost:8080/news", analysis.getId());
		analysisElementRepository.save(analysisElement);
		List<AnalysisElement> foundAnalysisElements = analysisElementRepository.findByAnalysisId(analysis.getId());
		assertThat(foundAnalysisElements).hasSize(1);
		assertAnalysisElementsEqual(foundAnalysisElements.get(0), analysisElement);
		
	}
	
	@Test
	@DisplayName("Save and load analysis with multiple elements")
	void createMoreAnalysisElements() {
		Analysis analysis = analysisRepository.save(new Analysis());
		AnalysisElement analysisElement1 = new AnalysisElement(3, "Burundi", "Burundi military sites attacked, 12 insurgents killed", "http://localhost:8080/news1", analysis.getId());
		AnalysisElement analysisElement2 = new AnalysisElement(2, "Donald", "To Democrats, Donald Trump Is No Longer a Laughing Matter", "http://localhost:8080/news2", analysis.getId());
		AnalysisElement analysisElement3 = new AnalysisElement(2, "Attacks", "Attacks on Donald Camps in Burundi Kill Eight", "http://localhost:8080/news3", analysis.getId());
		analysisElementRepository.saveAll(List.of(analysisElement1, analysisElement2, analysisElement3));
		List<AnalysisElement> foundAnalysisElements = analysisElementRepository.findByAnalysisId(analysis.getId());
		assertThat(foundAnalysisElements).hasSize(3);
		assertAnalysisElementsEqual(foundAnalysisElements.get(0), analysisElement1);
		assertAnalysisElementsEqual(foundAnalysisElements.get(1), analysisElement2);
		assertAnalysisElementsEqual(foundAnalysisElements.get(2), analysisElement3);
		
	}
	
	private void assertAnalysisElementsEqual(AnalysisElement real, AnalysisElement expected) {
		assertThat(real.getAnalysisId()).isEqualTo(expected.getAnalysisId());
		assertThat(real.getHeader()).isEqualTo(expected.getHeader());
		assertThat(real.getLink()).isEqualTo(expected.getLink());
		assertThat(real.getTopic()).isEqualTo(expected.getTopic());
		assertThat(real.getFrequency()).isEqualTo(expected.getFrequency());
	}

}
