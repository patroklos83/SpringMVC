package com.patroclos.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import com.patroclos.businessobject.*;
import com.patroclos.dto.UserDTO;
import com.patroclos.exception.SystemException;

@Component
public class AuthenticationService extends BaseService implements IAuthenticationService {

	@Autowired
	private UserBO UserBO;

	@Override
	public Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	@Override
	public com.patroclos.model.User getLoggedDbUser() throws Exception {
		if (getAuthentication() == null) {
			return null;
		}
		
		Authentication auth = getAuthentication();
		
		if (auth.getPrincipal() instanceof String) {
			String anonymousUser = (String)auth.getPrincipal();
			var user = UserBO.loadByUsername(anonymousUser);
			return (com.patroclos.model.User) user;
		}
		
		User principal = (User) auth.getPrincipal();
		com.patroclos.model.User user =(com.patroclos.model.User) UserBO.loadByUsername(principal.getUsername());

		if (user == null) {
			throw new SystemException("User is not found");
		}
		
		return user;
	}
	
	@Override
	public UserDTO getLoggedDbUserDTO(Authentication authentication) throws Exception {
		return (UserDTO) CustomModelMapper.mapModeltoDTO(getLoggedDbUser(authentication), UserDTO.class);
	}
	
	@Override
	public com.patroclos.model.User getLoggedDbUser(Authentication authentication) throws Exception {
		if (authentication == null) {
			return null;
		}
		
		User principal = (User) authentication.getPrincipal();
		com.patroclos.model.User user = getDbUserByUsername(principal.getUsername());
		
		return user;
	}
	
	@Override
	public UserDTO getLoggedDbUserDTO() throws Exception {
		return (UserDTO) CustomModelMapper.mapModeltoDTO(getLoggedDbUser(), UserDTO.class);
	}
	
	public com.patroclos.model.User getDbUserByUsername(String username) {
		com.patroclos.model.User user = (com.patroclos.model.User) UserBO.loadByUsername(username);	
		if (user == null) {
			throw new SystemException("User is not found");
		}		
		return user;
	}

}