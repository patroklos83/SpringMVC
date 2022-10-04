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

	public String draw(List<Input> inputs) {				
		StringBuilder inputHtml = new StringBuilder();

		for (Input input: inputs) {	
			String layoutComponentForm = drawLayoutFormField(input.getName());
			String layoutComponentFormHtml = layoutComponentForm.replace("?formComponent", input.getHtml());
			inputHtml.append(layoutComponentFormHtml);
		};
		
		String buttonSearchHtml = UIButton.draw("Search");
		String layoutButtonForm = drawLayoutFormField("", false, com.patroclos.uicomponent.UILayoutForm.ComponentAlignment.VERTICAL);
		String formButtonSearchHtml = layoutButtonForm.replace("?formComponent", buttonSearchHtml);				
		inputHtml.append(formButtonSearchHtml);

		return wrapInCard(inputHtml.toString());
	}


}
