package com.patroclos.controller.core;

import java.beans.PropertyEditorSupport;
import com.patroclos.utils.DateUtil;

public class StringToInstantPropertyEditor extends PropertyEditorSupport {
	
    private String format;

    public void setFormat(String format) {
        this.format = format;
    }
    
    public void setAsText(String text) {
        setValue(DateUtil.convertUiDateTimeStringToInstant(text));
    }
}