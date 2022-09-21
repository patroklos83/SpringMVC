package com.patroclos.controller.core;

import com.patroclos.dto.BaseDTO;

public class Form {
	
	private String hash;
	private BaseDTO dto;
	private BaseDTO dirtyDto;
	
	public Form(String hash) {
		this.hash = hash;
	}
		
	public String getHash() {
		return hash;
	}

	public BaseDTO getDto() {
		return dto;
	}

	public void setDto(BaseDTO dto) {
		this.dto = dto;
	}

	public BaseDTO getDirtyDto() {
		return dirtyDto;
	}

	public void setDirtyDto(BaseDTO dirtyDto) {
		this.dirtyDto = dirtyDto;
	}
	
}
