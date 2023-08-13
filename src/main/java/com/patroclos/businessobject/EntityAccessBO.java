package com.patroclos.businessobject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.patroclos.businessobject.core.BaseBO;
import com.patroclos.exception.ValidationException;
import com.patroclos.model.BaseO;
import com.patroclos.model.EntityAccess;
import com.patroclos.model.Role;
import com.patroclos.model.RoleEntityAccess;
import com.patroclos.utils.SystemUtil;

import jakarta.persistence.FlushModeType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EntityAccessBO extends BaseBO {

	@Autowired
	private RoleBO RoleBO;

	@Override
	public BaseO load(long id, Class<? extends BaseO> baseO) {
		return super.loadBaseO(id, baseO);
	}

	@Override
	public List<BaseO> loadAll() {
		return super.load(EntityAccess.class);
	}

	@Transactional
	@Override
	public void save(BaseO baseO) throws Exception {	
		EntityAccess entityAccess = (EntityAccess)baseO;
		boolean isNew = entityAccess.isNew();
		super.saveBaseO(entityAccess);

		// Add new Entity definition to all existing Roles
		if (isNew) {
			List<BaseO> roles = RoleBO.loadAll();
			if (roles != null && roles.size() > 0) {
				roles.forEach(r -> 
				{ 
					Role role = ((Role)r);
					RoleEntityAccess roleEntityAccess = new RoleEntityAccess();
					roleEntityAccess.setRole(role);
					roleEntityAccess.setEntityAccess(entityAccess);
					roleEntityAccess.setReadAccess(true);
					role.getRoleEntityAcces().add(roleEntityAccess);
				});

				// Add new extity access to all roles except Anonymous
				roles.removeIf(r -> ((Role)r).getName().equals(com.patroclos.businessobject.RoleBO.ROLE_ANONYMOUS));

				RoleBO.saveAll(roles);
			}	
		}
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
		EntityAccess entityAccess = (EntityAccess)baseO;

		if (entityAccess.getName().isBlank())
			throw new ValidationException("Name is mandatory. Please use the exact name of the entity without ignoring the case");	
		
		entityAccess.setName(entityAccess.getName().trim());

		try
		{
			String classFullName = "%s%s".formatted(SystemUtil.MODEL_PACKAGE, 
					entityAccess.getName());		
			Class.forName(classFullName);
		}
		catch (ClassNotFoundException e) {
			throw new ValidationException("Entity class with name [%s] not found in the models package".formatted(entityAccess.getName()));	
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("NAME", entityAccess.getName());
		params.put("ID", entityAccess.getId());
		List<BaseO> existingEntities = (List<BaseO>) Repository.query("FROM EntityAccess e where e.name = :NAME and e.id != :ID", params, FlushModeType.COMMIT);
		if (existingEntities != null && existingEntities.size() > 0)
			throw new ValidationException("Entity already registered");
	}

	@Override
	public void validateOnDelete(BaseO baseO) throws Exception {
		// TODO Auto-generated method stub

	}
}
