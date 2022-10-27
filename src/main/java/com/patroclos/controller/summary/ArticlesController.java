package com.patroclos.controller.summary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.patroclos.controller.core.SummaryController;
import com.patroclos.uicomponent.UIInputType;
import com.patroclos.uicomponent.UIInput.Input;
import com.patroclos.uicomponent.core.Action;
import com.patroclos.uicomponent.core.ColumnDefinition;
import com.patroclos.uicomponent.core.ColumnDefinitionType;
import com.patroclos.uicomponent.core.DbFieldType;
import com.patroclos.uicomponent.core.Table;

@Controller
public class ArticlesController extends SummaryController {

	@RequestMapping(value="/articles",method=RequestMethod.POST)
	public String getPageForm(@RequestParam Map<String,String> searchFilterParams, ModelMap model) throws Exception {       	
		return pageLoad(searchFilterParams, model);
	}

	@RequestMapping(value="/articles",method=RequestMethod.GET)    
	public String getPageLoad(@RequestParam Map<String,String> searchFilterParams, ModelMap model) throws Exception {       
		return pageLoad(searchFilterParams, model);
	}
	
	@Override
	protected String getPageForm(ModelMap model, String processId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public String pageLoad(@RequestParam Map<String,String> searchFilterParams, ModelMap model) throws Exception {
		List<Action> actionList = new ArrayList<Action>();
		Action action = new Action();
		action.setAction("executeProcess('article/new')");
		action.setDescription("New Article");
		action.setExecuteFromJavascript(true);
		actionList.add(action);
		return super.pageLoad(searchFilterParams, model, "Search Articles", "articles/search", actionList, 
				"/summary/articles", null);
	}

	@Override
	protected Table search(Map<String,String> searchParams) throws Exception {

		String sqlQuery = "select articles.Id, title, category, author, users.username as createdbyuser"
				+ " from articles "
				+ " inner join users on users.id=articles.createdby"
				+ " where articles.isdeleted = 0 ";
		
		Map<String, ColumnDefinition> columnDefinitions = new HashMap<String, ColumnDefinition>();
		ColumnDefinition articleIdDef = new ColumnDefinition();
		articleIdDef.setType(ColumnDefinitionType.CLICKABLE_LINK);
		articleIdDef.setColumnDbName("Id");
		articleIdDef.setActionLink("index?page=article/?id");
		columnDefinitions.put(articleIdDef.getColumnDbName(), articleIdDef);
		
		Table table = Table.Builder.newInstance()
				.setTableId("articlesTable")
				.setSqlQuery(sqlQuery)
				.setColumnDefinitions(columnDefinitions)
				.setInputFilters(getInputFilters())
				.setPagingUrl("articles/summaryPaging")
				.build();
		
		table = UITable.draw(table, searchParams);
		return table;
	}

	@Override
	protected Map<String,Input> getInputFilters() {
		Input articleId = UIInput.draw("Id", UIInputType.Text);
		articleId.setDbField("articles.id");
		articleId.setDbPrivateKey(true);
		Input articleTitle = UIInput.draw("articleTitle", UIInputType.Text);
		articleTitle.setDbField("articles.title");
		articleTitle.setDbFieldType(DbFieldType.Text);
		Input createdDateFrom = UIInput.draw("createdDate", UIInputType.DateTime);
		createdDateFrom.setDbField("articles.createdDate");
		
		Map<String,Input> inputFilters = new LinkedHashMap<String,Input>();
		inputFilters.put(articleId.getName(), articleId);
		inputFilters.put(articleTitle.getName(), articleTitle);
		inputFilters.put(createdDateFrom.getName(), createdDateFrom);
		return inputFilters;
	}

	@RequestMapping(value = "/articles/search", produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String searchControl(HttpSession session, @RequestParam Map<String,String> allParams) throws Exception {
		return super.searchControl(session, allParams);
	}
		
	@RequestMapping(value = "/articles/summaryPaging", produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String summaryTablePaging (
			@RequestParam Map<String,String> pagingParams, 
			ModelMap model) throws Exception {
	    return super.summaryTablePaging(pagingParams, model);
	}

	@Override
	protected Table search(String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}