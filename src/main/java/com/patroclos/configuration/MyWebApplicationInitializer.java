package com.patroclos.configuration;


import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.WebApplicationInitializer;

public class MyWebApplicationInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(jakarta.servlet.ServletContext servletContext) throws jakarta.servlet.ServletException {
		servletContext.getSessionCookieConfig().setHttpOnly(true);        
		servletContext.getSessionCookieConfig().setSecure(true);  	
				
		// If you wish to place constraints on a single user’s ability to log in to your application, 
		// Spring Security supports this out of the box with the following simple additions. 
		// First, you need to add the following listener to your configuration to keep Spring Security
		// updated about session lifecycle events	
		servletContext.addListener(new HttpSessionEventPublisher());
	}

}