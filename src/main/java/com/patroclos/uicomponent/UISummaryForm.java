package com.patroclos.uicomponent;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.patroclos.exception.SystemException;
import com.patroclos.uicomponent.UIInput.Input;

@Component
public class UISummaryForm extends UILayoutForm {
	
	@Autowired
	protected UIInput UIInput;
	@Autowired
	protected UIButton UIButton;
	
	public String draw(String postBackUrl, String postBackActionLabel, List<Input> inputs) {	
		return draw(postBackUrl, postBackActionLabel, inputs, null);
	}

	public String draw(String postBackUrl, String postBackActionLabel, List<Input> inputs, String updateHtmlComponentId) {
		
		if (postBackActionLabel == null) {
			postBackActionLabel = "DefaultLabel";
		}
		
		if (postBackUrl == null) {
			throw new SystemException("postBackUrl not defined");
		}
		
		StringBuilder inputHtml = new StringBuilder();
		String formId = getComponentId();
		
		inputHtml.append("<form id='?formid' class=\"form-inline\" style='width:100vw' method='POST' action='"+ postBackUrl +"'>");
		
		
		for (Input input: inputs) {	
			String layoutComponentForm = drawLayoutFormField(input.getName());
			String layoutComponentFormHtml = layoutComponentForm.replace("?formComponent", input.getHtml());
			inputHtml.append(layoutComponentFormHtml);
		};
		
		String buttonSearchHtml = UIButton.draw(postBackActionLabel);
		String layoutButtonForm = drawLayoutFormField("", false, com.patroclos.uicomponent.UILayoutForm.ComponentAlignment.VERTICAL);
		String formButtonSearchHtml = layoutButtonForm.replace("?formComponent", buttonSearchHtml);
				
		inputHtml.append(formButtonSearchHtml
				+ "         </form>"
				+ "<script>"
		        + "$(document).ready(function(){\r\n"
		        + "    $(\"#?formid\").on(\"submit\", function(event){\r\n"
		        + "        event.preventDefault();\r\n"
		        + " \r\n"
		        + "        var formValues= $(this).serialize();\r\n"
		        + " executeProcess('"+ postBackUrl +"', formValues, false, '#?id'); "
		        + "    });\r\n"
		        + "});\r\n"
		        + "\r\n"
		        + "</script>");
		
		String formHtml = inputHtml.toString().replace("?id", updateHtmlComponentId != null ? updateHtmlComponentId : "dynamic_content" );
		formHtml = formHtml.replace("?formid", formId);      	
		
		
		return wrapInCard(formHtml);
	}


}
