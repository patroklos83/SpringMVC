package com.patroclos.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.patroclos.dto.BaseDTO;
import com.patroclos.process.*;
import com.patroclos.processmanager.IProcess;
import com.patroclos.processmanager.IProcessManager;

@Component
public class Facade  {

	@Autowired
	private IProcessManager processManager;
	@Autowired
	private CRUDProcess CRUDProcess;
	@Autowired
	private UserProcess UserProcess;
	@Autowired
	private ActivityProcess ActivityProcess;	
	
	/***
	 * Validates if user has permission to search
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public Object validateSearchAccess(BaseDTO input) throws Exception {
		IProcess<?, ?, ?> process = b -> CRUDProcess.search(input);
		return processManager.runProcess(process, input, CRUDProcess.PROCESS_NAME_SEARCH);
	}
	
	public <T> Object load(long id, Class<? extends BaseDTO> inputType) throws Exception{
		IProcess<?, ?, ?> process = b -> CRUDProcess.load(id, inputType);
		return processManager.runProcess(process, inputType, id, CRUDProcess.PROCESS_NAME_READ);
	}
	
	public Object delete(BaseDTO input) throws Exception{
		IProcess<?, ?, ?> process = b -> CRUDProcess.delete(input);
		return processManager.runProcess(process, input, CRUDProcess.PROCESS_NAME_DELETE);
	}
	
	public Object saveNew(BaseDTO input) throws Exception{
		IProcess<?, ?, ?> process = b -> CRUDProcess.saveNew(input);
		return processManager.runProcess(process, input, CRUDProcess.PROCESS_NAME_CREATE);
	}
	
	public Object saveUpdate(BaseDTO input) throws Exception{
		IProcess<?, ?, ?> process = b -> CRUDProcess.saveUpdate(input);
		return processManager.runProcess(process, input, CRUDProcess.PROCESS_NAME_UPDATE);
	}
	
	public Object cancel(BaseDTO input) throws Exception{
		IProcess<?, ?, ?> process = b -> CRUDProcess.cancel(input);
		return processManager.runProcess(process, input, CRUDProcess.PROCESS_NAME_CANCEL);
	}
	
	public Object getActivityLogRevisionChanges(BaseDTO input) throws Exception{
		IProcess<?, ?, ?> process = b -> ActivityProcess.getActivityLogRevisionChanges(input);
		return processManager.runProcess(process, input, ActivityProcess.PROCESS_NAME_ACTIVITYLOG_REVISION_CHANGES);
	}
	
	public Object signUp(BaseDTO input) throws Exception {
		IProcess<?, ?, ?> process = b -> UserProcess.signUp(input);
		return processManager.runProcess(process, input, UserProcess.PROCESS_SIGNUP);
	}
}
