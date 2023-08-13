package com.patroclos.dto;

import com.patroclos.model.enums.CitationType;

public class CitationDTO extends BaseDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String title;
	private String link;
	private CitationType citationtype;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public CitationType getCitationtype() {
		return citationtype;
	}

	public void setCitationtype(CitationType citationtype) {
		this.citationtype = citationtype;
	}
	
}
