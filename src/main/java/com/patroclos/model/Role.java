package com.patroclos.model;

import java.util.Collection;
import java.util.List;

import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.*;

@Audited
@Entity
@Table(name = "roles")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Role extends BaseO {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id 
	@Column
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roles_sequence")
	@SequenceGenerator(name = "roles_sequence", sequenceName = "roles_sequence", allocationSize = 1)
    private Long id;
	
	@Audited(withModifiedFlag = true)
    @Column(nullable = false, unique = true)
    private String name;
	
	@Audited(withModifiedFlag = true)
    @Column(nullable = false, unique = true)
	private int hierarchyOrder;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToMany(cascade=CascadeType.MERGE, fetch=FetchType.EAGER)
    @JoinTable(
        name = "roles_privileges", 
        joinColumns = @JoinColumn(
          name = "role_id", referencedColumnName = "id"), 
        inverseJoinColumns = @JoinColumn(
          name = "privilege_id", referencedColumnName = "id"))
    private Collection<Privilege> privileges;
	
//	@Audited
//	@AuditJoinTable(name = "role_entityAccess_aud")
//	@ManyToMany(cascade=CascadeType.MERGE, fetch=FetchType.EAGER)
//	@JoinTable(
//			name="role_entityAccess",
//			joinColumns={@JoinColumn(name="role_id")},
//			inverseJoinColumns={@JoinColumn(name="entityAccess_id")})
//    private List<EntityAccess> entityAcces;
	
	@Audited
	@AuditJoinTable(name = "role_entityAccess_aud")
	@OneToMany(mappedBy = "role", fetch=FetchType.EAGER)
    private List<RoleEntityAccess> roleEntityAcces;
	
	@Transient
	private String roleHierarchy;

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

	public int getHierarchyOrder() {
		return hierarchyOrder;
	}
	
	public String getRoleHierarchy() {
		return roleHierarchy;
	}

	public void setRoleHierarchy(String roleHierarchy) {
		this.roleHierarchy = roleHierarchy;
	}

	public void setHierarchyOrder(int hierarchyOrder) {
		this.hierarchyOrder = hierarchyOrder;
	}

	public Collection<Privilege> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(Collection<Privilege> privileges) {
		this.privileges = privileges;
	}
    public List<RoleEntityAccess> getRoleEntityAcces() {
		return roleEntityAcces;
	}

	public void setRoleEntityAcces(List<RoleEntityAccess> roleEntityAcces) {
		this.roleEntityAcces = roleEntityAcces;
	} 
    
}