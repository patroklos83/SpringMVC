package com.patroclos.uicomponent;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.patroclos.uicomponent.UIInput.Input;
import com.patroclos.uicomponent.core.UIComponent;

public class UILayoutForm extends UIComponentTemplate {

	@Autowired
	protected UIInput UIInput;
	@Autowired
	protected UIButton UIButton;

	public class LayoutForm extends UIComponent {		
	}

	public enum ComponentAlignment {
		INLINE,
		VERTICAL
	}

	public enum ComponentMode {
		READ,
		EDIT
	}

	public String drawLayoutFormField(String formTitle) {
		return drawLayoutFormField(formTitle, true, ComponentAlignment.VERTICAL);
	}

	public String drawLayoutFormField(String formTitle, boolean includeLabel, ComponentAlignment componentAlignment) {
		StringBuilder formHtml = new StringBuilder();

		if (componentAlignment == ComponentAlignment.VERTICAL) {
			formHtml.append("<div class=\"form-group\">\r\n" + 
					"          <div class=\"d-flex flex-column align-items-start\">\r\n");
			if (includeLabel)
				formHtml.append(" <label for=\"name\" class=\"control-label\">" + formTitle + "</label>");

			formHtml.append("?formComponent");
			formHtml.append(" </div></div>");
		} 
		else if (componentAlignment == ComponentAlignment.INLINE) {
			formHtml.append("<div class=\"col\">\r\n");
			if (includeLabel)
				formHtml.append(" <label for=\"name\" class=\"control-label\">" + formTitle + "</label>");

			formHtml.append("?formComponent");
			formHtml.append(" </div>");
		}


		return formHtml.toString();
	}

	public LayoutForm draw(List<Input> fields, ComponentMode componentMode, ComponentAlignment componentAlignment) {
		LayoutForm layoutForm = new LayoutForm();
		StringBuilder layoutFormHtml = new StringBuilder();
		String layoutFormId = getComponentId();

		
		if (componentAlignment == ComponentAlignment.INLINE)
			layoutFormHtml.append("<div class=\"row\">\r\n");
		else if (componentAlignment == ComponentAlignment.VERTICAL)
			layoutFormHtml.append("\n<div>");			
		
		for (Input field: fields) {
			
			String formFieldHtml = drawLayoutFormField(field.getName(), true, componentAlignment );
			
			String fieldHtml = field.getHtml().replace(
					com.patroclos.uicomponent.UIInput.DISABLE_PLACEHOLDER, 
					componentMode == ComponentMode.READ ? com.patroclos.uicomponent.UIInput.DISABLE_INPUT 
							: com.patroclos.uicomponent.UIInput.ENABLE_INPUT);
			formFieldHtml = formFieldHtml.replace("?formComponent", fieldHtml);
			layoutFormHtml.append(formFieldHtml);
		};

		layoutFormHtml.append("\n</div>");
		String hash = UUID.randomUUID().toString();
		String html = layoutFormHtml.toString().replace("?id", layoutFormId).
				replace("?hash", hash);
		layoutForm.setHtml(html);
		layoutForm.setId(layoutFormId);

		return layoutForm;
	}

}
