package com.patroclos.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.patroclos.model.User;
import com.patroclos.process.ActivityProcess;
import com.patroclos.service.IAuthenticationService;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

	@Autowired
	private ActivityProcess ActivityProcess;
	@Autowired
	private IAuthenticationService AuthenticationService;

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {	
		
		try {
			User user = AuthenticationService.getLoggedDbUser(authentication);
			ActivityProcess.logLogoutActivity(request, user);
			response.sendRedirect(request.getContextPath() + "/login?logout");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
