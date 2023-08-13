package com.patroclos.uicomponent;

import org.springframework.stereotype.Component;

import com.patroclos.uicomponent.core.ButtonType;

@Component
public class UIButton extends UIComponentTemplate {
	
		private String uniqueId;
		
		public String draw(String title) {		
			String buttonHtml = null;
			
			uniqueId = "button" + title + getComponentId();
		    buttonHtml = draw(title, ButtonType.Submit, null);
			
			return buttonHtml;
		}

		public String draw(String title, ButtonType buttonType, String javascriptFunction) {		
			String buttonHtml = null;
			
			uniqueId = "button" + title + getComponentId();
			
			if (buttonType == ButtonType.Submit) {
				buttonHtml = getButtonHtml(title);
				buttonHtml = buttonHtml.replace("?type", "submit").replace("?onclick", "");
			}
			else {
				buttonHtml = getButtonHtml(title);
				buttonHtml = buttonHtml.replace("?type", "button").replace("?onclick", "onclick='" + javascriptFunction + "'");
			}
			
			return buttonHtml;
		}

		private String getButtonHtml(String title) {
			String textBoxHtml =  "<button type=\"?type\" class=\"btn btn-primary mr-2\" ?onclick>" + title +"</button>";
			return textBoxHtml;
		}

}
