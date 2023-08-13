package com.patroclos.dto;

import java.util.List;

public class ArticleDTO extends BaseDTO { 

	private static final long serialVersionUID = 1L;

	private String title;	
	private String category;
	private String summary;
	private String author;
	private List<CitationDTO> citations;
	private List<ArticleCommentDTO> comments;

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
	public List<CitationDTO> getCitations() {
		return citations;
	}
	public void setCitations(List<CitationDTO> citations) {
		this.citations = citations;
	}
	public List<ArticleCommentDTO> getComments() {
		return comments;
	}
	public void setComments(List<ArticleCommentDTO> comments) {
		this.comments = comments;
	}	
	
}  