package com.patroclos.businessobject;

import java.util.List;

import com.patroclos.businessobject.core.BaseBO;
import com.patroclos.model.BaseO;
import com.patroclos.model.RoleEntityAccess;

import org.springframework.stereotype.Component;

@Component
public class RoleEntityAccessBO extends BaseBO {

	@Override
	public BaseO load(long id, Class<? extends BaseO> baseO) {
		return super.loadBaseO(id, baseO);
	}

	@Override
	public List<BaseO> loadAll() {
		return super.load(RoleEntityAccess.class);
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
