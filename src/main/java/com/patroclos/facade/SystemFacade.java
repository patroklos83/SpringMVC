package com.patroclos.facade;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.patroclos.processmanager.IProcessManager;
import com.patroclos.dto.ActivityLogDTO;
import com.patroclos.dto.NotificationDTO;
import com.patroclos.process.*;

import ch.qos.logback.classic.Logger;

@Component("SystemFacade")
public class SystemFacade  {
	
	protected Logger logger = ((ch.qos.logback.classic.Logger)LoggerFactory.getLogger(this.getClass()));
	
	@Autowired
	protected IProcessManager processManager;
	@Autowired
	protected CRUDProcess CRUDProcess;
	@Autowired
	protected UserProcess UserProcess;
	@Autowired
	protected ActivityProcess ActivityProcess;
	
	
	/**
	 * Get latest activity log from database
	 * @return
	 * @throws Exception
	 */
	public List<ActivityLogDTO> loadLatestActivitiesFromDatabase() throws Exception {
		return ActivityProcess.getLatestActivities();		
	}

}
