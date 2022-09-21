package com.patroclos.dto;

public class ActivityLogDetailDTO extends BaseDTO {

	private static final long serialVersionUID = 1L;
	
	private String entity;
	
	private Long entityId;

	private Long entityRevision;
	
	private ActivityLogDTO activityLog;

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

	public Long getEntityRevision() {
		return entityRevision;
	}

	public void setEntityRevision(Long entityRevision) {
		this.entityRevision = entityRevision;
	}
	

	public ActivityLogDTO getActivityLog() {
		return activityLog;
	}

	public void setActivityLog(ActivityLogDTO activityLog) {
		this.activityLog = activityLog;
	}

}
	