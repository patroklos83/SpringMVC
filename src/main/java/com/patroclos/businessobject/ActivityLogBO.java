package com.patroclos.businessobject;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.patroclos.businessobject.core.BaseBO;
import com.patroclos.model.BaseO;

@Component
public class ActivityLogBO extends BaseBO {

	@Override
	public BaseO load(long id, Class<? extends BaseO> baseO) {
		return super.loadBaseO(id, baseO);
	}

	@SuppressWarnings("unchecked")
	public BaseO loadByProcessId(String processId) {
		String hql = "FROM ActivityLog A WHERE A.processId = :processId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("processId", processId);
		List<BaseO> results = (List<BaseO>) Repository.query(hql, params);
		if (results == null) {
			return null;
		}
		return results.get(0);
	}

	@Transactional
	@Override
	public void save(BaseO baseO) throws Exception {
		super.saveBaseO(baseO);		
	}

	@Override
	public void validateOnSave(BaseO baseO) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(BaseO baseO) throws Exception {
		super.deleteBaseO(baseO);
		
	}

	@Override
	public void validateOnDelete(BaseO baseO) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
