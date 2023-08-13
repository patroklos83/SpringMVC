package com.patroclos.controller.summary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.*;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.patroclos.controller.core.SummaryController;
import com.patroclos.dto.GroupDTO;
import com.patroclos.uicomponent.core.Input;
import com.patroclos.uicomponent.UIInputType;
import com.patroclos.uicomponent.core.Action;
import com.patroclos.uicomponent.core.ColumnDefinition;
import com.patroclos.uicomponent.core.ColumnDefinitionType;
import com.patroclos.uicomponent.core.DbFieldType;
import com.patroclos.uicomponent.core.Table;

@Controller
public class UserGroupsController extends SummaryController {

	@RequestMapping(value="/usergroups",method=RequestMethod.POST)
	public ModelAndView getPageForm(@RequestParam Map<String,String> searchFilterParams, ModelMap model) throws Exception {       	
		return pageLoad(searchFilterParams, model);
	}

	@RequestMapping(value="/usergroups",method=RequestMethod.GET)    
	public ModelAndView getPageLoad(@RequestParam Map<String,String> searchFilterParams, ModelMap model) throws Exception {       
		return pageLoad(searchFilterParams, model);
	}

	@Override
	protected ModelAndView getPageForm(ModelMap model, String processId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public ModelAndView pageLoad(@RequestParam Map<String,String> searchFilterParams, ModelMap model) throws Exception {
		List<Action> actionList = new ArrayList<Action>();
		Action action = new Action();
		action.setAction("executeProcess('group/new')");
		action.setDescription("New Group");
		action.setExecuteFromJavascript(true);
		actionList.add(action);
		return super.pageLoad(searchFilterParams, model, "User Groups", "usergroups/search", actionList, 
				"/summary/usergroups", null);
	}

	@Override
	protected Table search(Map<String,String> searchParams) throws Exception {

		String sqlQuery = "SELECT ID, NAME, CREATEDBY, CREATEDDATE, LASTMODIFIEDBY, LASTMODIFIEDDATE, LASTUPDATEDBYPROCESSID"
				+ " FROM GROUPS "
				+ " WHERE ISDELETED = 0 ";

		Map<String, ColumnDefinition> columnDefinitions = new HashMap<String, ColumnDefinition>();
		ColumnDefinition articleIdDef = new ColumnDefinition();
		articleIdDef.setType(ColumnDefinitionType.CLICKABLE_LINK);
		articleIdDef.setColumnDbName("Id");
		articleIdDef.setActionLink("index?page=group/?id");
		columnDefinitions.put(articleIdDef.getColumnDbName(), articleIdDef);

		Table table = Table.Builder.newInstance()
				.setTableId("usergroupsTable")
				.setName("usergroupsTable")
				.setSqlQuery(sqlQuery)
				.setColumnDefinitions(columnDefinitions)
				.setInputFilters(getInputFilters())
				.build();

		table = UITable.draw(table, searchParams);
		return table;
	}

	@Override
	protected Map<String,Input> getInputFilters() {
		Input id = UIInput.draw("Id", UIInputType.Text);
		id.setDbField("id");
		id.setDbPrivateKey(true);
		Input name = UIInput.draw("name", UIInputType.Text);
		name.setDbField("name");
		name.setDbFieldType(DbFieldType.Text);

		Map<String,Input> inputFilters = new LinkedHashMap<String,Input>();
		inputFilters.put(id.getName(), id);
		inputFilters.put(name.getName(), name);
		return inputFilters;
	}

	@RequestMapping(value = "/usergroups/search", produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String searchControl(HttpSession session, @RequestParam Map<String,String> allParams) throws Exception {
		return super.searchControl(session, allParams, GroupDTO.class);
	}

	@Override
	protected Table search(String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}