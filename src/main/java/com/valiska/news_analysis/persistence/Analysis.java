package com.valiska.news_analysis.persistence;

import javax.persistence.*;

@Entity
public class Analysis {
	
	@Id
	@GeneratedValue
	private long id;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
