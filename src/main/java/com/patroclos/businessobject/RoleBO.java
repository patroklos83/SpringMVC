package com.patroclos.businessobject;

import org.springframework.beans.factory.annotation.Autowired;

import com.patroclos.businessobject.core.BaseBO;
import com.patroclos.model.BaseO;
import com.patroclos.repository.RoleRepository;

import org.springframework.stereotype.Component;

@Component
public class RoleBO extends BaseBO {
	
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_USER = "ROLE_USER";
	public static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";
	
	@Autowired
	private RoleRepository RoleRepository;

	@Override
	public BaseO load(long id, Class<? extends BaseO> baseO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(BaseO baseO) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(BaseO baseO) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateOnSave(BaseO baseO) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateOnDelete(BaseO baseO) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	public BaseO findRoleByName(String roleName) {
		return RoleRepository.findByName(roleName);
	}

}
