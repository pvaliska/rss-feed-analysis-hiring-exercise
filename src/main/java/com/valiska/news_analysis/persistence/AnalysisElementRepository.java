package com.valiska.news_analysis.persistence;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.CrudRepository;

public interface AnalysisElementRepository extends CrudRepository<AnalysisElement, Long>{
	
	@Transactional(readOnly=true)
	List<AnalysisElement> findByAnalysisId(Long analysisId);
	

}
