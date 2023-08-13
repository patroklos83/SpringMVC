package com.patroclos.uicomponent;

import org.springframework.stereotype.Component;

import com.patroclos.uicomponent.core.Input;

@Component
public class UICheckbox extends UIInput {
	
	public static final String DISABLE_COMPONENT = "disabled";
	
	public Input draw(String name, boolean isChecked) {		
		Input input = new Input();

		uniqueId = "input" + name + getComponentId();	
		String html = getCheckBox(name).replace("?isChecked", isChecked ? "checked value='1'" : "value='0'");
		
		input.setHtml(html);
		input.setId(uniqueId);
		input.setType(UIInputType.CheckBox);
		input.setName(name);

		return input;
	}
	
	private String getCheckBox(String name) {
		String checkBoxHtml = "<div class='form-check'>"
				+ "                   <input type='checkbox' id='"+ name +"' name='"+ name +"'"
				+ "" + DISABLE_PLACEHOLDER + " ?isChecked>" 
				+ "             </div>";
		return checkBoxHtml;
	}

}
