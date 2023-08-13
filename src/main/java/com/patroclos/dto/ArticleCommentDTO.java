package com.patroclos.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ArticleCommentDTO extends BaseDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String comment;
		
	private ArticleDTO article;

	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

	public ArticleDTO getArticle() {
		return article;
	}

	public void setArticle(ArticleDTO article) {
		this.article = article;
	}
}
