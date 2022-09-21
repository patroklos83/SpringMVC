package com.patroclos.model;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;

@Audited
@Entity
@Table(name="USERS")
public class User extends BaseO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@NotAudited
	@Column
	@GeneratedValue(generator = "users_sequence")
	private Long id;
	
	@Audited(withModifiedFlag = true)
	@Column
	private String username;
	
	@Audited(withModifiedFlag = true)
	@Column
	private String password;
	
	@Audited(withModifiedFlag = true)
	@Column
	private Instant passwordExpirationDate;
	
	@Audited(withModifiedFlag = true)
	@Column
	private int enabled;
	
	@NotAudited
	@ManyToMany(cascade=CascadeType.MERGE, fetch=FetchType.EAGER)
    @JoinTable(
       name="user_role",
       joinColumns={@JoinColumn(name="user_id")},
       inverseJoinColumns={@JoinColumn(name="role_id")})
    private Collection<Role> roles;

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
	public Collection<Role> getRoles() {
		return roles;
	}
	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}	
	
}
