package com.patroclos.businessobject;

import java.util.List;

import org.springframework.stereotype.Component;

import com.patroclos.businessobject.core.BaseBO;
import com.patroclos.model.ArticleComment;
import com.patroclos.model.BaseO;

@Component
public class ArticleCommentBO extends BaseBO {

	@Override
	public BaseO load(long id, Class<? extends BaseO> baseO) {
		return super.loadBaseO(id, baseO);
	}

	@Override
	public List<BaseO> loadAll() {
		return super.load(ArticleComment.class);
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
