package com.patroclos.controller.core;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.patroclos.uicomponent.UIInput.Input;
import com.patroclos.uicomponent.core.Action;
import com.patroclos.uicomponent.core.Table;

public abstract class SummaryController extends BaseController {

	protected abstract String getPageForm(@RequestParam Map<String,String> searchFilterParams, ModelMap model) throws Exception;

	protected abstract String getPageForm(ModelMap model, @PathVariable("id") String processId) throws Exception;

	protected abstract String getPageLoad(@RequestParam Map<String,String> searchFilterParams, ModelMap model) throws Exception;

	protected abstract Table search(Map<String,String> searchParams) throws Exception;
	
	protected abstract Table search(String id) throws Exception;
	
	protected abstract Map<String,Input> getInputFilters();
	
	protected String pageLoad(
			@RequestParam Map<String,String> searchFilterParams, 
			ModelMap model, 
			String pageTitle,
			String searchPathUrl, 
			List<Action> actionList,
			String view,
			String id) throws Exception {

		var inputFilters = getInputFilters();
		boolean isForExpandableRow = false;
		if (inputFilters != null && inputFilters.size() > 0) {
			String formHtml = UIForm.draw(searchPathUrl, "Search",
					getInputFilters().values().stream().collect(Collectors.toList()), "summaryTable");
			model.addAttribute("filterForm", formHtml);
		}
		else {
			isForExpandableRow = id != null ? true : false;
			String summaryTableHtml = id != null ? searchControl(id) : searchControl(null , null);
			model.addAttribute("summaryTable", summaryTableHtml);
		}
			
		model.addAttribute("pagetitle", pageTitle);
		model.addAttribute("actionslist", actionList);
		model.addAttribute("showWholeSummaryPage", !isForExpandableRow);

		return view;
	}
	
	private String searchControl(String id) throws Exception {
		Table table = search(id);
		DataHolder.addDataToMap(table.getId(), table);	
		return table.getHtml();
	}
	
	protected String searchControl(HttpSession session, @RequestParam Map<String,String> allParams) throws Exception {
		Table table = search(allParams);
		DataHolder.addDataToMap(table.getId(), table);	
		return table.getHtml();
	}

	public String summaryTablePaging (
			@RequestParam Map<String,String> pagingParams, 
			ModelMap model) {
		Table table = (Table)DataHolder.getDataFromMap(pagingParams.entrySet().stream()
				.filter(k -> k.toString().startsWith("summaryHash"))
				.findFirst().get().getValue());
		table.setPagingParams(pagingParams);
		table = UITable.draw(table);
		return table.getHtml();
	}
}
