
package com.patroclos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView loginPage(@RequestParam(value = "error",required = false) String error,
			@RequestParam(value = "logout",	required = false) String logout,
			@RequestParam(value = "signup",required = false) String signup) {

		ModelAndView model = new ModelAndView();
		if (error != null) {
			if (error.toLowerCase().equals("passexpired"))
				model.addObject("Credentials are expired. Please change password");
			else
				model.addObject("message", "Invalid Credentials provided.");
		}
		if (signup != null && signup.equalsIgnoreCase("success")) {
				model.addObject("message", "Signup success");
				model.addObject("signup", "success");
		}

		if (logout != null) {
			//model.addObject("message", "Logged out from JournalDEV successfully.");
		}

		model.setViewName("login");
		return model;
	}

}
