package com.patroclos.uicomponent.core;

public class Action{
	
	private String description;
	private String action;
	private boolean executeFromJavascript;
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public boolean isExecuteFromJavascript() {
		return executeFromJavascript;
	}
	public void setExecuteFromJavascript(boolean executeFromJavascript) {
		this.executeFromJavascript = executeFromJavascript;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
