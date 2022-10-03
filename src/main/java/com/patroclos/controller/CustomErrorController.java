package com.patroclos.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CustomErrorController {

	@RequestMapping("/error")
	public ModelAndView handleError(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView errorPage = new ModelAndView("error");
		String errorMsg = "Oops something went wrong!";
		int httpErrorCode = getErrorCode(request);

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

		String errorMessage = (String) request
		.getAttribute("javax.servlet.error.message");

		Throwable throwable = (Throwable) request
				.getAttribute("javax.servlet.error.exception");
		Integer statusCode = (Integer) request
				.getAttribute("javax.servlet.error.status_code");
		String servletName = (String) request
				.getAttribute("javax.servlet.error.servlet_name");
		
		if (httpErrorCode == HttpServletResponse.SC_FORBIDDEN)
			errorMsg = "Session expired please relogin";
		

		errorPage.addObject("errorMsg", errorMsg);
		
		return errorPage;
	}

	private int getErrorCode(HttpServletRequest httpRequest) {
		return (Integer) httpRequest
				.getAttribute("javax.servlet.error.status_code");
	}

}