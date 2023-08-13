package com.patroclos.businessobject;

import java.util.List;

import com.patroclos.businessobject.core.BaseBO;
import com.patroclos.model.BaseO;
import com.patroclos.model.Privilege;
import org.springframework.stereotype.Component;

@Component
public class PrivilegeBO extends BaseBO {
	
	public static final String PRIVILEGE_NAME_SEARCH= "SEARCH";
	public static final String PRIVILEGE_NAME_CREATE = "CREATE";
	public static final String PRIVILEGE_NAME_READ = "READ";
	public static final String PRIVILEGE_NAME_UPDATE = "UPDATE";
	public static final String PRIVILEGE_NAME_DELETE = "DELETE";
	public static final String PRIVILEGE_NAME_CANCEL = "CANCEL";

	@Override
	public BaseO load(long id, Class<? extends BaseO> baseO) {
		return super.loadBaseO(id, baseO);
	}

	@Override
	public List<BaseO> loadAll() {
		return super.load(Privilege.class);
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
}
