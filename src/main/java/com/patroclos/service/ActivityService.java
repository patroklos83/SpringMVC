package com.patroclos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.patroclos.businessobject.*;
import com.patroclos.dto.ActivityLogDTO;

@Component
public class ActivityService extends BaseService {
	
	@Autowired
	private FailedLoginAttemptBO FailedLoginAttemptBO;
	
	@Autowired
	private ActivityLogBO ActivityLogBO;
	
	public int countLoginFailedAttempsByUserName(String userName) throws Exception {
		return FailedLoginAttemptBO.countFailedLoginsByUsername(userName);		
	}
	
	public void logFailedLoginAttempt(String userName) throws Exception {
	    FailedLoginAttemptBO.save(userName);		
	}
	
	public List<ActivityLogDTO> getLatestActivities() throws Exception {
		return ActivityLogBO.loadLatestActivities().stream()
				.map(a -> {
					try {
						return (ActivityLogDTO)CustomModelMapper.mapModeltoDTO(a, ActivityLogDTO.class);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				})
				.toList();		
	}

}
