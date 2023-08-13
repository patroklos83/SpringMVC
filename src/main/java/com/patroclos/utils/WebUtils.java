package com.patroclos.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;


@Component
public class WebUtils {

	private HttpServletRequest request;

	@Autowired
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getClientIp() {

		String remoteAddr = "";

		if (request != null) {
			remoteAddr = request.getHeader("X-FORWARDED-FOR");
			if (remoteAddr == null || "".equals(remoteAddr)) {
				remoteAddr = request.getRemoteAddr();
			}
		}

		return remoteAddr;
	}

	private static final String[] IP_HEADER_CANDIDATES = {
			"X-Forwarded-For",
			"Proxy-Client-IP",
			"WL-Proxy-Client-IP",
			"HTTP_X_FORWARDED_FOR",
			"HTTP_X_FORWARDED",
			"HTTP_X_CLUSTER_CLIENT_IP",
			"HTTP_CLIENT_IP",
			"HTTP_FORWARDED_FOR",
			"HTTP_FORWARDED",
			"HTTP_VIA",
			"REMOTE_ADDR"
	};

	public String getBaseUrl() {
		String baseUrl = "";
		
		var baseNoPath = ServletUriComponentsBuilder
				.fromRequestUri(request)
				.replacePath(null)
				.build();
		
		var base = ServletUriComponentsBuilder
				.fromRequestUri(request)
				.build();

		if (base.getPathSegments() != null && base.getPathSegments().size() > 0)
				baseUrl = baseNoPath.toUriString()  + "/" + base.getPathSegments().get(0);
		
		return baseUrl;
	}

	public Object getRequestParam(String key) {
		if (request == null)
			return null;
		Object value = request.getParameterValues(key);
		return value;
	}

	public static String getClientIpAddressIfServletRequestExist() {

		if (RequestContextHolder.getRequestAttributes() == null) {
			return "0.0.0.0";
		}

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		for (String header: IP_HEADER_CANDIDATES) {
			String ipList = request.getHeader(header);
			if (ipList != null && ipList.length() != 0 && !"unknown".equalsIgnoreCase(ipList)) {
				String ip = ipList.split(",")[0];
				return ip;
			}
		}

		return request.getRemoteAddr();
	}

	public static String getClientIpAddress(jakarta.servlet.http.HttpServletRequest request) {

		for (String header: IP_HEADER_CANDIDATES) {
			String ipList = request.getHeader(header);
			if (ipList != null && ipList.length() != 0 && !"unknown".equalsIgnoreCase(ipList)) {
				String ip = ipList.split(",")[0];
				return ip;
			}
		}

		return request.getRemoteAddr();
	}
	
	public String getContextPath() {
		
		if (request != null) {
			return request.getContextPath();
		}
		
		return "";
	}



}