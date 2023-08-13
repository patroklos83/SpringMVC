package com.patroclos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "activitylog")
public class ActivityLog extends BaseO {

	private static final long serialVersionUID = 1L;
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "activitylog_sequence")
	@SequenceGenerator(name = "activitylog_sequence", sequenceName = "activitylog_sequence", allocationSize = 1)
	@Column(name="id")
	private long id;  
	
	@Column(name="process")	
	private String process;

	@Column(name="processId")	
	private String processId;
	
	@Column(name="summary")	
	private String summary;
	
	@Column(name="result")
	private String result;
	
	@Column(name="error")
	private String error;
	
	@Column(name="clientip")
	private String clientIp;

	@Override
	public Long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}	

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	
	
}
