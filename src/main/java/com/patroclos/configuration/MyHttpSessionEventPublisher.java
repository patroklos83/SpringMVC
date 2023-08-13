package com.patroclos.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.session.SessionRegistry;
import com.patroclos.service.IAuthenticationService;

import jakarta.servlet.http.HttpSessionEvent;


public class MyHttpSessionEventPublisher { 

	@Autowired
	private IAuthenticationService AuthenticationFacade;
	
	@Autowired
	private SessionRegistry sessionRegistry;

	static final long serialVersionUID = 02L;

	ApplicationContext applicationContext = null;

	public void sessionCreated(HttpSessionEvent event) {
		
	}

	public void sessionDestroyed(HttpSessionEvent event) {
	}

	private SessionRegistry getSessionRegistry() {
		if (applicationContext != null){
			ApplicationContext appCtx =applicationContext;
			return appCtx.getBean("sessionRegistry", SessionRegistry.class);
			//Do something with this AccessBean
		}
		return null;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;

	}

}