package com.patroclos.model;

import jakarta.persistence.*;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RevisionNumber;

@MappedSuperclass
public abstract class BaseO extends BaseEntity<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Transient 
	@RevisionNumber
	protected String revNumber;
	
	@Audited
	@Column(name="lastUpdatedByprocessId")	
	private String lastUpdatedByprocessId;
	
	@Audited(withModifiedFlag = true)
	@Column(name="isdeleted")	
	private long isDeleted;
	
	public long getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(long isDeleted) {
		this.isDeleted = isDeleted;
	}
	
	public String getLastUpdatedByProcessId() {
		return lastUpdatedByprocessId;
	}
	public void setLastUpdatedByProcessId(String lastUpdatedByprocessId) {
		this.lastUpdatedByprocessId = lastUpdatedByprocessId;
	}

}
