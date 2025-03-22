package com.patroclos.dto;

import java.io.Serializable;
import java.time.Instant;

import com.patroclos.model.User;

public class BaseDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Long id;

	protected User createdByuser;

	protected Instant createdDate;

	protected User lastUpdatedByuser;

	protected Instant lastUpdatedDate;

	protected Long version;

	protected boolean isNew = true;
	
    protected long isDeleted;
	
	public long getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(long isDeleted) {
		this.isDeleted = isDeleted;
	}

	public boolean isNew() {
		return isNew; 
	}

	public void setIsNew(boolean isNew) {
		this.isNew = isNew; 
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long Id) {
		if (Id != null && Id > 0) this.isNew = false;
		if (Id == null) {
			Id = 0l;
		}
		this.id = Id;
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
