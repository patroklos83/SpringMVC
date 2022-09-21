package com.patroclos.exception;

public class SystemException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public SystemException(String message) {
		super(message);
	}
	
	public SystemException(Exception e) {
		super(e);
	}

}
