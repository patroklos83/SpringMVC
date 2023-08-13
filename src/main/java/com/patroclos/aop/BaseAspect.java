package com.patroclos.aop;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patroclos.model.BaseO;

import ch.qos.logback.classic.Logger;

public class BaseAspect {

	protected Logger logger = ((ch.qos.logback.classic.Logger)LoggerFactory.getLogger(this.getClass()));
	
	@Autowired
	protected ObjectMapper jacksonObjectMapper;
	
	protected String getUniqueThreadHashCode() {
		return Thread.currentThread().getName();
	}

	protected String getPageLoadEntityId(Object[] args) throws JsonProcessingException {
		if (args != null && args.length > 0) {
			for (Object arg : args) {
				if (arg != null) {
					
					if (!(arg instanceof Long))
						continue;
					
					return arg.toString();
				}
			}
		}
		return "";
	}
	
	protected void printMethodParams(Object[] args) throws JsonProcessingException {
		if (args != null && args.length > 0) {
			for (Object arg : args) {
				if (arg != null) {
					String val = arg.toString();
					if (arg instanceof BaseO) {
						val = jacksonObjectMapper.writeValueAsString(arg);
					}
					logger.info("{}; Method input args values: arg type {} | arg value: " +  val, getUniqueThreadHashCode(), arg.getClass().getName());
				}
			}
		}
	}
}
