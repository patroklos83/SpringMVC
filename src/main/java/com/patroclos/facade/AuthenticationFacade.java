package com.patroclos.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.patroclos.dto.UserDTO;
import com.patroclos.model.User;
import com.patroclos.service.IAuthenticationService;

@Component
public class AuthenticationFacade {
	
	@Autowired
    private IAuthenticationService AuthenticationService;
	
	public UserDTO getLoggedUser() throws Exception {
	  return AuthenticationService.getLoggedDbUserDTO();
	}
}
