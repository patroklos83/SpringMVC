package com.patroclos.controller.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import com.patroclos.dto.BaseDTO;
import com.patroclos.exception.SystemException;
import com.patroclos.uicomponent.core.Table;
import com.patroclos.uicomponent.core.UIComponent;
import com.patroclos.uicomponent.UILayoutForm.ComponentAlignment;
import com.patroclos.uicomponent.UILayoutForm.ComponentMode;
import com.patroclos.uicomponent.UILayoutForm.LayoutForm;
import com.patroclos.uicomponent.core.Action;

public abstract class PageController extends BaseController {

	protected abstract ModelAndView getPage(ModelMap model, @PathVariable("id") Long id) throws Exception;

	protected abstract ModelAndView postPage(ModelMap model, @PathVariable("id") Long id) throws Exception;

	protected abstract ModelAndView getPageEdit(ModelMap model, @PathVariable("id") Long id) throws Exception;

	protected abstract ModelAndView postPageEdit(ModelMap model, @PathVariable("id") Long id) throws Exception;

	protected abstract ModelAndView getPageNew(ModelMap model) throws Exception;

	protected abstract ModelAndView postPageNew(ModelMap model) throws Exception;

	protected abstract ModelAndView pageLoad(Long id, PageState state, ModelMap model) throws Exception;

	protected ModelAndView pageLoad(Long id, Class<? extends BaseDTO> dtoClassType, PageState state, ModelMap model, String view) throws Exception {

		BaseDTO baseDTO = null;
		String entityName = CustomModelMapper.getModelEntityNameFromDTO(dtoClassType);
		LayoutForm layoutForm = null;
		List<Action> actionList = new ArrayList<Action>();

		String isDisplaySuccessMessage = "true";
		if (state == PageState.Read) {
			baseDTO = (BaseDTO) Facade.load(id, dtoClassType);
			if (baseDTO == null) 
				throw new SystemException(String.format("%s not found!", entityName));

			List<UIComponent> fields = getFields(baseDTO).values().stream().collect(Collectors.toList());
			setTablesInMemory(fields);
			layoutForm = UILayoutForm.draw(fields, ComponentMode.READ, ComponentAlignment.VERTICAL);

			Action action = new Action();
			action.setAction(String.format("execute('%s/edit/%s')", entityName.toLowerCase(), id));
			action.setExecuteFromJavascript(true);
			action.setDescription("Edit");
			actionList.add(action);	
			action = new Action();
			action.setAction(String.format("execute('%s/delete')", entityName.toLowerCase()));
			action.setExecuteFromJavascript(true);
			action.setDescription("Delete");
			actionList.add(action);
		}
		else if (state == PageState.Edit){
			baseDTO = (BaseDTO) Facade.load(id, dtoClassType);		
			if (baseDTO == null) 
				throw new SystemException(String.format("%s not found!", entityName));

			List<UIComponent> fields = getFields(baseDTO).values().stream().collect(Collectors.toList());
			fields.removeIf(i -> i.getName().equals("Id"));
			fields.removeIf(i -> i.getName().equals("createdDate"));
			fields.removeIf(i -> !i.IsEditable());
			setTablesInMemory(fields);
			layoutForm = UILayoutForm.draw(fields, ComponentMode.EDIT, ComponentAlignment.VERTICAL);

			Action action = new Action();
			action.setAction(String.format("execute('%s/update', %s)", entityName.toLowerCase(), isDisplaySuccessMessage));
			action.setExecuteFromJavascript(true);
			action.setDescription("Save");
			actionList.add(action);
			action = new Action();
			action.setAction(String.format("execute('%s/%s')", entityName.toLowerCase(), id));
			action.setExecuteFromJavascript(true);
			action.setDescription("Cancel");
			actionList.add(action);
		}
		else if (state == PageState.New) {
			baseDTO = dtoClassType.getConstructor().newInstance();			

			List<UIComponent> fields = getFields(baseDTO).values().stream().collect(Collectors.toList());
			fields.removeIf(i -> i.getName().equals("Id"));
			fields.removeIf(i -> i.getName().equals("createdDate"));		
			setTablesInMemory(fields);
			layoutForm = UILayoutForm.draw(fields, ComponentMode.EDIT, ComponentAlignment.VERTICAL);

			Action action = new Action();
			action.setAction(String.format("execute('%s/create', %s)", entityName.toLowerCase(), isDisplaySuccessMessage));
			action.setExecuteFromJavascript(true);
			action.setDescription("Save");
			actionList.add(action);
			action = new Action();
			action.setAction("execute('dashboard')");
			action.setExecuteFromJavascript(true);
			action.setDescription("Cancel");
			actionList.add(action);
		}

		model.addAttribute("form", layoutForm.getHtml());
		String hash = UUID.randomUUID().toString();
		model.addAttribute("formId", hash);
		Form form = new Form(hash);
		form.setDto(baseDTO);
		DataHolder.addDataToMap(hash, form);
		model.addAttribute("contextPath", WebUtils.getContextPath());
		model.addAttribute("actionslist", actionList);
		model.addAttribute("pagetitle", String.format("%s #%s", entityName, baseDTO.getId() != null ? baseDTO.getId() : ""));

		//String baseUrl = WebUtils.getBaseUrl();
		//var param = WebUtils.getRequestParam("contenttype");


		if (view.endsWith("?dynamicContent")) {
			model.addAttribute("isStandalonePage", "false");
			view.replace("?dynamicContent", "");
		}
		else
			model.addAttribute("isStandalonePage", "true");

		return super.pageLoad(view, model);
	}

	/***
	 * If form consists of Table elements store them in memory for later use [like paging for example]
	 */
	private void setTablesInMemory(List<UIComponent> fields) {
		if (fields != null && fields.stream().filter(c -> c instanceof Table).count() > 0) {
			fields.stream().filter(f -> f instanceof Table)
			.forEach(t -> DataHolder.addDataToMap(t.getId(), t));
		}
	}

	protected abstract Map<String, UIComponent> getFields(Object o) throws Exception;

	protected abstract ModelAndView saveUpdate(@FormParam Form form, ModelMap model)  throws Exception;

	protected abstract ModelAndView saveNew(@FormParam Form form, ModelMap model)  throws Exception;

	protected abstract ModelAndView delete(@FormParam Form form, ModelMap model)  throws Exception;

	protected ModelAndView saveNewEntity(BaseDTO baseDTO, ModelMap model, String view) throws Exception {
		BaseDTO persistedBaseDTO =  (BaseDTO) Facade.saveNew(baseDTO);
		return new ModelAndView(String.format("redirect:/%s/%s", view, persistedBaseDTO.getId()));
	}

	protected ModelAndView saveUpdateEntity(BaseDTO baseDTO, ModelMap model, String view) throws Exception {
		BaseDTO persistedBaseDTO =  (BaseDTO) Facade.saveUpdate(baseDTO);
		return new ModelAndView(String.format("redirect:/%s/%s", view, persistedBaseDTO.getId()));
	}

	protected ModelAndView deleteEntity(BaseDTO baseDTO, ModelMap model, String view) throws Exception {
		Facade.delete(baseDTO);
		return new ModelAndView(String.format("redirect:/%s", view));
	}
}
