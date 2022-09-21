package com.patroclos.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.patroclos.businessobject.*;
import com.patroclos.dto.BaseDTO;
import com.patroclos.dto.UserDTO;
import com.patroclos.exception.ValidationException;
import com.patroclos.model.Role;
import com.patroclos.model.User;

@Component
public class UserService extends BaseService {

	@Autowired
	private PasswordEncoder PasswordEncoder;
	@Autowired
	private CRUDService CRUDService;
	@Autowired
	private UserBO UserBO;
	@Autowired
	private RoleBO RoleBO;

	public void signUp(BaseDTO input) throws Exception {

		UserDTO userDTO = (UserDTO)input;

		if (userDTO.getUsername().isBlank() || userDTO.getUsername().isEmpty()) {
			throw new ValidationException("Username cannot be empty!");
		}
		
		User user = (User) UserBO.loadByUsername(userDTO.getUsername());
		if (user != null) {
			throw new ValidationException("Please choose a different username.");
		}

		if (userDTO.getPassword().isBlank() || userDTO.getPassword().isEmpty()) {
			throw new ValidationException("Enter password to confirm!");
		}

		if (userDTO.getPasswordConfirm().isBlank() || userDTO.getPasswordConfirm().isEmpty()) {
			throw new ValidationException("Password confirm is empty!");
		}

		if (userDTO.getPassword().length() < 10) {
			throw new ValidationException("password must have at least 10 chars length");
		}

		Pattern pattern = Pattern.compile("^(?=.*\\d)(?=.*[@$!%*?&])(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{10,15}$");
		Matcher passMatcher = pattern.matcher(userDTO.getPassword());
		if (!passMatcher.matches()) {
			throw new ValidationException("password must have 10-15 characters consisting of lowercase/uppercase letters, digits and special characters");
		}

		if (!userDTO.getPassword().equals(userDTO.getPasswordConfirm())) {
			throw new ValidationException("password confirm failed. Please enter correct password.");
		}

		var passwordHash = PasswordEncoder.encode(userDTO.getPassword());
		userDTO.setPassword(passwordHash);
		userDTO.setPasswordExpirationDate(Instant.now().plus(Duration.ofDays(90)));
		userDTO.setRoles(Arrays.asList((Role)RoleBO.findRoleByName(com.patroclos.businessobject.RoleBO.ROLE_USER)));
		userDTO.setEnabled(1);
		CRUDService.save(userDTO);	
	}
	
	public Collection<? extends GrantedAuthority> getUserAuthorities(UserDTO userDTO) throws Exception {
		User user = (User) CustomModelMapper.mapDTOtoModel(userDTO, User.class);
		return UserBO.getAuthorities(user.getRoles());
	}
	
	public BaseDTO loadUserByUsername(String username) throws Exception {
		User user = (User) UserBO.loadByUsername(username);
		if (user == null) return null;
		return CustomModelMapper.mapModeltoDTO(user, UserDTO.class);
	}
	
	public void disableUserByUsername(UserDTO userDTO) throws Exception {
		userDTO.setEnabled(0);
		CRUDService.save(userDTO);
	}

}
