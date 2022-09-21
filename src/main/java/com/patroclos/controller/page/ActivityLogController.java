package com.patroclos.controller.page;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.patroclos.controller.core.Form;
import com.patroclos.controller.core.PageController;
import com.patroclos.controller.core.PageState;
import com.patroclos.dto.ActivityLogDetailDTO;
import com.patroclos.dto.AuditedProperty;
import com.patroclos.model.ActivityLogDetail;
import com.patroclos.model.Article;
import com.patroclos.model.BaseO;
import com.patroclos.uicomponent.UIInputType;
import com.patroclos.uicomponent.UIInput.Input;
import com.patroclos.uicomponent.UILayoutForm.ComponentAlignment;
import com.patroclos.uicomponent.UILayoutForm.ComponentMode;
import com.patroclos.uicomponent.UILayoutForm.LayoutForm;

@Controller
public class ActivityLogController extends PageController{

	@RequestMapping(value="/activitylog/{id}",method=RequestMethod.POST)
	public String postPage(ModelMap model, @PathVariable("id") Long id) throws Exception {       	
		return pageLoad(id, PageState.Read, model);
	}

	@RequestMapping(value="/activitylog/{id}",method=RequestMethod.GET)    
	public String getPage(ModelMap model, @PathVariable("id") Long id) throws Exception {       
		return pageLoad(id, PageState.Read, model);
	}

	public String pageLoad(Long id, PageState state, ModelMap model) throws Exception {
		LayoutForm layoutForm = null;

		ActivityLogDetailDTO activityLongDetailDto = (ActivityLogDetailDTO) Facade.load(id, ActivityLogDetailDTO.class);				

		@SuppressWarnings("unchecked")
		HashMap<String, AuditedProperty> revisionChanges = (HashMap<String, AuditedProperty>) Facade.getActivityLogRevisionChanges(activityLongDetailDto);
		if (revisionChanges != null && revisionChanges.size() > 0) {

			StringBuilder htmlPageComponentsForm = new StringBuilder();
			for (Map.Entry<String, AuditedProperty> revField : revisionChanges.entrySet()) {

				List<Input> fields = getFields(revField).values().stream().collect(Collectors.toList());		

				layoutForm = UILayoutForm.draw(fields, ComponentMode.READ, ComponentAlignment.INLINE);
				
				htmlPageComponentsForm.append(layoutForm.getHtml());
				htmlPageComponentsForm.append("<br><br>");
			}

			var firstRevisionAuditProperty = revisionChanges.values().stream().findFirst().get();

			model.addAttribute("pagetitle", String.format("Activity Audit Changes for %s #%s, applied by user [%s] as of %s", 
					firstRevisionAuditProperty.getEntity(),
					firstRevisionAuditProperty.getEntityId().toString(),
					activityLongDetailDto.getActivityLog().getCreatedByuser().getUsername(),
					InstantToStringFormatter.print(activityLongDetailDto.getActivityLog().getCreatedDate())));

			model.addAttribute("activitylogrevisionauditForm", htmlPageComponentsForm.toString());
		}
		return "/pages/activitylog";
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Map<String,Input> getFields(Object o) {
		@SuppressWarnings("unchecked")

		//Map<String, AuditedProperty> revisionChanges = (Map<String, AuditedProperty>)o;
		Map<String,Input> fields = new LinkedHashMap<String,Input>();
		int counter = 1;

		Map.Entry<String, AuditedProperty> revisionChange = (java.util.Map.Entry<String, AuditedProperty>) o;
		//for(var entry : revisionChanges.entrySet()) {
			var value = revisionChange.getValue();//entry.getValue();
			counter++;
			Input prevValue = UIInput.draw("Previous Value", value.getPropName() + ": " + value.getPrevValue(), UIInputType.Text);	
			Input newValue = UIInput.draw("New Value", value.getPropName() + ": " + value.getNewValue(), UIInputType.Text);		
			fields.put(prevValue.getName() + counter, prevValue);
			fields.put(newValue.getName() + counter, newValue);
		//}

		return fields;
	}

	@Override
	protected String getPageEdit(ModelMap model, Long id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String postPageEdit(ModelMap model, Long id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getPageNew(ModelMap model) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String postPageNew(ModelMap model) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String delete(Form form, ModelMap model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String saveUpdate(Form form, ModelMap model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String saveNew(Form form, ModelMap model) {
		// TODO Auto-generated method stub
		return null;
	}

}
