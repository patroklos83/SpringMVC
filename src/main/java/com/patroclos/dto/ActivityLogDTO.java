package com.patroclos.dto;

public class ActivityLogDTO extends BaseDTO {

	private static final long serialVersionUID = 1L;

	private String process;
	
	private String processId;

	private String summary;
	
	private String result;
	
	private String error;
	
	private String clientIp;
	
	private Class<? extends BaseDTO> inputType;
	
	private BaseDTO input;
	
	private Long id;

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

	public Class<? extends BaseDTO> getInputType() {
		return inputType;
	}

	public void setInputType(Class<? extends BaseDTO> inputType) {
		this.inputType = inputType;
	}

	public BaseDTO getInput() {
		return input;
	}

	public void setInput(BaseDTO input) {
		this.input = input;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}	
	
}
