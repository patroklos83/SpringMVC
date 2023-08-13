package com.patroclos.model;

import java.time.Instant;
import java.util.List;

import jakarta.persistence.*;

import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Audited
@Entity
@Table(name="USERS")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class User extends BaseO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@NotAudited
	@Column
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_sequence")
	@SequenceGenerator(name = "users_sequence", sequenceName = "users_sequence", allocationSize = 1)
	private Long id;
	
	@Audited(withModifiedFlag = true)
	@Column
	private String username;
	
	@Audited(withModifiedFlag = true)
	@Column
	private String password;
	
	@Transient
	private String passwordConfirm;

	@Transient
	private boolean isPasswordChange;
	
	@Audited(withModifiedFlag = true)
	@Column
	private Instant passwordExpirationDate;
	
	@Audited(withModifiedFlag = true)
	@Column
	private int enabled;
	
	@Audited
	@AuditJoinTable(name = "user_role_aud")
	@ManyToMany(cascade=CascadeType.MERGE, fetch=FetchType.EAGER)
    @JoinTable(
       name="user_role",
       joinColumns={@JoinColumn(name="user_id")},
       inverseJoinColumns={@JoinColumn(name="role_id")})
    private List<Role> roles;
	
	@Audited
	@AuditJoinTable(name = "user_group_aud")
	@ManyToMany(cascade=CascadeType.MERGE, fetch=FetchType.EAGER)
    @JoinTable(
       name="user_group",
       joinColumns={@JoinColumn(name="user_id")},
       inverseJoinColumns={@JoinColumn(name="group_id")})
    private List<Group> groups;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
	public boolean isPasswordChange() {
		return isPasswordChange;
	}
	public void setPasswordChange(boolean isPasswordChange) {
		this.isPasswordChange = isPasswordChange;
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
	public boolean getIsEnabled() {
		return enabled == 1 ? true : false;
	}
	public void setEnabled(int enabled) {
		this.enabled = enabled;
	}
	public List<Role> getRoles() {
		return roles;
	}
	public void setRoles(List<Role> list) {
		this.roles = list;
	}	
	public List<Group> getGroups() {
		return groups;
	}
	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
}
