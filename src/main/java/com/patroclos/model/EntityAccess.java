package com.patroclos.model;

import java.util.List;

import org.hibernate.envers.Audited;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.*;

@Audited
@Entity
@Table(name = "entityAccess")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class EntityAccess extends BaseO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id 
	@Column
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entityAccess_sequence")
	@SequenceGenerator(name = "entityAccess_sequence", sequenceName = "entityAccess_sequence", allocationSize = 1)
	private Long id;

	@Audited(withModifiedFlag = true)
	@Column(nullable = false, unique = true)
	private String name;

	//@OneToMany(mappedBy = "entityAccess")
	//private List<RoleEntityAccess> entityAccess;

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
}