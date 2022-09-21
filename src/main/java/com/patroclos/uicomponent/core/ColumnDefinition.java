package com.patroclos.uicomponent.core;

public class ColumnDefinition{	
	
	private ColumnDefinitionType type;
	private String columnDbName;
	private String columnAlias;
	private String actionLink;
	private String expandableRowActionLink;
	private Object value;
	
	public ColumnDefinitionType getType() {
		return type;
	}
	public void setType(ColumnDefinitionType type) {
		this.type = type;
	}
	public String getColumnDbName() {
		return columnDbName;
	}
	public void setColumnDbName(String columnDbName) {
		this.columnDbName = columnDbName;
	}
	public String getActionLink() {
		return actionLink;
	}
	public void setActionLink(String actionLink) {
		this.actionLink = actionLink;
	}
	public String getExpandableRowActionLink() {
		return expandableRowActionLink;
	}
	public void setExpandableRowActionLink(String expandableRowActionLink) {
		this.expandableRowActionLink = expandableRowActionLink;
	}
	public String getColumnAlias() {
		return columnAlias;
	}
	public void setColumnAlias(String columnAlias) {
		this.columnAlias = columnAlias;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
}