package com.patroclos.dto;

public class RoleEntityAccessDTO extends BaseDTO {

	private static final long serialVersionUID = 1L;

    private RoleDTO role;
    private EntityAccessDTO entityAccess;
    private boolean readAccess;
    private boolean createAccess;
    private boolean updateAccess;
    private boolean deleteAccess;

	public EntityAccessDTO getEntityAccess() {
		return entityAccess;
	}
	public void setEntityAccess(EntityAccessDTO entityAccess) {
		this.entityAccess = entityAccess;
	}
	public RoleDTO getRole() {
		return role;
	}
	public void setRole(RoleDTO role) {
		this.role = role;
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