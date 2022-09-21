package com.patroclos.uicomponent;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.patroclos.uicomponent.core.UIComponent;
import com.patroclos.utils.DateUtil;

@Component
public class UIInput extends UIComponentTemplate {

	private String uniqueId;
	private final String LABEL_CLASS = "col-sm-4 col-form-label";
	private final String INPUT_CONTROL_CLASS = "form-control mb-2 mr-sm-2";

	public static final String DATETIME_FROM_NAME_SUFFIX = "From";
	public static final String DATETIME_TO_NAME_SUFFIX = "To";
	public static final String AUTOCOMPLETE_PLACEHOLDER = "?autocomplete";
	public static final String DISABLE_PLACEHOLDER = "?disable";
	public static final String DISABLE_INPUT = "readonly";
	public static final String ENABLE_INPUT = " ";
	public static final String VALUE_PLACEHOLDER = "?value";

	public class Input extends UIComponent { 
		
	}

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
		case CheckBox : inputHtml = getCheckBox(name);
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
				+ "          <input type=\"text\" name=\""+ name +"\" class='"+INPUT_CONTROL_CLASS+"' id=\""+ uniqueId +"\" placeholder=\""+ name 
				+"\"  autocomplete='?autocomplete' "+ DISABLE_PLACEHOLDER +" value='" + VALUE_PLACEHOLDER + "' ></input>";
		return wrapInFormControlColumn(textBoxHtml);
	}

	private String getTextAreaBox(String name) {
		String textBoxHtml = ""
				+ "          <textarea name=\""+ name +"\" rows='7' cols='50' class='"+INPUT_CONTROL_CLASS+"' id=\""+ uniqueId +"\" placeholder=\""+ name 
				+"\"  autocomplete='?autocomplete' "+ DISABLE_PLACEHOLDER +" >"+VALUE_PLACEHOLDER+"</textarea>";
		return wrapInFormControlColumn(textBoxHtml);
	}

	private String getCheckBox(String name) {
		String checkBoxHtml = "<div class=\"form-check\">"
				+ "                              <label class='"+ LABEL_CLASS  +"'>"
				+ "                                <input type=\"checkbox\" name=\""+ name +"\" class='"+INPUT_CONTROL_CLASS+"'>"+ name +"<i class=\"input-helper\"></i></label>"
				+ "                            </div>";
		return wrapInFormControlColumn(checkBoxHtml);
	}

	private String getDropDownBox(String name) {
		String dropdownBoxHtml = ""
				+ "                        <select name=\""+ name +"\"  class='"+INPUT_CONTROL_CLASS+"' id=\"exampleSelectGender\">"
				+ "                          <option>Male</option>"
				+ "                          <option>Female</option>"
				+ "                        </select>";
		return wrapInFormControlColumn(dropdownBoxHtml);
	}

	private String getDateTimeBox(String name) {

		String dateTimeFromName = name +  DATETIME_FROM_NAME_SUFFIX;
		String dateTimeToName = name +  DATETIME_TO_NAME_SUFFIX;
		String dateFormat = DateUtil.UI_DATE_FORMAT.toLowerCase();

		String dateBoxHtml = "<div class=\"input-group date\">"
				+ "    <div>"
				+ wrapInFormControlColumn("<input placeholder='From ?dateformat' name=\"" + dateTimeFromName 
						+ "\" id=\"" + dateTimeFromName + "\" type=\"text\" class='"+INPUT_CONTROL_CLASS+"' autocomplete='?autocomplete'>")
				+ "    </div>" 
				+ "    <div>"
				+ wrapInFormControlColumn("<input placeholder='To ?dateformat' name=\"" + dateTimeToName 
						+ "\" id=\"" + dateTimeToName + "\" type=\"text\" class='"+INPUT_CONTROL_CLASS+"' autocomplete='?autocomplete' >")
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

	private String wrapInFormControlColumn(String inputElement) {
		StringBuilder formWrapper = new StringBuilder();
		formWrapper.append("<div>");
		formWrapper.append(inputElement);
		formWrapper.append("</div>");
		return formWrapper.toString();
	}


}
