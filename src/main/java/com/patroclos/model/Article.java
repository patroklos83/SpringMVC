package com.patroclos.model;

import jakarta.persistence.CascadeType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.util.List;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

// @JsonIdentityInfo to properly serialize bi-directional relations [oneToMany-ManyToOne]
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "articles_sequence")
	@SequenceGenerator(name = "articles_sequence", sequenceName = "articles_sequence", allocationSize = 1)
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

	@Audited
	@AuditJoinTable(name = "article_citation_aud")
	@ManyToMany(cascade=CascadeType.MERGE, fetch=FetchType.EAGER)
	@JoinTable(
			name="article_citation",
			joinColumns={@JoinColumn(name="article_id")},
			inverseJoinColumns={@JoinColumn(name="citation_id")})
	private List<Citation> citations;

	@Audited
	@AuditJoinTable(name = "articlecomments_aud")
	@OneToMany(mappedBy="article", fetch=FetchType.EAGER)
	private List<ArticleComment> comments;

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

	public List<Citation> getCitations() {
		return citations;
	}

	public void setCitations(List<Citation> citations) {
		this.citations = citations;
	}

	public List<ArticleComment> getComments() {
		return comments;
	}

	public void setComments(List<ArticleComment> comments) {
		this.comments = comments;
	}

}  