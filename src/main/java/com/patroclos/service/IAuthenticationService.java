package com.patroclos.service;

import org.springframework.security.core.Authentication;

import com.patroclos.dto.UserDTO;
import com.patroclos.model.User;

public interface IAuthenticationService {
	
    public Authentication getAuthentication();

    public User getLoggedDbUser() throws Exception;
    
	public UserDTO getLoggedDbUserDTO() throws Exception;
	
	public UserDTO getLoggedDbUserDTO(Authentication authentication) throws Exception;

	public User getLoggedDbUser(Authentication authentication) throws Exception;
}