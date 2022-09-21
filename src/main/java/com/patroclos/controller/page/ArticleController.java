package com.patroclos.controller.page;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.patroclos.configuration.*;
import com.patroclos.controller.core.Form;
import com.patroclos.controller.core.FormParam;
import com.patroclos.controller.core.PageController;
import com.patroclos.controller.core.PageState;
import com.patroclos.dto.ArticleDTO;
import com.patroclos.model.Article;
import com.patroclos.model.BaseO;
import com.patroclos.uicomponent.UIInputType;
import com.patroclos.uicomponent.UIInput.Input;

import io.micrometer.core.annotation.Timed;

@Controller
public class ArticleController extends PageController {

	@RequestMapping(value="/article/{id}", method=RequestMethod.POST)
	@Override
	protected String postPage(ModelMap model, @PathVariable("id") Long id) throws Exception {       	
		return pageLoad(id, PageState.Read, model);
	}

	@RequestMapping(value="/article/{id}", method=RequestMethod.GET) 
	@Override
	protected String getPage(ModelMap model, @PathVariable("id") Long id) throws Exception {       
		return pageLoad(id, PageState.Read, model);
	}

	@RequestMapping(value="/article/edit/{id}", method=RequestMethod.POST)
	@Override
	protected String postPageEdit(ModelMap model, @PathVariable("id") Long id) throws Exception {       	
		return pageLoad(id, PageState.Edit, model);
	}

	@RequestMapping(value="/article/edit/{id}", method=RequestMethod.GET)    
	@Override
	protected String getPageEdit(ModelMap model, @PathVariable("id") Long id) throws Exception {       
		return pageLoad(id, PageState.Edit, model);
	}

	@RequestMapping(value="/article/new", method=RequestMethod.POST)
	@Override
	protected String postPageNew(ModelMap model) throws Exception {       	
		return pageLoad(0L, PageState.New, model);
	}

	@RequestMapping(value="/article/new", method=RequestMethod.GET)
	@Override
	protected String getPageNew(ModelMap model) throws Exception {       	
		return pageLoad(0L, PageState.New, model);
	}

	public String pageLoad(Long id, PageState state, ModelMap model) throws Exception {
		return super.pageLoad(id, ArticleDTO.class, state, model, "/pages/article");
	}

	@Override
	public Map<String,Input> getFields(Object o) {
		ArticleDTO article = (ArticleDTO)o;
		Input articleId = UIInput.draw("Id", article.getId(), UIInputType.Text);
		Input articleTitle = UIInput.draw("title", article.getTitle(), UIInputType.Text);
		Input articleAuthor = UIInput.draw("author", article.getAuthor(), UIInputType.Text);
		Input articleCategory = UIInput.draw("category", article.getCategory(), UIInputType.Text);
		Input articleSummary = UIInput.draw("summary", article.getSummary(), UIInputType.TextArea);

		Map<String,Input> fields = new LinkedHashMap<String,Input>();
		fields.put(articleId.getName(), articleId);
		fields.put(articleTitle.getName(), articleTitle);
		fields.put(articleAuthor.getName(), articleAuthor);
		fields.put(articleCategory.getName(), articleCategory);
		fields.put(articleSummary.getName(), articleSummary);
		return fields;
	}

	@RequestMapping(value="/article/update", method=RequestMethod.POST)
	protected String saveUpdate(@FormParam Form form, ModelMap model) throws Exception { 
		ArticleDTO articleDTO = (ArticleDTO) form.getDto();
		ArticleDTO dirtyArticle = (ArticleDTO) form.getDirtyDto();
		articleDTO.setTitle(dirtyArticle.getTitle());
		articleDTO.setAuthor(dirtyArticle.getAuthor());
		articleDTO.setSummary(dirtyArticle.getSummary());
		articleDTO.setCategory(dirtyArticle.getCategory());
		return super.saveUpdateEntity(articleDTO, model, "article");
	}
	
	@RequestMapping(value="/article/create", method=RequestMethod.POST)
	protected String saveNew(@FormParam Form form, ModelMap model) throws Exception { 
		ArticleDTO articleDTO = (ArticleDTO) form.getDto();
		ArticleDTO dirtyArticle = (ArticleDTO) form.getDirtyDto();
		articleDTO.setTitle(dirtyArticle.getTitle());
		articleDTO.setAuthor(dirtyArticle.getAuthor());
		articleDTO.setSummary(dirtyArticle.getSummary());
		articleDTO.setCategory(dirtyArticle.getCategory());
		return super.saveNewEntity(articleDTO, model, "article");
	}

	@RequestMapping(value="/article/delete", method=RequestMethod.POST)
	protected String delete(@FormParam Form form, ModelMap model) throws Exception {   	
		ArticleDTO articleDTO = (ArticleDTO) form.getDto();
		return super.deleteEntity(articleDTO, model, "articles");
	}
}