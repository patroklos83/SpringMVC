package com.patroclos.model;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.*;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;

@MappedSuperclass
public abstract class BaseEntity<ID> implements Persistable<ID>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Audited
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "createdBy", updatable = false)
	private User createdByuser;
	
	@Audited
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "createdByGroup", updatable = false)
	private Group createdByGroup;

	@Audited
	@CreatedDate
    @Column(name = "createdDate", updatable = false)
	private Instant createdDate;
	
	@Audited
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "lastModifiedBy")
	private User lastUpdatedByuser;

	@Audited
	@LastModifiedDate 
    @Column(name = "lastModifiedDate")
	private Instant lastUpdatedDate;
	
	// @Version as per the envers documentation
	// is not being audited since there is no reason to
	// Unless configuration key is set to false
	// org.hibernate.envers.do_not_audit_optimistic_locking_field
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
	
	public Group getCreatedByGroup() {
		return createdByGroup;
	}

	public void setCreatedByGroup(Group createdByGroup) {
		this.createdByGroup = createdByGroup;
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