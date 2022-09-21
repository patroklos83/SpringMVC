package com.patroclos.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "activitylog")
public class ActivityLog extends BaseO {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(generator = "activitylog_sequence")
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
