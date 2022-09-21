package com.patroclos.configuration;

import javax.servlet.http.HttpSessionEvent;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.patroclos.service.IAuthenticationService;


public class MyHttpSessionEventPublisher extends HttpSessionEventPublisher implements ApplicationContextAware {

	@Autowired
	private IAuthenticationService AuthenticationFacade;
	
	@Autowired
	private SessionRegistry sessionRegistry;

	static final long serialVersionUID = 02L;

	ApplicationContext applicationContext = null;

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		super.sessionCreated(event);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {

		//var principal = AuthenticationFacade.getAuthentication().getPrincipal();
		//do something 

		//	   if (sessionRegistry == null)
		//		   return;
		//	   
		//	   SessionRegistry sessionRegistry = getSessionRegistry();
		//	    SessionInformation sessionInfo = (sessionRegistry != null ? sessionRegistry
		//	            .getSessionInformation(event.getSession().getId()) : null);
		//	 //   UserDetails ud = null;
		//	    if (sessionInfo != null) {
		//	   //     ud = (UserDetails) sessionInfo.getPrincipal();
		//	    }
		////	    if (ud != null) {
		////	               // Do my stuff
		////	    }

		super.sessionDestroyed(event);
	}

	private SessionRegistry getSessionRegistry() {
		//  ApplicationContext context = new ClassPathXmlApplicationContext("classpath:WebContent/WEB-INF/startup-servlet.xml");
		//	   if (ApplicationContextProvider != null) { 
		//	   ApplicationContext appCtx = ApplicationContextProvider.getApplicationContext();
		//	    return appCtx.getBean("sessionRegistry", SessionRegistry.class);
		//	   }
		if (applicationContext != null){
			ApplicationContext appCtx =applicationContext;
			return appCtx.getBean("sessionRegistry", SessionRegistry.class);
			//Do something with this AccessBean
		}
		return null;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;

	}

}