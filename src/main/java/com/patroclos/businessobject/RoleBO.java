package com.patroclos.businessobject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import com.patroclos.businessobject.core.BaseBO;
import com.patroclos.exception.ValidationException;
import com.patroclos.model.BaseO;
import com.patroclos.model.EntityAccess;
import com.patroclos.model.Privilege;
import com.patroclos.model.Role;
import com.patroclos.model.RoleEntityAccess;
import com.patroclos.repository.RoleRepository;

import jakarta.persistence.FlushModeType;

import org.springframework.stereotype.Component;

@Component
public class RoleBO extends BaseBO {

	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_USER = "ROLE_USER";
	public static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";
	
	private static final String ROLE_NAME_PREFIX = "ROLE_";

	@Autowired
	private RoleRepository RoleRepository;
	@Autowired
	private EntityAccessBO EntityAccessBO;

	@Override
	public BaseO load(long id, Class<? extends BaseO> baseO) {
		Role role = (Role) super.loadBaseO(id, baseO);
		if (role != null) {
			String roleHierarchy = getRoleHierarchyString(true);
			role.setRoleHierarchy(roleHierarchy);
		}

		return role;
	}

	@Override
	public List<BaseO> loadAll() {
		return super.load(Role.class);
	}

	@Override
	public void save(BaseO baseO) throws Exception {
		Role role = (Role)baseO;
		boolean isNew = baseO.isNew();
		
		String name = role.getName().startsWith(ROLE_NAME_PREFIX) ? role.getName().replaceFirst(ROLE_NAME_PREFIX, "") : role.getName();
		role.setName(name.toUpperCase().trim());

		if (isNew) {
			List<BaseO> entities = EntityAccessBO.loadAll();
			if (entities != null && entities.size() > 0) {
				entities.forEach(r -> 
				{ 
					EntityAccess entity = ((EntityAccess)r);

					RoleEntityAccess roleEntityAccess = new RoleEntityAccess();
					roleEntityAccess.setRole(role);
					roleEntityAccess.setEntityAccess(entity);
					roleEntityAccess.setReadAccess(true);

					if (role.getRoleEntityAcces() == null)
						role.setRoleEntityAcces(new ArrayList<RoleEntityAccess>());
					role.getRoleEntityAcces().add(roleEntityAccess);
				});
			}
		}
		
		// Validate entity access level consistency with privileges eisting on role
		if (role.getPrivileges() != null) {
			Optional<Privilege> create = role.getPrivileges().stream().filter(p -> p.getName().equalsIgnoreCase(com.patroclos.businessobject.PrivilegeBO.PRIVILEGE_NAME_CREATE)).findAny();
			Optional<Privilege> read = role.getPrivileges().stream().filter(p -> p.getName().equalsIgnoreCase(com.patroclos.businessobject.PrivilegeBO.PRIVILEGE_NAME_READ)).findAny();
			Optional<Privilege> update = role.getPrivileges().stream().filter(p -> p.getName().equalsIgnoreCase(com.patroclos.businessobject.PrivilegeBO.PRIVILEGE_NAME_UPDATE)).findAny();
			Optional<Privilege> delete = role.getPrivileges().stream().filter(p -> p.getName().equalsIgnoreCase(com.patroclos.businessobject.PrivilegeBO.PRIVILEGE_NAME_DELETE)).findAny();
		
			if (role.getRoleEntityAcces() != null) {
				boolean invalidAccessCreate = create.isEmpty() &&  role.getRoleEntityAcces().stream().anyMatch(e -> e.isCreateAccess());
				boolean invalidAccessRead = read.isEmpty() &&  role.getRoleEntityAcces().stream().anyMatch(e -> e.isReadAccess());
				boolean invalidAccessUpdate = update.isEmpty() &&  role.getRoleEntityAcces().stream().anyMatch(e -> e.isUpdateAccess());
				boolean invalidAccessDelete = delete.isEmpty() &&  role.getRoleEntityAcces().stream().anyMatch(e -> e.isDeleteAccess());
			
			   if (invalidAccessCreate)
				   throw new ValidationException("CREATE Privilege not found, Role cannot set [Create=true] to Entity Access Level");
			   if (invalidAccessRead)
				   throw new ValidationException("READ Privilege not found, Role cannot set [Read=true] to Entity Access Level");
			   if (invalidAccessUpdate)
				   throw new ValidationException("UPDATE Privilege not found, Role cannot set [Update=true] to Entity Access Level");
			   if (invalidAccessDelete)
				   throw new ValidationException("DELETE Privilege not found, Role cannot set [Delete=true] to Entity Access Level");
			
			}
		}

		super.saveBaseO(baseO);
	}

	@Override
	public void saveAll(List<BaseO> baseOs) throws Exception {
		super.saveAllBaseO(baseOs);
	}

	@Override
	public void delete(BaseO baseO) throws Exception {
		super.deleteBaseO(baseO);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void validateOnSave(BaseO baseO) throws Exception {
		Role role = (Role)baseO;
			
		if (role.getName() == null || role.getName().isBlank())
			throw new ValidationException("Please specify Role Name");
		
		Pattern pattern = Pattern.compile("^[A-Za-z]*$");
		Matcher passMatcher = pattern.matcher(role.getName());
		if (!passMatcher.matches()) {
			throw new ValidationException("Role name must contain only letters");
		}
		
		String name = !role.getName().startsWith(ROLE_NAME_PREFIX) ? "ROLE_%s".formatted(role.getName()) : role.getName();
		role.setName(name.toUpperCase().trim());
			
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("NAME", role.getName());
		params.put("ID", role.getId());
		List<BaseO> existingRoles = (List<BaseO>) Repository.query("FROM Role e where e.name = :NAME and e.id != :ID", params, FlushModeType.COMMIT);
		if (existingRoles != null && existingRoles.size() > 0) {
			throw new ValidationException("Role [%s] already registered".formatted(role.getName()));
		}
	}

	@Override
	public void validateOnDelete(BaseO baseO) throws Exception {
		// TODO Auto-generated method stub

	}

	public BaseO findRoleByName(String roleName) {
		return RoleRepository.findByName(roleName);
	}

	public String getRoleHierarchyString(boolean attachHierarchyNumbering) {
		StringBuilder hierarchyBuilder = new StringBuilder();

		List<BaseO> r = loadAll();
		if (r != null && r.size() > 0) {		
			List<Role> roles = r.stream().map(o -> (Role)o).collect(Collectors.toList());
			roles = roles.stream().sorted(Comparator.comparingInt(Role::getHierarchyOrder).reversed()).collect(Collectors.toList());	

			Role prevRole = null;
			for (int i=0; i < roles.size(); i++) {
				Role currRole = roles.get(i);

				if (prevRole != null && prevRole.getHierarchyOrder() > currRole.getHierarchyOrder())
					hierarchyBuilder.append(" > ");
				else if (i > 0)
					hierarchyBuilder.append(" ");
				
				hierarchyBuilder.append(currRole.getName() + (attachHierarchyNumbering ? " [" + currRole.getHierarchyOrder() + "]" : " "));

				prevRole = currRole;
			}
		}		

		return hierarchyBuilder.toString();
	}
}
