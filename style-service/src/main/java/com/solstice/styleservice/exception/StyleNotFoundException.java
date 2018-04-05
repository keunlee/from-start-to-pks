package com.solstice.styleservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class StyleNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -142997897038522854L;

	public StyleNotFoundException(String errorMessage) {
		super(errorMessage);
	}
}
