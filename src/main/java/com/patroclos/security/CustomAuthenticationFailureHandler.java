package com.patroclos.security;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patroclos.model.ActivityLog;
import com.patroclos.process.ActivityProcess;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Autowired
    private ActivityProcess ActivityProcess;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {		
	    String userName = request.getParameter("username");
		try {
			ActivityProcess.loginFailActivity(request, userName, exception.getMessage());
			response.sendRedirect(request.getContextPath() + "/login?error");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}