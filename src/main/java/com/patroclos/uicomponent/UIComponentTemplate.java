package com.patroclos.uicomponent;

import java.util.UUID;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

public abstract class UIComponentTemplate {

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
	
	protected boolean containsCardWrapper(String componentHtml) {
		if (componentHtml != null && componentHtml.contains("class=\"card\""))
			return true;
		else
			return false;
	}
}
