package com.patroclos.dto;

import java.util.List;

import com.patroclos.model.enums.AuditedPropertyFieldType;

public class AuditedPropertyDTO {
	
	private String entity;
	private Long entityId;
	private String propName;
	private String prevValue;
	private String newValue;
	private List<BaseDTO> prevValues;
	private List<BaseDTO> newValues;
	private Class<? extends BaseDTO> dataListType;
	private AuditedPropertyFieldType fieldType;
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
	public List<BaseDTO> getPrevValues() {
		return prevValues;
	}
	public void setPrevValues(List<BaseDTO> prevValues) {
		this.prevValues = prevValues;
	}
	public List<BaseDTO> getNewValues() {
		return newValues;
	}
	public void setNewValues(List<BaseDTO> newValues) {
		this.newValues = newValues;
	}
	public AuditedPropertyFieldType getFieldType() {
		return fieldType;
	}
	public void setFieldType(AuditedPropertyFieldType fieldType) {
		this.fieldType = fieldType;
	}
	public Class<? extends BaseDTO> getDataListType() {
		return dataListType;
	}
	public void setDataListType(Class<? extends BaseDTO> dataListType) {
		this.dataListType = dataListType;
	}
	

}
