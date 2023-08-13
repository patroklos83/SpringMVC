package com.patroclos.controller.core;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.patroclos.dto.BaseDTO;
import com.patroclos.dto.SummaryDTO;
import com.patroclos.uicomponent.core.Action;
import com.patroclos.uicomponent.core.Input;
import com.patroclos.uicomponent.core.Table;

import jakarta.servlet.http.HttpSession;

public abstract class SummaryController extends BaseController {

	protected abstract ModelAndView getPageForm(@RequestParam Map<String,String> searchFilterParams, ModelMap model) throws Exception;

	protected abstract ModelAndView getPageForm(ModelMap model, @PathVariable("id") String processId) throws Exception;

	protected abstract ModelAndView getPageLoad(@RequestParam Map<String,String> searchFilterParams, ModelMap model) throws Exception;

	protected abstract Table search(Map<String,String> searchParams) throws Exception;
	
	protected abstract Table search(String id) throws Exception;
	
	protected abstract Map<String,Input> getInputFilters();
	
	protected ModelAndView pageLoad(
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
			String formId = UISummaryForm.getComponentId();			
			String formHtml = UISummaryForm.draw(getInputFilters().values().stream().collect(Collectors.toList()));
			
			model.addAttribute("filterForm", formHtml);
			model.addAttribute("formId", formId);
			model.addAttribute("searchPathUrl", searchPathUrl);
		}
		else {
			isForExpandableRow = id != null ? true : false;
			String summaryTableHtml = id != null ? searchControl(id) : searchControl(null , null, null);
			model.addAttribute("summaryTable", summaryTableHtml);
		}
			
		//model.addAttribute("contextPath", WebUtils.getContextPath());
		model.addAttribute("pagetitle", pageTitle);
		model.addAttribute("actionslist", actionList);
		model.addAttribute("showWholeSummaryPage", !isForExpandableRow);

		return super.pageLoad(view, model);
	}
	
	private String searchControl(String id) throws Exception {
		Table table = search(id);
		DataHolder.addDataToMap(table.getId(), table);	
		return table.getHtml();
	}
	
	protected String searchControl(HttpSession session, @RequestParam Map<String,String> allParams, Class<? extends BaseDTO> type) throws Exception {
		Table table = search(allParams);
		
		SummaryDTO dummyDTO = new SummaryDTO();
		dummyDTO.setQueryArgs(allParams);
		dummyDTO.setReferencesEntityDTO(type);
		SummaryDTO resultDTO = (SummaryDTO) Facade.validateSearchAccess(dummyDTO);
		
		DataHolder.addDataToMap(table.getId(), table);	
		return table.getHtml();
	}

	public String summaryTablePaging (@RequestParam Map<String,String> pagingParams, ModelMap model) throws Exception {
		Table table = (Table)DataHolder.getDataFromMap(pagingParams.entrySet().stream()
				.filter(k -> k.toString().startsWith("summaryHash"))
				.findFirst().get().getValue());
		table.setPagingParams(pagingParams);
		table = UITable.draw(table);
		return table.getHtml();
	}
}
