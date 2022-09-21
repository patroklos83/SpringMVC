package com.patroclos.controller.core;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patroclos.configuration.converterformatter.*;
import com.patroclos.facade.*;
import com.patroclos.uicomponent.*;
import com.patroclos.utils.Base64Util;
import com.patroclos.utils.CustomModelMapper;
import com.patroclos.utils.WebUtils;

public class BaseController {
	
	@Autowired
	protected Facade Facade;
	@Autowired
	protected AuthenticationFacade AuthenticationFacade;
	@Autowired
	protected CustomModelMapper CustomModelMapper;
	@Autowired
    protected DataHolder DataHolder;
	
	@Autowired
	protected UITable UITable;
	@Autowired
	protected UIInput UIInput;
	@Autowired
	protected UISummaryForm UIForm;
	@Autowired
	protected UILayoutForm UILayoutForm;
	
	@Autowired
	protected InstantToStringFormatter InstantToStringFormatter;
	
	@Autowired
	protected WebUtils WebUtils;
	
	protected void setMasterPageInnerForm(ModelMap model, String formName) {		
		model.addAttribute("innerContentform", Base64Util.encode(formName));
	}
}
