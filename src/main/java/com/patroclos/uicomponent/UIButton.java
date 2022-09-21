package com.patroclos.uicomponent;

import org.springframework.stereotype.Component;

@Component
public class UIButton extends UIComponentTemplate {
	
		private String uniqueId;

		public String draw(String title) {
			uniqueId = "button" + title + getComponentId();
			String inputHtml = getButtonHtml(title);
			return inputHtml;
		}

		private String getButtonHtml(String title) {
			String textBoxHtml =  "<button type=\"submit\" class=\"btn btn-primary mr-2\">" + title +"</button>";
			return textBoxHtml;
		}

}
