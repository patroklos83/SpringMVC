package com.patroclos.controller;

import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patroclos.controller.core.BaseController;

@Slf4j
@Controller
public class IndexController extends BaseController {
	
	public static String themeFolder = "main";
	
	/***
	 * Define the / root path as per the below annotation values, produces = MediaType.TEXT_HTML_VALUE.
	 * Avoid conflicts with the swagger springdoc / root path index
	 * @param model
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView getPageDefault(ModelMap model, @RequestParam(name="page", required = false) String page) throws Exception {
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
		
		setCommonAttributes(model);
		log.info("entering: " + themeFolder + "/index");
		return super.pageLoad(themeFolder + "/index", model);
	}
	
	@GetMapping("/index")
	public ModelAndView getPage(ModelMap model, @RequestParam(name="page", required = false) String page) throws Exception {
		if (page != null) {
			setMasterPageInnerForm(model, page);
		}
		else
		{
			model.addAttribute("dynamicScript", "");
		}		
		
		setCommonAttributes(model);
		log.info("entering: " + themeFolder + "/index");
		return super.pageLoad(themeFolder + "/index", model);
	}

	@SuppressWarnings("unchecked")
	@PostMapping("/")	
	public ModelAndView postPage(ModelMap model, @RequestParam(name="vars") String vars) throws Exception {
		if (vars != null) {
			byte[] paramsBytes = Base64.getDecoder().decode(vars.getBytes());
			String params = new String(paramsBytes);
			ObjectMapper objectMapper = new ObjectMapper();
			var modelAttributes = objectMapper.readValue(params, HashMap.class);
			model.addAllAttributes(modelAttributes);
		}		
		
		setCommonAttributes(model);
		return super.pageLoad(themeFolder + "/index", model);
	}
	
	private void setCommonAttributes(ModelMap model) throws Exception {
		boolean isAdmin = AuthenticationFacade.isLoggedUserAdmin();		
		model.addAttribute("permissionUserManagement", isAdmin);
		model.addAttribute("permissionActivityLog", isAdmin);	
		model.addAttribute("permissionScheduler", isAdmin);
		model.addAttribute("contextPath", WebUtils.getContextPath());
	}
	
	@PostMapping(value = "/tablePaging", produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String getTablePaging(@RequestParam Map<String,String> pagingParams, ModelMap model) throws Exception {
		return super.getTablePaging(pagingParams, model);
	}
	
	@PostMapping(value = "/tableDataFunc", produces = MediaType.TEXT_HTML_VALUE)
	@ResponseBody
	public String executeTableDataFunc(@RequestParam Map<String,String> params) throws Exception {
		return super.executeTableDataFunc(params);
	}
}