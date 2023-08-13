package com.patroclos.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.patroclos.businessobject.RoleBO;
import com.patroclos.businessobject.UserBO;
import com.patroclos.dto.BaseDTO;
import com.patroclos.dto.UserDTO;
import com.patroclos.model.Group;
import com.patroclos.model.User;

@Component
public class UserService extends BaseService {

	@Autowired
	private CRUDService CRUDService;
	@Autowired
	private UserBO UserBO;
	@Autowired
	public RoleBO RoleBO;

	public void signUp(BaseDTO input) throws Exception {
		CRUDService.save(input);	
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

	public List<Group> getUserGroups(String username) {
		User user = (User) UserBO.loadByUsername(username);
		return user.getGroups();
	}

	/***
	 * Find all users who belong to the same
	 * groups of the user passed in the input
	 * param.
	 * @param username
	 * @return
	 */
	public List<User> getAllUsersBelongingToUserGroups(String username) {
		List<Group> groups = getUserGroups(username);
		if (groups != null && groups.size() > 0)
			return loadUsersByGroupIds(groups.stream().map(g -> g.getId()).collect(Collectors.toList()));
		return null;
	}

	public List<User> loadUsersByGroupIds(List<Long> groupIds) {	
		return UserBO.loadUsersByGroupIds(groupIds);		
	}
	
	public String getRoleHierarchyString() {
		return RoleBO.getRoleHierarchyString(false);
	}

}
