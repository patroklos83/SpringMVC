package com.patroclos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ExceptionGlobalHandler {
	
	@ExceptionHandler(value = { SystemException.class })
	public ResponseEntity<String> SystemExceptionHandler(SystemException e) {
		HttpStatus status = HttpStatus.NOT_FOUND; // 404
		if (e.getCause() != null)
			return new ResponseEntity<String>(e.getCause().getMessage(), status);			
		return new ResponseEntity<String>(e.getMessage(), status);
	}
	
	@ExceptionHandler(value = { Exception.class })
	public ResponseEntity<String> ExceptionHandler(Exception e) {
		HttpStatus status = HttpStatus.NOT_FOUND; // 404
		return new ResponseEntity<String>(e.getMessage(), status);
	}
}