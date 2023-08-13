package com.patroclos.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.patroclos.model.Group;
import com.patroclos.model.Privilege;
import com.patroclos.model.Role;
import com.patroclos.model.User;
import com.patroclos.businessobject.*;

@Component("CustomUserDetailsService")
@Transactional
public class CustomUserDetailsService extends BaseService implements UserDetailsService {

	@Autowired
	private UserBO UserBO;
	@Autowired
	private RoleBO RoleBO;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = (User) UserBO.loadByUsername(username);
		if (user == null) {
			return new org.springframework.security.core.userdetails.User(
					" ", " ", true, true, true, true, 
					getAuthorities(Arrays.asList(
							(Role)RoleBO.findRoleByName(com.patroclos.businessobject.RoleBO.ROLE_ANONYMOUS))));
		}

		var userDetails = new org.springframework.security.core.userdetails.User(
				user.getUsername(), user.getPassword(), user.getIsEnabled(), true, true, 
				true, getAuthorities(user.getRoles()));
		
		return userDetails;
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
}
