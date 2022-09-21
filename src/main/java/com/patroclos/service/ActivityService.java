package com.patroclos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.patroclos.businessobject.*;

@Component
public class ActivityService extends BaseService {
	
	@Autowired
	private FailedLoginAttemptBO FailedLoginAttemptBO;
	
	public int countLoginFailedAttempsByUserName(String userName) throws Exception {
		return FailedLoginAttemptBO.countFailedLoginsByUsername(userName);		
	}
	
	public void logFailedLoginAttempt(String userName) throws Exception {
	    FailedLoginAttemptBO.save(userName);		
	}

}
