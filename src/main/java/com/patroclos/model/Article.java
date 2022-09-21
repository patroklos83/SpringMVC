package com.patroclos.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RevisionEntity;

@Audited
@Entity
@Table(name="articles")
public class Article extends BaseO { 

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@NotAudited
	@GeneratedValue(generator = "articles_sequence")
	@Column(name="id", updatable = false)
	private long id;  
	
	@Audited(withModifiedFlag = true)
	@Column(name="title")	
	private String title;
	
	@Audited(withModifiedFlag = true)
	@Column(name="category")	
	private String category;
	
	@Audited(withModifiedFlag = true)
	@Column(name="summary")	
	private String summary;
	
	@Audited(withModifiedFlag = true)
	@Column(name="author")	
	private String author;
	
	@Override
	public Long getId() {
		return id;
	}
	
	public void setId(long Id) {
		 this.id = Id;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
}  