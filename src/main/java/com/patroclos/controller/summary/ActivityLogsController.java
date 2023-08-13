package com.patroclos.controller.summary;

import java.util.HashMap;
import java.util.LinkedHashMap;
import com.patroclos.uicomponent.UIInputType;
import com.patroclos.uicomponent.core.ColumnDefinition;
import com.patroclos.uicomponent.core.ColumnDefinitionType;
import com.patroclos.uicomponent.core.DbFieldType;
import com.patroclos.uicomponent.core.Table;

import jakarta.servlet.http.HttpSession;

import com.patroclos.controller.core.SummaryController;
import com.patroclos.dto.ActivityLogDTO;
import com.patroclos.uicomponent.core.Input;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ActivityLogsController extends SummaryController {

	@RequestMapping(value="/activitylogs",method=RequestMethod.POST)
	public ModelAndView getPageForm(@RequestParam Map<String,String> searchFilterParams, ModelMap model) throws Exception {       	
		return pageLoad(searchFilterParams, model);
	}

	@RequestMapping(value="/activitylogs",method=RequestMethod.GET)    
	public ModelAndView getPageLoad(@RequestParam Map<String,String> searchFilterParams, ModelMap model) throws Exception {       
		return pageLoad(searchFilterParams, model);
	}

	public ModelAndView pageLoad(@RequestParam Map<String,String> searchFilterParams, ModelMap model) throws Exception {
		return super.pageLoad(searchFilterParams, model, "Activity Audit Logs", "activitylogs/search", null, 
				"/summary/activitylogs", null);
	}
	
	@Override
	protected ModelAndView getPageForm(ModelMap model, String processId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Table search(Map<String, String> searchParams) throws Exception {
		String sqlQuery = "SELECT activitylog.id, activitylog.summary, activitylog.process as processname, activitylog.result, "
				+ "activitylog.processid, activitylog.error, users.username, activitylog.createddate as date"
				+ ",activitylog.clientip, activitylog.processid as process_id"
				+ " from activitylog"
				+ " left join users on users.id = activitylog.createdby"
				+ " where activitylog.isdeleted = 0"
				+ " order by activitylog.id desc";
		
		Map<String, ColumnDefinition> columnDefinitions = new HashMap<String, ColumnDefinition>();
		ColumnDefinition processIdDef = new ColumnDefinition();
		processIdDef.setType(ColumnDefinitionType.EXPANDABLE_ROW);
		processIdDef.setColumnDbName("processId");
		processIdDef.setColumnAlias("Entities Affected");
		processIdDef.setExpandableRowActionLink("activitylogdetails/?id");
		columnDefinitions.put(processIdDef.getColumnDbName(), processIdDef);
		
		Table table = Table.Builder.newInstance()
				.setTableId("activitylogTable")
				.setName("activitylogTable")
				.setSqlQuery(sqlQuery)
				.setColumnDefinitions(columnDefinitions)
				.setInputFilters(getInputFilters())
				.build();;
		
		table = UITable.draw(table, searchParams);
		return table;
	}

	@Override
	protected Map<String, Input> getInputFilters() {		
		Input id = UIInput.draw("Id", UIInputType.Text);
		id.setDbField("activitylog.id");
		id.setDbPrivateKey(true);
		Input processName = UIInput.draw("Process Name", UIInputType.Text);
		processName.setDbField("activitylog.process");
		processName.setDbFieldType(DbFieldType.Text);
		Input createdDateFrom = UIInput.draw("Execution Date", UIInputType.DateTime);
		createdDateFrom.setDbField("activitylog.createddate");

		Map<String,Input> inputFilters = new LinkedHashMap<String,Input>();
		inputFilters.put(id.getName(), id);
		inputFilters.put(processName.getName(), processName);
		inputFilters.put(createdDateFrom.getName(), createdDateFrom);
		
		return inputFilters;
	}

	@Override
	protected Table search(String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@RequestMapping(value = "/activitylogs/search", produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String searchControl(HttpSession session, @RequestParam Map<String,String> allParams) throws Exception {
		return super.searchControl(session, allParams, ActivityLogDTO.class);
	}

}
