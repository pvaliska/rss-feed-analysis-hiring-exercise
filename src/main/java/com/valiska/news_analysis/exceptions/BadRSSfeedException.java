package com.valiska.news_analysis.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="One of the urls didn't provide valid RSS feed.")
public class BadRSSfeedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

}
