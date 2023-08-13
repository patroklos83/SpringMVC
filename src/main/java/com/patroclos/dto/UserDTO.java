package com.patroclos.dto;

import java.time.Instant;
import java.util.List;

public class UserDTO extends BaseDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String username;
	private String password;
	private String passwordConfirm;
	private boolean isPasswordChange;
	private Instant passwordExpirationDate;
	private int enabled;
    private List<RoleDTO> roles;
    private List<GroupDTO> groups;

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}	
	public String getPasswordConfirm() {
		return passwordConfirm;
	}
	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}
	public Instant getPasswordExpirationDate() {
		return passwordExpirationDate;
	}
	public void setPasswordExpirationDate(Instant passwordExpirationDate) {
		this.passwordExpirationDate = passwordExpirationDate;
	}
	public int getEnabled() {
		return enabled;
	}
	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}
	public List<RoleDTO> getRoles() {
		return roles;
	}
	public void setRoles(List<RoleDTO> roles) {
		this.roles = roles;
	}
	public boolean isPasswordChange() {
		return isPasswordChange;
	}
	public void setPasswordChange(boolean isPasswordChange) {
		this.isPasswordChange = isPasswordChange;
	}
	public List<GroupDTO> getGroups() {
		return groups;
	}
	public void setGroups(List<GroupDTO> groups) {
		this.groups = groups;
	}
	
}
