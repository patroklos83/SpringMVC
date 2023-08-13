package com.patroclos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "activitylogDetails")
public class ActivityLogDetail extends BaseO {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "activitylogDetails_sequence")
	@SequenceGenerator(name = "activitylogDetails_sequence", sequenceName = "activitylogDetails_sequence", allocationSize = 1)
	@Column(name="id")
	private long id;  
	
	@Column(name="entity")	
	private String entity;
	
	@Column(name="entityid")
	private Long entityId;
	
	@Column(name="entityrevision")
	private Long entityRevision;
	
	@Column(name="processId")
	private String 	processId;
	
	@Transient
	private ActivityLog activityLog;

	@Override
	public Long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

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

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}
	
	public ActivityLog getActivityLog() {
		return this.activityLog;
	}

	public void setActivityLog(ActivityLog activityLog) {
		this.activityLog = activityLog;		
	}
		
}
