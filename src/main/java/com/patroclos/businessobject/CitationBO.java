package com.patroclos.businessobject;

import java.util.List;

import org.springframework.stereotype.Component;

import com.patroclos.businessobject.core.BaseBO;
import com.patroclos.model.BaseO;
import com.patroclos.model.Citation;

@Component
public class CitationBO extends BaseBO {

	@Override
	public BaseO load(long id, Class<? extends BaseO> baseO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BaseO> loadAll() {
		return super.load(Citation.class);
	}

	@Override
	public void save(BaseO baseO) throws Exception {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void saveAll(List<BaseO> baseOs) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(BaseO baseO) throws Exception {
		// TODO Auto-generated method stub

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
