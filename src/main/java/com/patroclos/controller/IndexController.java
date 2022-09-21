package com.patroclos.controller;

import java.util.Base64;
import java.util.HashMap;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patroclos.controller.core.BaseController;


@Controller
public class IndexController extends BaseController {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getPageDefault(ModelMap model, @RequestParam(required = false) String page) throws Exception {
		if (page != null) {
			setMasterPageInnerForm(model, page);
		}
		else
		{
			model.addAttribute("dynamicScript", "");
		}		
		
		var loggedUser = AuthenticationFacade.getLoggedUser();
		if (loggedUser == null || loggedUser.getId() == 1) //anonymous User
			return "login";
		
		return "main/index";
	}
	
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String getPage(ModelMap model, @RequestParam(required = false) String page) {
		if (page != null) {
			setMasterPageInnerForm(model, page);
		}
		else
		{
			model.addAttribute("dynamicScript", "");
		}		
		return "main/index";
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)	
	public String postPage(ModelMap model, @RequestParam("vars") String vars) throws Exception {
		if (vars != null) {
			byte[] paramsBytes = Base64.getDecoder().decode(vars.getBytes());
			String params = new String(paramsBytes);
			ObjectMapper objectMapper = new ObjectMapper();
			var modelAttributes = objectMapper.readValue(params, HashMap.class);
			model.addAllAttributes(modelAttributes);
		}		
		return "main/index";
	}
}