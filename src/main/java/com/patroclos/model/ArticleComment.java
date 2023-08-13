package com.patroclos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Audited
@Entity
@Table(name="articlecomments")
public class ArticleComment extends BaseO { 

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@NotAudited
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "articlecomments_sequence")
	@SequenceGenerator(name = "articlecomments_sequence", sequenceName = "articlecomments_sequence", allocationSize = 1)
	@Column(name="id", updatable = false)
	private long id;  

	@Audited(withModifiedFlag = true)
	@Column(name="comment")	
	private String comment;

	@Audited(withModifiedFlag = true, modifiedColumnName = "article_id_mod")
	@ManyToOne
	@JoinColumn(name="article_id")
	private Article article;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(long Id) {
		this.id = Id;
	}

	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}
}  