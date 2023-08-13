package com.patroclos.businessobject;

import java.util.List;

import org.springframework.stereotype.Component;

import com.patroclos.businessobject.core.BaseBO;
import com.patroclos.exception.ValidationException;
import com.patroclos.model.Article;
import com.patroclos.model.BaseO;

@Component
public class ArticleBO extends BaseBO {

	@Override
	public BaseO load(long id, Class<? extends BaseO> input) {
		return super.loadBaseO(id, input);
	}

	@Override
	public List<BaseO> loadAll() {
		return super.load(Article.class);
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
	public void validateOnSave(BaseO baseO) throws Exception {
		Article article = (Article)baseO;
		if (article.getAuthor() == null || article.getAuthor().isEmpty())
			throw new ValidationException("Author is empty!");
		if (article.getCategory()== null || article.getAuthor().isEmpty())
			throw new ValidationException("Category is empty!");		
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
