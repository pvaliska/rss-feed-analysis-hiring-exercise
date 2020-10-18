package com.valiska.news_analysis.persistence;

import javax.persistence.*;

@Entity
public class AnalysisElement {
	
	@Id
	@GeneratedValue
	private long id;
	
	private long analysisId;
	
	private int frequency;
	private String topic;
	private String header;
	@Column(length = 500)
	private String link;
	
	
	public AnalysisElement() {
		
	}
	
	public AnalysisElement(int frequency, String topic, String header, String link, long analysisId) {
		this.frequency = frequency;
		this.topic = topic;
		this.header = header;
		this.link = link;
		this.analysisId = analysisId;
	}
	
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getAnalysisId() {
		return analysisId;
	}

	public void setAnalysisId(long analysisId) {
		this.analysisId = analysisId;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	

}
