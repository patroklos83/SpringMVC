package com.patroclos.controller.summary;

import java.util.HashMap;

import com.patroclos.controller.core.SummaryController;
import com.patroclos.uicomponent.UIInput.Input;
import com.patroclos.uicomponent.core.*;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ActivityLogDetailsController extends SummaryController{

	@RequestMapping(value="/activitylogdetails/{id}",method=RequestMethod.POST)
	public String getPageForm(ModelMap model, @PathVariable("id") String id) throws Exception {       	
		return pageLoad(id, model);
	}

	@RequestMapping(value="/activitylogdetails/{id}",method=RequestMethod.GET)    
	public String getPageLoad(ModelMap model, @PathVariable("id") String id) throws Exception {       
		return pageLoad(id, model);
	}
	
	@Override
	protected String getPageForm(Map<String, String> searchFilterParams, ModelMap model) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getPageLoad(Map<String, String> searchFilterParams, ModelMap model) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public String pageLoad(String id, ModelMap model) throws Exception {
		return super.pageLoad(null, model,  "Activity Audit Details Logs", null, null, 
				"/summary/activitylogdetails", id);
	}

	@Override
	protected Table search(String id) throws Exception {
		
		String sqlQuery = "SELECT activitylogdetails.id, activitylogdetails.entity, activitylogdetails.entityid, activitylogdetails.entityrevision \r\n"
				+ " from activitylogdetails"
				+ " inner join users on users.id = activitylogdetails.createdby\r\n"
				+ " where upper(activitylogdetails.processid) = upper(:processid) and activitylogdetails.isdeleted = 0"
				+ " order by activitylogdetails.id desc";

		Map<String, ColumnDefinition> columnDefinitions = new HashMap<String, ColumnDefinition>();
		ColumnDefinition activityLogDetailsIdDef = new ColumnDefinition();
		activityLogDetailsIdDef.setType(ColumnDefinitionType.CLICKABLE_LINK);
		activityLogDetailsIdDef.setColumnDbName("Id");
		activityLogDetailsIdDef.setActionLink("index?page=activitylog/?id");
		columnDefinitions.put(activityLogDetailsIdDef.getColumnDbName(), activityLogDetailsIdDef);

		ColumnDefinition processIdDef = new ColumnDefinition();
		processIdDef.setColumnDbName("processid");
		processIdDef.setType(ColumnDefinitionType.EXPANDABLE_ROW);
		processIdDef.setValue(id);
		columnDefinitions.put(processIdDef.getColumnDbName(), processIdDef);
		
		Table table = Table.Builder.newInstance()
				.setTableId("activitylogTable")
				.setSqlQuery(sqlQuery)
				.setColumnDefinitions(columnDefinitions)
				.setPagingUrl("activitylogdetails/summaryPaging")
				.build();

		table = UITable.draw(table);
		return table;
	}

	@RequestMapping(value = "/activitylogdetails/summaryPaging", produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String summaryTablePaging (
			@RequestParam Map<String,String> pagingParams, 
			ModelMap model) throws Exception {
		return super.summaryTablePaging(pagingParams, model);
	}

	@Override
	protected Table search(Map<String, String> searchParams) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Map<String, Input> getInputFilters() {
		// TODO Auto-generated method stub
		return null;
	}

}
