package com.patroclos.model;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.patroclos.model.enums.CitationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Audited
@Entity
@Table(name="citations")
public class Citation extends BaseO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@NotAudited
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "articles_citations_sequence")
	@SequenceGenerator(name = "articles_citations_sequence", sequenceName = "articles_citations_sequence", allocationSize = 1)
	@Column(name="id", updatable = false)
	private long id;  
	
	@Audited(withModifiedFlag = true)	
	private String title;
	
	@Audited(withModifiedFlag = true)
	private String link;
	
	@Audited(withModifiedFlag = true)
	@Enumerated(EnumType.STRING)
	private CitationType citationtype;
	
	@Override
	public Long getId() {
		return id;
	}
	
	public void setId(Long Id) {
		this.id = Id;
	}

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
