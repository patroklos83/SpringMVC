package com.patroclos.model;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.envers.Audited;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.format.annotation.DateTimeFormat;

@MappedSuperclass
public abstract class BaseEntity<ID> implements Persistable<ID>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//@CreatedBy
	@NotAudited
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "createdBy", updatable = false)
	//@Column(name = "createdBy")
	private User createdByuser;

	@Audited
	@CreatedDate
    @Column(name = "createdDate", updatable = false)
	private Instant createdDate;
	
//	@LastModifiedBy
	@NotAudited
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "lastModifiedBy")
	//@Column(name = "lastModifiedBy")
	private User lastUpdatedByuser;

	@Audited
	@LastModifiedDate 
    @Column(name = "lastModifiedDate")
	private Instant lastUpdatedDate;
	
	@Audited
	@Column(name = "version")
	@Version
	private Long version;

	@Transient //always use javax.persistence.Transient
	private boolean isNew = true; 

	@Override
	public boolean isNew() {
		return isNew; 
	}
	
	public void setIsNew(boolean isNew) {
		 this.isNew = isNew; 
	}

	@PrePersist 
	@PostLoad
	void markNotNew() {
		this.isNew = false;
	}
	
	public User getCreatedByuser() {
		return createdByuser;
	}

	public void setCreatedByuser(User createdByuser) {
		this.createdByuser = createdByuser;
	}

	public Instant getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Instant createdDate) {
		this.createdDate = createdDate;
	}

	public User getLastUpdatedByuser() {
		return lastUpdatedByuser;
	}

	public void setLastUpdatedByuser(User lastUpdatedByuser) {
		this.lastUpdatedByuser = lastUpdatedByuser;
	}

	public Instant getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Instant lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

}