package com.patroclos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.patroclos.controller.core.BaseController;
import com.patroclos.dto.UserDTO;

@Controller
public class SignUpController extends BaseController {

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String pageLoad(ModelMap model) {
		return "signup";
	}

	@RequestMapping(value = "/signupconfirm", method = RequestMethod.POST)
	public String signUpConfirm(ModelMap model, 
			@RequestParam(required = true) String username, 
			@RequestParam(required = true) String password,
			@RequestParam(required = true) String passwordconfirm) throws Exception {

		UserDTO user = new UserDTO();
		user.setUsername(username);
		user.setPassword(password);
		user.setPasswordConfirm(passwordconfirm);	

		try
		{
			Facade.signUp(user);
		}catch (Exception e) {
			String error = e.getMessage();
			if (e.getCause() != null)
				error = e.getCause().getMessage();	
			model.addAttribute("error", error);
			return "signup";
		}

		return "redirect:login?signup=success";
	}
}
