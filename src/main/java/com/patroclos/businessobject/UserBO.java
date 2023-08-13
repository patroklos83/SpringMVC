package com.patroclos.businessobject;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.patroclos.businessobject.core.BaseBO;
import com.patroclos.exception.ValidationException;
import com.patroclos.model.BaseO;
import com.patroclos.model.Privilege;
import com.patroclos.model.Role;
import com.patroclos.model.User;
import com.patroclos.repository.*;

@Component
public class UserBO extends BaseBO {

	@Autowired
	private UserRepository UserRepository;

	@Autowired
	private RoleBO RoleBO;

	@Autowired
	private PasswordEncoder PasswordEncoder;

	@Override
	public BaseO load(long id, Class<? extends BaseO> baseO) {
		return super.loadBaseO(id, baseO);
	}
	
	@Override
	public List<BaseO> loadAll() {
		return super.load(User.class);
	}

	public BaseO loadByUsername(String userName) {
		return UserRepository.findByUsername(userName);
	}

	@Override
	public void save(BaseO baseO) throws Exception {		
		User user = (User)baseO;

		if (user.isNew()) {
			ValidateSingUpNewUser(user);
			ValidatePassword(user);
			setPassword(user);
			if (user.getRoles() == null || user.getRoles().isEmpty()) {
				Role roleUser = (Role) RoleBO.findRoleByName(com.patroclos.businessobject.RoleBO.ROLE_USER);
				user.setRoles(List.of(roleUser));
			}
			user.setEnabled(1);
		}
		else {
			if (user.isPasswordChange()) { 
				ValidatePassword(user);
				setPassword(user);
			}
		}
		super.saveBaseO(baseO);
	}
	
	@Override
	public void saveAll(List<BaseO> baseOs) throws Exception {
		super.saveAllBaseO(baseOs);
	}

	private void setPassword(BaseO baseO) {
		User user = (User)baseO;

		String passwordHash = PasswordEncoder.encode(user.getPassword());
		user.setPassword(passwordHash);
		user.setPasswordExpirationDate(Instant.now().plus(Duration.ofDays(90)));
	}

	private void ValidatePassword(BaseO baseO) {
		User user = (User)baseO;

		if (user.getPassword().isBlank() || user.getPassword().isEmpty()) {
			throw new ValidationException("Enter password to confirm!");
		}

		if (user.getPasswordConfirm().isBlank() || user.getPasswordConfirm().isEmpty()) {
			throw new ValidationException("PasswordConfirm is empty!");
		}

		Pattern pattern = Pattern.compile("^(?=.*\\d)(?=.*[@$!%*?&])(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{10,15}$");
		Matcher passMatcher = pattern.matcher(user.getPassword());
		if (!passMatcher.matches()) {
			throw new ValidationException("password length must be between 10-15 characters, has at least one Uppercase letter, 1 digit and one special character (@,$,!,%,*,?,&)");
		}
	}

	private void ValidateSingUpNewUser(BaseO baseO) {
		User user = (User)baseO;

		if (user.getUsername().isBlank() || user.getUsername().isEmpty()) {
			throw new ValidationException("Username cannot be empty!");
		}

		User u = (User) loadByUsername(user.getUsername());
		if (u != null) {
			throw new ValidationException("Please choose a different username.");
		}
	}

	@Override
	public void delete(BaseO baseO) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void validateOnSave(BaseO baseO) throws Exception {
		User user = (User)baseO;
		
		if (user.getGroups() == null || user.getGroups().size() == 0)
			throw new ValidationException("User has to be included in at least 1 Group");
	}

	@Override
	public void validateOnDelete(BaseO baseO) throws Exception {
		// TODO Auto-generated method stub

	}

	public Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) { 
		return getGrantedAuthorities(getPrivileges(roles));
	}

	private List<String> getPrivileges(Collection<Role> roles) {

		List<String> privileges = new ArrayList<>();
		List<Privilege> collection = new ArrayList<>();
		for (Role role : roles) {
			privileges.add(role.getName());
			collection.addAll(role.getPrivileges());
		}
		for (Privilege item : collection) {
			privileges.add(item.getName());
		}
		return privileges;
	}

	private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		for (String privilege : privileges) {
			authorities.add(new SimpleGrantedAuthority(privilege));
		}
		return authorities;
	}
	
	@SuppressWarnings("unchecked")
	public List<User> loadUsersByGroupIds(List<Long> groupIds) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", groupIds);
		List<User> users = (List<User>) Repository.query("from User u" 
				+ " join u.groups g"
				+ " where g.id in (:id)", params);	
		return users;
		
	}
}
