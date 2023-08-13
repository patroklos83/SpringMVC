package com.patroclos.controller.page;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.patroclos.controller.core.Form;
import com.patroclos.controller.core.PageController;
import com.patroclos.controller.core.PageState;
import com.patroclos.dto.ActivityLogDetailDTO;
import com.patroclos.dto.AuditedPropertyDTO;
import com.patroclos.model.enums.AuditedPropertyFieldType;
import com.patroclos.uicomponent.UIInputType;
import com.patroclos.uicomponent.core.Table;
import com.patroclos.uicomponent.core.UIComponent;
import com.patroclos.uicomponent.UILayoutForm.ComponentAlignment;
import com.patroclos.uicomponent.UILayoutForm.ComponentMode;
import com.patroclos.uicomponent.UILayoutForm.LayoutForm;

@Controller
@RequestMapping("activitylog")
public class ActivityLogController extends PageController{

	@RequestMapping(value="/{id}",method=RequestMethod.POST)
	public ModelAndView postPage(ModelMap model, @PathVariable("id") Long id) throws Exception {       	
		return pageLoad(id, PageState.Read, model);
	}

	@RequestMapping(value="/{id}",method=RequestMethod.GET)    
	public ModelAndView getPage(ModelMap model, @PathVariable("id") Long id) throws Exception {       
		return pageLoad(id, PageState.Read, model);
	}

	@SuppressWarnings("unchecked")
	public ModelAndView pageLoad(Long id, PageState state, ModelMap model) throws Exception {
		LayoutForm layoutForm = null;

		ActivityLogDetailDTO activityLongDetailDto = (ActivityLogDetailDTO) Facade.load(id, ActivityLogDetailDTO.class);				

		HashMap<String, AuditedPropertyDTO> revisionChanges = (HashMap<String, AuditedPropertyDTO>) Facade.getActivityLogRevisionChanges(activityLongDetailDto);
		if (revisionChanges != null && revisionChanges.size() > 0) {

			StringBuilder htmlPageComponentsForm = new StringBuilder();
			for (Map.Entry<String, AuditedPropertyDTO> revField : revisionChanges.entrySet()) {

				List<UIComponent> fields = getFields(revField).values().stream().collect(Collectors.toList());		

				layoutForm = UILayoutForm.draw(fields, ComponentMode.READ, ComponentAlignment.INLINE);

				htmlPageComponentsForm.append(layoutForm.getHtml());
				htmlPageComponentsForm.append("<br><br>");
			}

			var firstRevisionAuditProperty = revisionChanges.values().stream().findFirst().get();
			
			model.addAttribute("pagetitle", 
					String.format("<h3>Activity Audit Changes for %s #%s, applied by [%s] on %s</h3>"
					+ "<br><h5>Revision %s</h5>", 
					firstRevisionAuditProperty.getEntity(),
					firstRevisionAuditProperty.getEntityId().toString(),
					activityLongDetailDto.getActivityLog().getCreatedByuser().getUsername(),
					InstantToStringFormatter.print(activityLongDetailDto.getActivityLog().getCreatedDate()),
					firstRevisionAuditProperty.getNewRevision()));

			model.addAttribute("activitylogrevisionauditForm", htmlPageComponentsForm.toString());
		}
		else
		{
			model.addAttribute("pagetitle", "No revisions found to compare data changes");
		}

		return super.pageLoad("/pages/activitylog", model);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Map<String, UIComponent> getFields(Object o) throws Exception {
		Map<String, UIComponent> fields = new LinkedHashMap<String, UIComponent>();

		Map.Entry<String, AuditedPropertyDTO> revisionChange = (java.util.Map.Entry<String, AuditedPropertyDTO>) o;
		var value = revisionChange.getValue();
		UIComponent prevValue = null;
		UIComponent newValue = null;
		
		// Display fields using TextBox
		if (value.getFieldType() == AuditedPropertyFieldType.Single) {
			prevValue = UIInput.draw("Previous", value.getPropName() + ": " + value.getPrevValue(), UIInputType.Text);	
			newValue = UIInput.draw("New", value.getPropName() + ": " + value.getNewValue(), UIInputType.Text);		
		}
		// Display fields using Tables
		else if (value.getFieldType() == AuditedPropertyFieldType.Multiple)
		{

		    Table prevValueTable = Table.Builder.newInstance()
					.setTableDescription(value.getPropName())	
					.setName(value.getPropName())
					.setDatalist(value.getPrevValues())
					.setDatalistType(value.getDataListType())
					.setTableId(UUID.randomUUID().toString())
					.build();
		    prevValue = UIDataTable.draw(prevValueTable);
		    prevValue.setName("Previous: " + value.getPropName());
		    
		    Table newValueTable = Table.Builder.newInstance()
					.setName(value.getPropName())	
					.setDatalist(value.getNewValues())
					.setDatalistType(value.getDataListType())
					.setTableId(UUID.randomUUID().toString())
					.build();
		    newValue = UIDataTable.draw(newValueTable);
		    newValue.setName("New: " + value.getPropName());
		}

		fields.put(prevValue.getName() + "_PREV", prevValue);
		fields.put(newValue.getName() + "_NEW", newValue);

		return fields;
	}

	@Override
	protected ModelAndView getPageEdit(ModelMap model, Long id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ModelAndView postPageEdit(ModelMap model, Long id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ModelAndView getPageNew(ModelMap model) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ModelAndView postPageNew(ModelMap model) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ModelAndView delete(Form form, ModelMap model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ModelAndView saveUpdate(Form form, ModelMap model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ModelAndView saveNew(Form form, ModelMap model) {
		// TODO Auto-generated method stub
		return null;
	}

}
