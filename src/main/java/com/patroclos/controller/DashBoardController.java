package com.patroclos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.patroclos.controller.core.BaseController;
import com.patroclos.dto.UserDTO;

@Controller
public class DashBoardController extends BaseController {
	
	@GetMapping("/dashboard")
	public String pageLoadGet(ModelMap model) throws Exception {
		setWelcomeMessage(model);
		return "main/dashboard";
	}
	
	@PostMapping("/dashboard")
	public String pageLoadPost(ModelMap model) throws Exception {
		setWelcomeMessage(model);
		return "main/dashboard";
	}
	
	private void setWelcomeMessage(ModelMap model) throws Exception {
		UserDTO user = AuthenticationFacade.getLoggedUser();
		model.addAttribute("WelcomeMessage", String.format("Welcome %s!", user.getUsername()));
	}

}
