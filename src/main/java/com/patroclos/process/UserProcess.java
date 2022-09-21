package com.patroclos.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.patroclos.dto.BaseDTO;
import com.patroclos.service.UserService;

@Component
public class UserProcess extends BaseProcess {

	@Autowired
	private UserService UserService;

	public final String PROCESS_SIGNUP = "SIGNUP";

	public Object signUp(BaseDTO input) throws Exception {		
		UserService.signUp(input);
		return input;
	}
}
