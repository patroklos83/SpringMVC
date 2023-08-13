package com.patroclos.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.patroclos.dto.UserDTO;
import com.patroclos.service.IAuthenticationService;

@Component
public class AuthenticationFacade {
	
	@Autowired
    private IAuthenticationService AuthenticationService;
	
	public UserDTO getLoggedUser() throws Exception {
	  return AuthenticationService.getLoggedDbUserDTO();
	}
	
	public boolean isLoggedUserAdmin() throws Exception {
		UserDTO user = getLoggedUser();
		return user.getRoles() != null 
				&& user.getRoles()
				.stream()
				.anyMatch(r -> r.getName().equals(com.patroclos.businessobject.RoleBO.ROLE_ADMIN)) ? true : false;
	}
}
