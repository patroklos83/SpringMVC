package com.patroclos.uicomponent.core;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.patroclos.uicomponent.UIInput.Input;

public class Table extends UIComponent{		

	private final String tableId;
	private final String sqlQuery;
	private final Map<String, Input> inputFilters;
	private final String pagingUrl;
	private final Map<String, ColumnDefinition> columnDefinitions;	
	private SqlRowSet sqlRowSet;
	private Map<String,String> pagingParams;

	public Table(Builder builder)
	{
		this.tableId = builder.tableId;
		this.sqlQuery = builder.sqlQuery;
		this.sqlRowSet = builder.sqlRowSet;
		this.inputFilters = builder.inputFilters;
		this.pagingParams = builder.pagingParams;
		this.pagingUrl = builder.pagingUrl;
		this.columnDefinitions = builder.columnDefinitions;
	}
	
	public void setSqlRowSet(SqlRowSet sqlRowSet) {
		this.sqlRowSet = sqlRowSet;
	}		
		
	public void setPagingParams(Map<String, String> pagingParams) {
		this.pagingParams = pagingParams;
	}

	public String getTableId() {
		return tableId;
	}


	public String getSqlQuery() {
		return sqlQuery;
	}

	public Map<String, Input> getInputFilters() {
		return inputFilters;
	}

	public String getPagingUrl() {
		return pagingUrl;
	}

	public Map<String, String> getPagingParams() {
		return pagingParams;
	}

	public SqlRowSet getSqlRowSet() {
		return sqlRowSet;
	}

	public Map<String, ColumnDefinition> getColumnDefinitions() {
		return columnDefinitions;
	}

	public static class Builder {	
		
		private String tableId;
		private String sqlQuery;
		private SqlRowSet sqlRowSet;
		private Map<String, Input> inputFilters;
		private String pagingUrl;
		private Map<String,String> pagingParams;
		private Map<String, ColumnDefinition> columnDefinitions;

		public static Builder newInstance()
		{
			return new Builder();
		}

		private Builder() {}

		public Builder setTableId(String tableId) {
			this.tableId = tableId;
			return this;
		}

		public Builder setSqlQuery(String sqlQuery) {
			this.sqlQuery = sqlQuery;
			return this;
		}
		
		public Builder setInputFilters(Map<String, Input> inputFilters) {
			this.inputFilters = inputFilters;
			return this;
		}

		public Builder setPagingUrl(String pagingUrl) {
			this.pagingUrl = pagingUrl;
			return this;
		}

		public Builder setPagingParams(Map<String, String> pagingParams) {
			this.pagingParams = pagingParams;
			return this;
		}

		public Builder setSqlRowSet(SqlRowSet sqlRowSet) {
			this.sqlRowSet = sqlRowSet;
			return this;
		}

		public Builder setColumnDefinitions(Map<String, ColumnDefinition> columnDefinitions) {
			var t = columnDefinitions;
			if (t != null && t.size() > 0) {
				t = columnDefinitions
						.entrySet()
						.stream()
						.collect(Collectors.toMap(entry -> entry.getKey().toUpperCase(), entry -> entry.getValue()));	
			}
			this.columnDefinitions = t;
			return this;
		} 

		public Table build() {
			Table t = new Table(this);
			return t;		
		}

	}
}