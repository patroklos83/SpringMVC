package com.patroclos.exception;

public class ValidationException extends SystemException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ValidationException(Exception e) {
		super(e);
	}

	public ValidationException(String message) {
		super(message);
	}

}
