package com.patroclos.repository;

import org.springframework.data.repository.CrudRepository;

import com.patroclos.model.User;

public interface UserRepository extends CrudRepository<User,Long>{
	
	public User findByUsername(String username);

}
