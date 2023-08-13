package com.patroclos.businessobject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.patroclos.businessobject.core.BaseBO;
import com.patroclos.model.BaseO;
import com.patroclos.model.Group;
import com.patroclos.model.User;

import org.springframework.stereotype.Component;

@Component
public class GroupBO extends BaseBO {

	@SuppressWarnings("unchecked")
	@Override
	public BaseO load(long id, Class<? extends BaseO> baseO) {
		Group group = (Group) super.loadBaseO(id, baseO);
		if (group != null) {
			List<User> usersOfGroup = getGroupUsers(group);
			group.setUsers(usersOfGroup);
		}
		
		return group;
	}

	@Override
	public List<BaseO> loadAll() {
		return super.load(Group.class);
	}

	@Override
	public void save(BaseO baseO) throws Exception {
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

	@Override
	public void validateOnSave(BaseO baseO) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateOnDelete(BaseO baseO) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@SuppressWarnings("unchecked")
	public List<User> getGroupUsers(Group group) {	
		if (group == null)
			return null;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("groupId", group.getId());
		List<User> usersOfGroup = (List<User>) Repository.query("FROM User s JOIN s.groups g WHERE g.id = :groupId", params);

		return usersOfGroup;
	}
}
