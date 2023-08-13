package com.patroclos.security;

import java.io.IOException;
import java.time.Instant;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.patroclos.model.User;
import com.patroclos.process.ActivityProcess;
import com.patroclos.service.IAuthenticationService;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
 
	@Autowired
    private ActivityProcess ActivityProcess;
	@Autowired
    private IAuthenticationService AuthenticationService;
	
//	@Override
//	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//			Authentication authentication) throws IOException, ServletException {
//		
//		try {
//			ActivityProcess.logLoginActivity(request);
//			String redirectPath = "/index?page=dashboard";
//			User loggedInUser = AuthenticationService.getLoggedDbUser(authentication);
//			if (loggedInUser.getPasswordExpirationDate().isBefore(Instant.now())) {
//				redirectPath  = "/login?error=passexpired";
//			}
//			
//			response.sendRedirect(request.getContextPath() + redirectPath);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	@Override
	public void onAuthenticationSuccess(jakarta.servlet.http.HttpServletRequest request,
			jakarta.servlet.http.HttpServletResponse response, Authentication authentication)
			throws IOException, jakarta.servlet.ServletException {
		
		try {
			ActivityProcess.logLoginActivity(request);
			String redirectPath = "/index?page=dashboard";
			User loggedInUser = AuthenticationService.getLoggedDbUser(authentication);
			if (loggedInUser.getPasswordExpirationDate().isBefore(Instant.now())) {
				redirectPath  = "/login?error=passexpired";
			}
			
			response.sendRedirect(request.getContextPath() + redirectPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}