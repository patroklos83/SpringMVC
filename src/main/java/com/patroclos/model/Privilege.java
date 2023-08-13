package com.patroclos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class Privilege extends BaseO {
 
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "privileges_sequence")
    @SequenceGenerator(name = "privileges_sequence", sequenceName = "privileges_sequence", allocationSize = 1)
    private Long id;

    private String name;

//    @ManyToMany(mappedBy = "privileges", fetch = FetchType.EAGER)
//    private Collection<Role> roles;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
//
//	public Collection<Role> getRoles() {
//		return roles;
//	}
//
//	public void setRoles(Collection<Role> roles) {
//		this.roles = roles;
//	}
    
    
}