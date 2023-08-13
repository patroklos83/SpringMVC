package com.patroclos.dto;

import java.util.List;

public class RoleDTO extends BaseDTO {

	private static final long serialVersionUID = 1L;

    private String name;
    private int hierarchyOrder;
	private String roleHierarchy;
    private List<PrivilegeDTO> privileges;
    private List<RoleEntityAccessDTO> roleEntityAccess;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public List<PrivilegeDTO> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(List<PrivilegeDTO> privileges) {
		this.privileges = privileges;
	}

	public int getHierarchyOrder() {
		return hierarchyOrder;
	}

	public void setHierarchyOrder(int hierarchyOrder) {
		this.hierarchyOrder = hierarchyOrder;
	}

	public List<RoleEntityAccessDTO> getRoleEntityAccess() {
		return roleEntityAccess;
	}

	public void setEntityAccess(List<RoleEntityAccessDTO> roleEntityAccess) {
		this.roleEntityAccess = roleEntityAccess;
	}

	public String getRoleHierarchy() {
		return roleHierarchy;
	}

	public void setRoleHierarchy(String roleHierarchy) {
		this.roleHierarchy = roleHierarchy;
	}  
}