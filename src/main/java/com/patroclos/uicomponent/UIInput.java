package com.patroclos.uicomponent;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.patroclos.uicomponent.core.Input;
import com.patroclos.utils.DateUtil;

@Component
public class UIInput extends UIComponentTemplate {

	public static final String DATETIME_FROM_NAME_SUFFIX = "From";
	public static final String DATETIME_TO_NAME_SUFFIX = "To";
	public static final String AUTOCOMPLETE_PLACEHOLDER = "?autocomplete";
	public static final String VALUE_PLACEHOLDER = "?value";

	public Input draw(String name, UIInputType inputType) {
		return draw(name, null, inputType);
	}

	public Input draw(String name, Object value, UIInputType inputType) {
		Input input = new Input();

		uniqueId = "input" + name + getComponentId();
		String inputHtml = "";

		switch (inputType) {
		case Text : inputHtml = getTextBox(name);
		break;
		case DropDown : inputHtml = getDropDownBox(name);
		break;
		case DateTime : inputHtml = getDateTimeBox(name);
		break;
		case TextArea : inputHtml = getTextAreaBox(name);
		break;
		default: break;
		}

		inputHtml = inputHtml.replace(AUTOCOMPLETE_PLACEHOLDER, "off");

		String fieldValue = "";
		if (value != null) {
			Object val = Optional.ofNullable(value).orElse("");

			if (value instanceof Instant) { //DateTime column type
				val = DateUtil.convertDbDateToUiDateFormat((Instant)value);
			}

			fieldValue = val.toString();
		}

		inputHtml = value != null ? inputHtml.replace(VALUE_PLACEHOLDER, fieldValue) : inputHtml.replace(VALUE_PLACEHOLDER, fieldValue);		
		input.setHtml(inputHtml);
		input.setId(uniqueId);
		input.setType(inputType);
		input.setName(name);

		return input;
	}

	private String getTextBox(String name) {
		String textBoxHtml = ""
				+ "          <input type=\"text\" name=\""+ name +"\" class='"+INPUT_CONTROL_CLASS+ " " + DISABLE_PLACEHOLDER + "' id=\""+ uniqueId +"\" placeholder=\""+ name 
				+"\"  autocomplete='?autocomplete' "+ DISABLE_PLACEHOLDER +" value='" + VALUE_PLACEHOLDER + "' ></input>";
		return wrapInFormControlColumn(textBoxHtml);
	}

	private String getTextAreaBox(String name) {
		String textBoxHtml = ""
				+ "          <textarea name=\""+ name +"\" rows='7' cols='50' class='"+INPUT_CONTROL_CLASS+ " " + DISABLE_PLACEHOLDER + "' id=\""+ uniqueId +"\" placeholder=\""+ name 
				+"\"  autocomplete='?autocomplete' "+ DISABLE_PLACEHOLDER +" >"+VALUE_PLACEHOLDER+"</textarea>";
		return wrapInFormControlColumn(textBoxHtml);
	}

	private String getDropDownBox(String name) {
		String dropdownBoxHtml = ""
				+ "                        <select name=\""+ name +"\"  class='"+INPUT_CONTROL_CLASS+ " " + DISABLE_PLACEHOLDER + "' id=\"exampleSelectGender\">"
				+ "                          <option>Male</option>"
				+ "                          <option>Female</option>"
				+ "                        </select>";
		return wrapInFormControlColumn(dropdownBoxHtml);
	}

	private String getDateTimeBox(String name) {

		String dateTimeFromName = name.replace(" ", "") +  DATETIME_FROM_NAME_SUFFIX;
		String dateTimeToName = name.replace(" ", "") +  DATETIME_TO_NAME_SUFFIX;
		String dateFormat = DateUtil.UI_DATE_FORMAT.toLowerCase();

		String dateBoxHtml = "<div class=\"input-group date\">"
				+ "    <div>"
				+ wrapInFormControlColumn("<input placeholder='From ?dateformat' name=\"" + dateTimeFromName 
						+ "\" id=\"" + dateTimeFromName + "\" type=\"text\" class='"+INPUT_CONTROL_CLASS+ " " + DISABLE_PLACEHOLDER + "' autocomplete='?autocomplete'>")
				+ "    </div>" 
				+ "    <div style='padding-left: 10px'>"
				+ wrapInFormControlColumn("<input placeholder='To ?dateformat' name=\"" + dateTimeToName 
						+ "\" id=\"" + dateTimeToName + "\" type=\"text\" class='"+INPUT_CONTROL_CLASS+ " " + DISABLE_PLACEHOLDER + "' autocomplete='?autocomplete' >")
				+ "    </div>" 
				+ "   </div>"
				+ "<script>"
				+ "\n $(document).ready(function(){"
				+ "\n   $('#"+ dateTimeFromName +"').datepicker({"
				+ "\n     format: '" + dateFormat + "'"
				+ "\n     ,clearBtn: true"
				+ "\n  });"
				+ "\n   $('#"+ dateTimeToName +"').datepicker({"
				+ "\n     format: '" + dateFormat + "'"
				+ "\n     ,clearBtn: true"
				+ "\n  });"
				+ "\n });"
				+ " </script>";
		
		dateBoxHtml = dateBoxHtml.replace("?dateformat", dateFormat);
		
		return dateBoxHtml;
	}


}
