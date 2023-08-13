package com.patroclos.controller;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patroclos.controller.core.BaseController;

@Controller
public class IndexController extends BaseController {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView getPageDefault(ModelMap model, @RequestParam(required = false) String page) throws Exception {
		if (page != null) {
			setMasterPageInnerForm(model, page);
		}
		else
		{
			model.addAttribute("dynamicScript", "");
		}		
		
		var loggedUser = AuthenticationFacade.getLoggedUser();
		if (loggedUser == null || loggedUser.getId() == 1) //anonymous User
			return super.pageLoad("login", model);
		
		return super.pageLoad("main/index", model);
	}
	
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public ModelAndView getPage(ModelMap model, @RequestParam(required = false) String page) throws Exception {
		if (page != null) {
			setMasterPageInnerForm(model, page);
		}
		else
		{
			model.addAttribute("dynamicScript", "");
		}		
		
		setCommonAttributes(model);
		return super.pageLoad("main/index", model);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/", method = RequestMethod.POST)	
	public ModelAndView postPage(ModelMap model, @RequestParam("vars") String vars) throws Exception {
		if (vars != null) {
			byte[] paramsBytes = Base64.getDecoder().decode(vars.getBytes());
			String params = new String(paramsBytes);
			ObjectMapper objectMapper = new ObjectMapper();
			var modelAttributes = objectMapper.readValue(params, HashMap.class);
			model.addAllAttributes(modelAttributes);
		}		
		
		setCommonAttributes(model);
		return super.pageLoad( "main/index", model);
	}
	
	private void setCommonAttributes(ModelMap model) throws Exception {
		boolean isAdmin = AuthenticationFacade.isLoggedUserAdmin();		
		model.addAttribute("permissionUserManagement", isAdmin);
		model.addAttribute("permissionActivityLog", isAdmin);		
		model.addAttribute("contextPath", WebUtils.getContextPath());
	}
	
	@RequestMapping(value = "/tablePaging", produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String getTablePaging(@RequestParam Map<String,String> pagingParams, ModelMap model) throws Exception {
		return super.getTablePaging(pagingParams, model);
	}
	
	@RequestMapping(value = "/tableDataFunc", produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String executeTableDataFunc(@RequestParam Map<String,String> params) throws Exception {
		return super.executeTableDataFunc(params);
	}
}