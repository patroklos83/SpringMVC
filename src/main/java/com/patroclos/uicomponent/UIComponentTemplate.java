package com.patroclos.uicomponent;

import java.util.UUID;

public abstract class UIComponentTemplate {
	
	protected String uniqueId;
	
	public static final String DISABLE_PLACEHOLDER = "?disable";
	public static final String DISABLE_COMPONENT = "readonly";
	public static final String DISABLE_COMPONENT_COLOR = "#e9ecef";
	public static final String ENABLE_INPUT = " ";
	protected final String LABEL_CLASS = "col-sm-4 col-form-label";
	protected final String INPUT_CONTROL_CLASS = "form-control mb-2 mr-sm-2";
	
	public String getComponentId() {
		return UUID.randomUUID().toString();
	}

	protected String wrapInCard(String componentHtml) {
		
		String card = "<div class=\"card\">\r\n"
				+ "                  <div id=\"?id\" class=\"card-body\">\r\n"
				//+ "                    <h4 class=\"card-title\">Default form</h4>\r\n"
				//+ "                    <p class=\"card-description\">Basic form layout</p>"
				+ componentHtml
				+ "          </div>\r\n"
				+ "                </div>";
		return card;
	}
	
	protected String wrapInFormControlColumn(String inputElement) {
		StringBuilder formWrapper = new StringBuilder();
		formWrapper.append("<div>");
		formWrapper.append(inputElement);
		formWrapper.append("</div>");
		return formWrapper.toString();
	}
	
	protected boolean containsCardWrapper(String componentHtml) {
		if (componentHtml != null && componentHtml.contains("class=\"card\""))
			return true;
		else
			return false;
	}
}
