package com.patroclos.uicomponent.core;

import com.patroclos.uicomponent.UIInputType;

public class Input extends UIComponent { 
	
	public Input() {
		isEditable = true; // set default to true
	}
	
	public UIInputType getInputType() {
		return type;
	}
	
}