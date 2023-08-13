package com.patroclos.uicomponent;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.patroclos.uicomponent.core.Input;
import com.patroclos.uicomponent.core.Table;
import com.patroclos.uicomponent.core.UIComponent;
import com.patroclos.controller.core.*;

public class UILayoutForm extends UIComponentTemplate {

	@Autowired
	protected UIInput UIInput;

	@Autowired
	protected UIButton UIButton;

	@Autowired
	protected UIDataTable UIDataTable;

	@Autowired
	protected DataHolder DataHolder;

	public class LayoutForm extends UIComponent {		
	}

	public enum ComponentAlignment {
		INLINE,
		VERTICAL,
		NONE
	}

	public enum ComponentMode {
		READ,
		EDIT,
		EDIT_IN_MODAL
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
		else if (componentAlignment == ComponentAlignment.NONE) {
			formHtml.append("<div>\r\n");
			if (includeLabel)
				formHtml.append(" <label for=\"name\" class=\"control-label\">" + formTitle + "</label>");

			formHtml.append("?formComponent");
			formHtml.append(" </div>");
		}


		return formHtml.toString();
	}

	public LayoutForm draw(List<UIComponent> fields, ComponentMode componentMode, ComponentAlignment componentAlignment) throws Exception {
		LayoutForm layoutForm = new LayoutForm();
		StringBuilder layoutFormHtml = new StringBuilder();
		String layoutFormId = getComponentId();


		if (componentAlignment == ComponentAlignment.INLINE)
			layoutFormHtml.append("<div class=\"row\">\r\n");
		else if (componentAlignment == ComponentAlignment.VERTICAL)
			layoutFormHtml.append("\n<div>");			

		// Add input fields
		var inputFields = fields.stream().filter(f -> f instanceof Input)
				.map(f -> (Input)f)
				.collect(Collectors.toList());

		if (inputFields != null)
		{
			for (Input field: inputFields) {

				String formFieldHtml = drawLayoutFormField(field.getName(), true, componentAlignment);

				String fieldHtml = "";
				if (componentMode == ComponentMode.READ) {
					if (field.getType() == UIInputType.CheckBox)
						fieldHtml = field.getHtml().replace(UIComponentTemplate.DISABLE_PLACEHOLDER, UICheckbox.DISABLE_COMPONENT);
					else
						fieldHtml = field.getHtml().replace(UIComponentTemplate.DISABLE_PLACEHOLDER, UIComponentTemplate.DISABLE_COMPONENT);
				}
				else {
					if (field.IsEditable())
						fieldHtml = field.getHtml().replace(UIComponentTemplate.DISABLE_PLACEHOLDER, UIComponentTemplate.ENABLE_INPUT);
				}

				formFieldHtml = formFieldHtml.replace("?formComponent", fieldHtml);
				layoutFormHtml.append(formFieldHtml);
			};
		}

		// Add tables
		var tableFields = fields.stream().filter(f -> f instanceof Table)
				.map(f -> (Table)f)
				.collect(Collectors.toList());

		if (tableFields != null)
		{
			for (Table table: tableFields) {

				// class d-flex disables checkbox behavior for some reason, so skip the wrapping
				String formFieldHtml = drawLayoutFormField(
						table.getTableDescription() != null ? table.getTableDescription() : table.getName() 
						, true, ComponentAlignment.INLINE);

				table.setComponentMode(componentMode);
				table = UIDataTable.draw(table);
				DataHolder.addDataToMap(table.getId(), table);

				formFieldHtml = formFieldHtml.replace("?formComponent", table.getHtml());

				layoutFormHtml.append("\n\n<br><br><br>" + formFieldHtml);
			};
		}

		layoutFormHtml.append("\n</div>");
		String hash = UUID.randomUUID().toString();
		String html = layoutFormHtml.toString().replace("?id", layoutFormId).replace("?hash", hash);
		layoutForm.setHtml(html);
		layoutForm.setId(layoutFormId);

		return layoutForm;
	}

}
