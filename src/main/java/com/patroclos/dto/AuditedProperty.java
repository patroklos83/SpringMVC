package com.patroclos.dto;

public class AuditedProperty {
	
	private String entity;
	private Long entityId;
	private String propName;
	private String prevValue;
	private String newValue;
	private int newRevision;
	private int prevRevision;
	
	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
	public Long getEntityId() {
		return entityId;
	}
	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}
	public String getPropName() {
		return propName;
	}
	public void setPropName(String propName) {
		this.propName = propName;
	}
	public String getPrevValue() {
		return prevValue;
	}
	public void setPrevValue(String prevValue) {
		this.prevValue = prevValue;
	}
	public String getNewValue() {
		return newValue;
	}
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	public int getNewRevision() {
		return newRevision;
	}
	public void setNewRevision(int newRevision) {
		this.newRevision = newRevision;
	}
	public int getPrevRevision() {
		return prevRevision;
	}
	public void setPrevRevision(int prevRevision) {
		this.prevRevision = prevRevision;
	}

}
