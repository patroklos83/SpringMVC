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
import com.patroclos.dto.EntityAccessDTO;
import com.patroclos.uicomponent.core.Input;
import com.patroclos.uicomponent.UIInputType;
import com.patroclos.uicomponent.core.Action;
import com.patroclos.uicomponent.core.ColumnDefinition;
import com.patroclos.uicomponent.core.ColumnDefinitionType;
import com.patroclos.uicomponent.core.DbFieldType;
import com.patroclos.uicomponent.core.Table;

@Controller
public class EntitiesAccessController extends SummaryController {

	@RequestMapping(value="/entitiesaccess",method=RequestMethod.POST)
	public ModelAndView getPageForm(@RequestParam Map<String,String> searchFilterParams, ModelMap model) throws Exception {       	
		return pageLoad(searchFilterParams, model);
	}

	@RequestMapping(value="/entitiesaccess",method=RequestMethod.GET)    
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
		action.setAction("executeProcess('entityaccess/new')");
		action.setDescription("New Entity");
		action.setExecuteFromJavascript(true);
		actionList.add(action);
		return super.pageLoad(searchFilterParams, model, "Entities", "entitiesaccess/search", actionList, 
				"/summary/entitiesaccess", null);
	}

	@Override
	protected Table search(Map<String,String> searchParams) throws Exception {

		String sqlQuery = "SELECT ID, NAME, CREATEDBY, CREATEDDATE, LASTMODIFIEDBY, LASTMODIFIEDDATE, LASTUPDATEDBYPROCESSID"
				+ " FROM ENTITYACCESS "
				+ " WHERE ISDELETED = 0 ";

		Map<String, ColumnDefinition> columnDefinitions = new HashMap<String, ColumnDefinition>();
		ColumnDefinition roleIdColDef = new ColumnDefinition();
		roleIdColDef.setType(ColumnDefinitionType.CLICKABLE_LINK);
		roleIdColDef.setColumnDbName("Id");
		roleIdColDef.setActionLink("index?page=entityaccess/?id");
		columnDefinitions.put(roleIdColDef.getColumnDbName(), roleIdColDef);

		Table table = Table.Builder.newInstance()
				.setTableId("entitiesTable")
				.setName("entitiesTable")
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

	@RequestMapping(value = "/entitiesaccess/search", produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String searchControl(HttpSession session, @RequestParam Map<String,String> allParams) throws Exception {
		return super.searchControl(session, allParams, EntityAccessDTO.class);
	}

	@Override
	protected Table search(String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}