package com.patroclos.dto;

public class ArticleDTO extends BaseDTO { 

	private static final long serialVersionUID = 1L;

	private String title;
	
	private String category;

	private String summary;
	
	private String author;

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