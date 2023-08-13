package com.patroclos.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CustomErrorController  {

	@RequestMapping("/error")
	public ModelAndView handleError(HttpServletRequest request) {
    	ModelAndView errorPage = new ModelAndView("error");
		String errorMsg = "";
		int httpErrorCode = getErrorCode(request);
		
		getError(request);

		switch (httpErrorCode) {
			case 400: {
				errorMsg = "Http Error Code: 400. Bad Request";
				break;
			}
			case 401: {
				errorMsg = "Http Error Code: 401. Unauthorized";
				break;
			}
			case 404: {
				errorMsg = "Http Error Code: 404. Resource not found";
				break;
			}
			case 500: {
				errorMsg = "Http Error Code: 500. Internal Server Error";
				break;
			}
		}
		
		if (httpErrorCode == HttpServletResponse.SC_FORBIDDEN)
			errorMsg = "Session expired please relogin";
		

		errorPage.addObject("errorMsg", errorMsg);
		return errorPage;
	}

	private int getErrorCode(HttpServletRequest httpRequest) {
		return (Integer) httpRequest
				.getAttribute(RequestDispatcher.ERROR_STATUS_CODE); //"jakarta.servlet.error.status_code");
	}
	
	private void getError(HttpServletRequest httpRequest) {
		Throwable throwable = (Throwable) httpRequest
				.getAttribute(RequestDispatcher.ERROR_EXCEPTION); //"jakarta.servlet.error.exception");
		var temp = 1;
	}
}