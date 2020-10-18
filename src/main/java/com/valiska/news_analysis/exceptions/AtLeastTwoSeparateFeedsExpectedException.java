package com.valiska.news_analysis.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="At least two unique RSS feed urls are expected.")
public class AtLeastTwoSeparateFeedsExpectedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

}
