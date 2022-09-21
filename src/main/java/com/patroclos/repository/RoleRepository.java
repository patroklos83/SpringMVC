package com.patroclos.repository;

import org.springframework.data.repository.CrudRepository;

import com.patroclos.model.Role;

public interface RoleRepository extends CrudRepository<Role,Long> {
	
	public Role findByName(String name);	

}
