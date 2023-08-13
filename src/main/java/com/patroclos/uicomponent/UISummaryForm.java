package com.patroclos.uicomponent;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.patroclos.uicomponent.UILayoutForm.ComponentAlignment;
import com.patroclos.uicomponent.core.Input;

@Component
public class UISummaryForm extends UILayoutForm {
	
	@Autowired
	protected UIInput UIInput;
	@Autowired
	protected UIButton UIButton;

	public String draw(List<Input> inputs) {				
		StringBuilder inputHtml = new StringBuilder();

		// Draw input fields
		inputHtml.append("<div>");
		for (Input input: inputs) {	
			String layoutComponentForm = drawLayoutFormField(input.getName(), true, ComponentAlignment.NONE);
			String layoutComponentFormHtml = layoutComponentForm.replace("?formComponent", input.getHtml());
			inputHtml.append("<div style='float:left; padding: 10px'>%s</div>".formatted(layoutComponentFormHtml));
		};
		inputHtml.append("</div>");
		
		// Draw Search Button
		String buttonSearchHtml = UIButton.draw("Search");
		String layoutButtonForm = drawLayoutFormField("", false, com.patroclos.uicomponent.UILayoutForm.ComponentAlignment.INLINE);
		String formButtonSearchHtml = layoutButtonForm.replace("?formComponent", buttonSearchHtml);				
		
		inputHtml.append("<div style='clear: both; float:left; padding: 10px'>%s</div>".formatted(formButtonSearchHtml));

		return wrapInCard(inputHtml.toString());
	}
}
