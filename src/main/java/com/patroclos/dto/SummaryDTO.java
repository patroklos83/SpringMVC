package com.patroclos.dto;

import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class SummaryDTO extends BaseDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    private String query;

	private Map<String,String> queryArgs;
	
	private SqlRowSet queryResults;
	
	// For what entity-dto this query refers to
	private Class<? extends BaseDTO> referencesEntityDTO;
	
    public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public Map<String,String> getQueryArgs() {
		return queryArgs;
	}
	public void setQueryArgs(Map<String,String> queryArgs) {
		this.queryArgs = queryArgs;
	}

	public SqlRowSet getQueryResults() {
		return queryResults;
	}
	public void setQueryResults(SqlRowSet queryResults) {
		this.queryResults = queryResults;
	}
	public Class<? extends BaseDTO> getReferencesEntityDTO() {
		return referencesEntityDTO;
	}
	public void setReferencesEntityDTO(Class<? extends BaseDTO> referencesEntityDTO) {
		this.referencesEntityDTO = referencesEntityDTO;
	}

}
