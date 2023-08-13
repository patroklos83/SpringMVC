package com.patroclos.security;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.patroclos.process.ActivityProcess;
import com.patroclos.service.IAuthenticationService;
import com.patroclos.controller.core.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.patroclos.exception.SystemException;
import com.patroclos.model.User;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

	@Autowired
	private ActivityProcess ActivityProcess;
	@Autowired
	private IAuthenticationService AuthenticationService;
	//@Autowired
	//private SecurityContextHolder SecurityContextHolder;

	//@Autowired
	//private DataHolder DataHolder;

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, jakarta.servlet.ServletException {

		try {
			User user = AuthenticationService.getLoggedDbUser(authentication);
			if (user != null)
				ActivityProcess.logLogoutActivity(request, user);
			
			//if (DataHolder != null)
				//DataHolder.clear();

			response.sendRedirect(request.getContextPath() + "/login?logout");
		} catch (Exception e) {
			e.printStackTrace();
			throw new SystemException(e);
		}

	}

}
