package com.patroclos.businessobject.core;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.patroclos.model.BaseO;
import com.patroclos.repository.IRepository;
import  com.patroclos.utils.CustomModelMapper;

public abstract class BaseBO {

	@Autowired
	protected IRepository Repository;

	@Autowired
	protected CustomModelMapper CustomModelMapper;

	public abstract BaseO load(long id, Class<? extends BaseO> baseO);

	public abstract List<BaseO> loadAll();

	public abstract void save(BaseO baseO) throws Exception;
	
	public abstract void saveAll(List<BaseO> baseOs) throws Exception;

	public abstract void delete(BaseO baseO) throws Exception;

	public abstract void validateOnSave(BaseO baseO) throws Exception;

	public abstract void validateOnDelete(BaseO baseO) throws Exception;

	public BaseO loadBaseO(long id, Class<? extends BaseO> baseO) {
		BaseO o = (BaseO) Repository.findById(baseO, id);
		return o;
	}

	public List<BaseO> load(Class<? extends BaseO> baseO) {
		@SuppressWarnings("unchecked")
		List<BaseO> list = (List<BaseO>) Repository.query("SELECT A FROM " + baseO.getSimpleName() + " A where A.isDeleted=0", new HashMap<String, Object>());
		return list;
	}

	@Transactional
	public void saveAllBaseO(List<BaseO> baseOs) throws Exception {
		if (baseOs == null || baseOs.size() == 0) return;
		for (BaseO baseO : baseOs) {		
			validateOnSave(baseO);
			Repository.save(baseO);	
		}
	}

	@Transactional
	public void saveBaseO(BaseO baseO) throws Exception {
		validateOnSave(baseO);
		Repository.save(baseO);		
	}

	@Transactional
	public void deleteBaseO(BaseO baseO) throws Exception {
		validateOnDelete(baseO);
		baseO.setIsDeleted(1);
		Repository.save(baseO);		
	}
}
