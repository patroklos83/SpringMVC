package com.patroclos.controller.summary;

import java.util.HashMap;

import com.patroclos.controller.core.SummaryController;
import com.patroclos.uicomponent.UIInput.Input;
import com.patroclos.uicomponent.core.ColumnDefinition;
import com.patroclos.uicomponent.core.ColumnDefinitionType;
import com.patroclos.uicomponent.core.Table;

import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ActivityLogsController extends SummaryController{

	@RequestMapping(value="/activitylogs",method=RequestMethod.POST)
	public String getPageForm(@RequestParam Map<String,String> searchFilterParams, ModelMap model) throws Exception {       	
		return pageLoad(searchFilterParams, model);
	}

	@RequestMapping(value="/activitylogs",method=RequestMethod.GET)    
	public String getPageLoad(@RequestParam Map<String,String> searchFilterParams, ModelMap model) throws Exception {       
		return pageLoad(searchFilterParams, model);
	}

	public String pageLoad(@RequestParam Map<String,String> searchFilterParams, ModelMap model) throws Exception {
		return super.pageLoad(searchFilterParams, model, "Activity Audit Logs", null, null, 
				"/summary/activitylogs", null);
	}
	
	@Override
	protected String getPageForm(ModelMap model, String processId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Table search(Map<String, String> searchParams) throws Exception {
		String sqlQuery = "SELECT activitylog.id, activitylog.summary, process as processname, activitylog.result, "
				+ "activitylog.processid, activitylog.error, users.username as user, activitylog.createddate as date"
				+ ",clientip, activitylog.processid as process_id\r\n"
				+ " from activitylog\r\n"
				+ " left join users on users.id = activitylog.createdby\r\n"
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
				.setSqlQuery(sqlQuery)
				.setColumnDefinitions(columnDefinitions)
				.setPagingUrl("activitylogs/summaryPaging")
				.build();
		
		table = UITable.draw(table);
		return table;
	}

	@RequestMapping(value = "/activitylogs/summaryPaging", produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String summaryTablePaging (
			@RequestParam Map<String,String> pagingParams, 
			ModelMap model) {
		return super.summaryTablePaging(pagingParams, model);
	}

	@Override
	protected Map<String, Input> getInputFilters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Table search(String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
