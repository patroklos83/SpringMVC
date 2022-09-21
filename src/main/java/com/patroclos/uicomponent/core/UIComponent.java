package com.patroclos.uicomponent.core;

import com.patroclos.uicomponent.UIInputType;

public abstract class UIComponent {
	
	private String Id;
	private String name;
	private String hash;
	private UIInputType type;
	private Object value;
	private String html;
	private String dbField;
	private DbFieldType dbFieldType;

	private boolean isDbPrivateKey;
	
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}	
	public UIInputType getType() {
		return type;
	}
	public void setType(UIInputType type) {
		this.type = type;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getDbField() {
		return dbField;
	}
	public void setDbField(String dbField) {
		this.dbField = dbField;
	}
	public boolean isDbPrivateKey() {
		return isDbPrivateKey;
	}
	public void setDbPrivateKey(boolean isDbPrivateKey) {
		this.isDbPrivateKey = isDbPrivateKey;
	}
	public DbFieldType getDbFieldType() {
		return dbFieldType;
	}
	public void setDbFieldType(DbFieldType dbFieldType) {
		this.dbFieldType = dbFieldType;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	
}
