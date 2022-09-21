package com.patroclos.model;

import java.util.Collection;
import java.util.List;

import javax.persistence.*;


@Entity
@Table(name = "roles")
public class Role extends BaseO {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id 
	@Column
    @GeneratedValue(generator = "roles_sequence")
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
//
//    @ManyToMany(mappedBy = "roles", fetch=FetchType.EAGER)
//    private List<User> users;
//    
    @ManyToMany(cascade=CascadeType.MERGE, fetch=FetchType.EAGER)
    @JoinTable(
        name = "roles_privileges", 
        joinColumns = @JoinColumn(
          name = "role_id", referencedColumnName = "id"), 
        inverseJoinColumns = @JoinColumn(
          name = "privilege_id", referencedColumnName = "id"))
    private Collection<Privilege> privileges;

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

//    public List <User> getUsers() {
//        return users;
//    }
//
//    public void setUsers(List <User> users) {
//        this.users = users;
//    }

	public Collection<Privilege> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(Collection<Privilege> privileges) {
		this.privileges = privileges;
	}
    
    
}