package com.patroclos.businessobject.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.patroclos.model.BaseO;
import com.patroclos.repository.IRepository;

public abstract class BaseBO {
	
	@Autowired
	protected IRepository Repository;
	
	public abstract BaseO load(long id, Class<? extends BaseO> baseO);
	
	public abstract void save(BaseO baseO) throws Exception;
	
	public abstract void delete(BaseO baseO) throws Exception;
	
	public abstract void validateOnSave(BaseO baseO) throws Exception;
	
	public abstract void validateOnDelete(BaseO baseO) throws Exception;
	
	public BaseO loadBaseO(long id, Class<? extends BaseO> baseO) {
		BaseO o = (BaseO) Repository.findById(baseO, id);
		return o;
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
