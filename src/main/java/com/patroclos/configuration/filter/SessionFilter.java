package com.patroclos.configuration.filter;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

@WebFilter(urlPatterns = {"/*" })
public class SessionFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		Cookie[] allCookies = req.getCookies();
		if (allCookies != null) {
			Cookie session = 
					Arrays.stream(allCookies).filter(x -> x.getName().equals("JSESSIONID"))
					.findFirst().orElse(null);

			if (session != null) {
				//session.setHttpOnly(true);
			//	session.setSecure(true);
			//	res.addCookie(session);
			}
			else {
//				request = new HttpServletRequestWrapper((HttpServletRequest) request) {
//	                @Override
//	                public String getRequestURI() {
//	                    return "/login";
//	                }
//	            };
			}
		}
		
		
		HttpSession currentSession = ((HttpServletRequest)request).getSession(false);
        if(currentSession == null)
        {
        	// Handle cases of xhr ajax requests when session is expired
            String ajaxHeader = ((HttpServletRequest) request).getHeader("X-Requested-With");
            if("XMLHttpRequest".equals(ajaxHeader))
            {
                HttpServletResponse resp = (HttpServletResponse) response;
                resp.sendError(901, "Session expired. Please relogin");
            }
            else
            {
                // Redirect to login page. this case is handled by Spring Security Configuration
            }
        }
        else
        {
        	 // Redirect to login page. this case is handled by Spring Security Configuration
        }
		
		chain.doFilter(req, res);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}
	
}