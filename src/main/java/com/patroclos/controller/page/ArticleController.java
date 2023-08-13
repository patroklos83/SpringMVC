package com.patroclos.controller.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.patroclos.controller.core.Form;
import com.patroclos.controller.core.FormParam;
import com.patroclos.controller.core.PageController;
import com.patroclos.controller.core.PageState;
import com.patroclos.dto.ArticleCommentDTO;
import com.patroclos.dto.ArticleDTO;
import com.patroclos.dto.BaseDTO;
import com.patroclos.dto.CitationDTO;
import com.patroclos.uicomponent.core.Input;
import com.patroclos.uicomponent.core.Table;
import com.patroclos.uicomponent.core.UIComponent;
import com.patroclos.uicomponent.UIInputType;
import com.patroclos.uicomponent.UILayoutForm.ComponentAlignment;
import com.patroclos.uicomponent.UILayoutForm.ComponentMode;
import com.patroclos.uicomponent.UILayoutForm.LayoutForm;

@Controller
@RequestMapping("article")
public class ArticleController extends PageController {

	@RequestMapping(value="/{id}", method=RequestMethod.POST)
	@Override
	protected ModelAndView postPage(ModelMap model, @PathVariable("id") Long id) throws Exception {       	
		return pageLoad(id, PageState.Read, model);
	}

	@RequestMapping(value="/{id}", method=RequestMethod.GET) 
	@Override
	protected ModelAndView getPage(ModelMap model, @PathVariable("id") Long id) throws Exception {       
		return pageLoad(id, PageState.Read, model);
	}

	@RequestMapping(value="/edit/{id}", method=RequestMethod.POST)
	@Override
	protected ModelAndView postPageEdit(ModelMap model, @PathVariable("id") Long id) throws Exception {       	
		return pageLoad(id, PageState.Edit, model);
	}

	@RequestMapping(value="/edit/{id}", method=RequestMethod.GET)    
	@Override
	protected ModelAndView getPageEdit(ModelMap model, @PathVariable("id") Long id) throws Exception {       
		return pageLoad(id, PageState.Edit, model);
	}

	@RequestMapping(value="/new", method=RequestMethod.POST)
	@Override
	protected ModelAndView postPageNew(ModelMap model) throws Exception {       	
		return pageLoad(0L, PageState.New, model);
	}

	@RequestMapping(value="/new", method=RequestMethod.GET)
	@Override
	protected ModelAndView getPageNew(ModelMap model) throws Exception {       	
		return pageLoad(0L, PageState.New, model);
	}

	public ModelAndView pageLoad(Long id, PageState state, ModelMap model) throws Exception {
		return super.pageLoad(id, ArticleDTO.class, state, model, "/pages/article");
	}

	@Override
	public Map<String, UIComponent> getFields(Object o) throws Exception {
		ArticleDTO article = (ArticleDTO)o;
		Input articleId = UIInput.draw("Id", article.getId(), UIInputType.Text);
		Input articleTitle = UIInput.draw("title", article.getTitle(), UIInputType.Text);
		Input articleAuthor = UIInput.draw("author", article.getAuthor(), UIInputType.Text);
		Input articleCategory = UIInput.draw("category", article.getCategory(), UIInputType.Text);
		Input articleSummary = UIInput.draw("summary", article.getSummary(), UIInputType.TextArea);		

		Table citations = Table.Builder.newInstance()
				.setTableDescription("citations")
				.setName("citations")
				.setIsEditable(true)
				.addEditBehavior()
					.setAddButtonUILayoutTitle("Select Citations")
					.done()
				.setDatalist(article.getCitations())
				.setDatalistType(CitationDTO.class)
				.setTableId("citations")
				.build();
		citations = UIDataTable.draw(citations);

		Table comments = Table.Builder.newInstance()
				.setTableDescription("comments")
				.setName("comments")
				.setIsEditable(true)
				.addEditBehavior()
					.setAddButtonActionUrl("article/confirmAddNewComment")
					.setAddButtonGetUILayoutUrl("article/getAddNewCommentLayout")
					.setAddButtonUILayoutTitle("New Comment")
					.done()
				.setDatalist(article.getComments())
				.setDatalistType(ArticleCommentDTO.class)
				.setTableId("comments")
				.build();
		comments = UIDataTable.draw(comments);

		Map<String, UIComponent> fields = new LinkedHashMap<String, UIComponent>();
		fields.put(articleId.getName(), articleId);
		fields.put(articleTitle.getName(), articleTitle);
		fields.put(articleAuthor.getName(), articleAuthor);
		fields.put(articleCategory.getName(), articleCategory);
		fields.put(articleSummary.getName(), articleSummary);
		fields.put("citations", citations);
		fields.put("comments", comments);

		return fields;
	}

