package com.patroclos.security;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

import com.patroclos.exception.SystemException;
import com.patroclos.process.ActivityProcess;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Autowired
	private ActivityProcess ActivityProcess;
	
	public final static String LOGIN_FAIL_MAX_CONCURRENT_SESSIONS = "1";

	@Override
	public void onAuthenticationFailure(jakarta.servlet.http.HttpServletRequest request,
			jakarta.servlet.http.HttpServletResponse response, AuthenticationException exception)
			throws IOException, jakarta.servlet.ServletException {
		
	    String userName = request.getParameter("username");
		try {
			
			String errorCode = "";
			if (exception instanceof SessionAuthenticationException)
				errorCode = LOGIN_FAIL_MAX_CONCURRENT_SESSIONS;
			
			ActivityProcess.loginFailActivity(request, userName, exception.getMessage());
			response.sendRedirect(request.getContextPath() + "/login?error=%s".formatted(errorCode));
		} catch (Exception e) {
			e.printStackTrace();
			throw new SystemException(e);
		}
		
		
	}
}