package com.patroclos.model;

import java.util.List;
import java.util.Set;

import org.hibernate.envers.Audited;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.*;

@Audited
@Entity
@Table(name = "groups")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Group extends BaseO {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id 
	@Column
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "groups_sequence")
	@SequenceGenerator(name = "groups_sequence", sequenceName = "groups_sequence", allocationSize = 1)
    private Long id;
	
	@Audited(withModifiedFlag = true)
    @Column(nullable = false, unique = true)
    private String name;
	
	@Transient
	private List<User> users;

	@Override
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
    
    public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
}