	@RequestMapping(value="/getAddNewCommentLayout", produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	protected String getAddNewCommentLayout(@RequestParam Map<String,String> params) throws Exception {   	
		Input commentTextBox = UIInput.draw("comment", "", UIInputType.Text);
		List<UIComponent> commentAddButtonFields = new ArrayList<UIComponent>();
		commentAddButtonFields.add(commentTextBox);
		LayoutForm addCommentLayout = UILayoutForm.draw(commentAddButtonFields, ComponentMode.EDIT, ComponentAlignment.VERTICAL);
		
		return addCommentLayout.getHtml();
	}

	@RequestMapping(value="/confirmAddNewComment", produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	protected String addNewComment(@RequestParam Map<String,String> params, @FormParam Form form) throws Exception {   	
		
		String commentValue = params.get("comment");

		Table table = getTableFromMemoryByParams(params);

		BaseDTO dirtyDTO = form.getDirtyDto();

		ArticleDTO article = (ArticleDTO)dirtyDTO;
		if (article.getComments() == null) {
			article.setComments(new ArrayList<ArticleCommentDTO>());
		}

		ArticleCommentDTO newComment = new ArticleCommentDTO();
		newComment.setComment(commentValue);
		article.getComments().add(newComment);

		article.setComments(article.getComments());

		table.setDatalist(article.getComments());
		
		form.setDirtyDto(dirtyDTO);
		DataHolder.addDataToMap(form.getHash(), form);
		
		return redrawTableAndSave(table);
	}

	@RequestMapping(value="/update", method=RequestMethod.POST)
	protected ModelAndView saveUpdate(@FormParam Form form, ModelMap model) throws Exception { 
		ArticleDTO articleDTO = (ArticleDTO) form.getDto();
		ArticleDTO dirtyArticle = (ArticleDTO) form.getDirtyDto();
		articleDTO.setTitle(dirtyArticle.getTitle());
		articleDTO.setAuthor(dirtyArticle.getAuthor());
		articleDTO.setSummary(dirtyArticle.getSummary());
		articleDTO.setCategory(dirtyArticle.getCategory());
		articleDTO.setCitations(dirtyArticle.getCitations());		
		articleDTO.setComments(dirtyArticle.getComments());

		return super.saveUpdateEntity(articleDTO, model, "article");
	}

	@RequestMapping(value="/create", method=RequestMethod.POST)
	protected ModelAndView saveNew(@FormParam Form form, ModelMap model) throws Exception { 
		ArticleDTO articleDTO = (ArticleDTO) form.getDto();
		ArticleDTO dirtyArticle = (ArticleDTO) form.getDirtyDto();
		articleDTO.setTitle(dirtyArticle.getTitle());
		articleDTO.setAuthor(dirtyArticle.getAuthor());
		articleDTO.setSummary(dirtyArticle.getSummary());
		articleDTO.setCategory(dirtyArticle.getCategory());
		articleDTO.setCitations(dirtyArticle.getCitations());
		articleDTO.setComments(dirtyArticle.getComments());

		return super.saveNewEntity(articleDTO, model, "article");
	}

	@RequestMapping(value="/delete", method=RequestMethod.POST)
	protected ModelAndView delete(@FormParam Form form, ModelMap model) throws Exception {   	
		ArticleDTO articleDTO = (ArticleDTO) form.getDto();
		return super.deleteEntity(articleDTO, model, "articles");
	}

}