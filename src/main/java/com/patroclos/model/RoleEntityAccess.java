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
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Audited
@Entity
@Table(name="role_entityAccess")
public class RoleEntityAccess extends BaseO { 

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@NotAudited
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_entityAccess_sequence")
	@SequenceGenerator(name = "role_entityAccess_sequence", sequenceName = "role_entityAccess_sequence", allocationSize = 1)
	@Column(name="id", updatable = false)
    private Long id;
	
	@Audited(withModifiedFlag = true, modifiedColumnName = "role_id_mod")
	@ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
 
	@Audited(withModifiedFlag = true, modifiedColumnName = "entityAccess_id_mod")
    @ManyToOne
    @JoinColumn(name = "entityAccess_id")
    private EntityAccess entityAccess;
    
	@Audited(withModifiedFlag = true, modifiedColumnName = "readAccess_mod")
    private boolean readAccess;
    
	@Audited(withModifiedFlag = true, modifiedColumnName = "createAccess_mod")
    private boolean createAccess;

	@Audited(withModifiedFlag = true, modifiedColumnName = "updateAccess_mod")
    private boolean updateAccess;
    
	@Audited(withModifiedFlag = true, modifiedColumnName = "deleteAccess_mod")
    private boolean deleteAccess;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(long Id) {
		this.id = Id;	
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public EntityAccess getEntityAccess() {
		return entityAccess;
	}

	public void setEntityAccess(EntityAccess entityAccess) {
		this.entityAccess = entityAccess;
	}
	
	 public boolean isReadAccess() {
			return readAccess;
		}

		public void setReadAccess(boolean readAccess) {
			this.readAccess = readAccess;
		}

		public boolean isCreateAccess() {
			return createAccess;
		}

		public void setCreateAccess(boolean createAccess) {
			this.createAccess = createAccess;
		}

		public boolean isUpdateAccess() {
			return updateAccess;
		}

		public void setUpdateAccess(boolean updateAccess) {
			this.updateAccess = updateAccess;
		}

		public boolean isDeleteAccess() {
			return deleteAccess;
		}

		public void setDeleteAccess(boolean deleteAccess) {
			this.deleteAccess = deleteAccess;
		}
}  