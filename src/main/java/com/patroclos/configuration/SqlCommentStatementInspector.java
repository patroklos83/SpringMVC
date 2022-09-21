package com.patroclos.configuration;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class SqlCommentStatementInspector implements StatementInspector {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Logger logger = ((ch.qos.logback.classic.Logger)LoggerFactory.getLogger(this.getClass()));
	
 
    @Override
    public String inspect(String sql) {
    	logger.info("{}; Hibernate executes query: {}", getUniqueThreadHashCode(), sql);
    	return sql;
    }
    
	private String getUniqueThreadHashCode() {
		return Thread.currentThread().getName();
	}
}