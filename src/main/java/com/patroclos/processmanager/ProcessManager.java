package com.patroclos.processmanager;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.patroclos.dto.*;
import com.patroclos.exception.SystemException;
import com.patroclos.process.*;
import com.patroclos.service.*;
import com.patroclos.utils.ProcessUtil;

@Component
public class ProcessManager implements IProcessManager {

	@Autowired
    private ActivityProcess ActivityProcess;
	
	@Autowired
    private IAuthenticationService AuthenticationService;
	
	@Autowired
    private UserService UserService;

	public Object runProcess(IProcess<?, ?, ?> process, BaseDTO input, String processName) throws Exception
	{
		return runProcess(process, input.getClass(), input, null, processName);	
	}

	public Object runProcess(IProcess<?, ?, ?> process, Class<? extends BaseDTO> inputType, Long id, String processName) throws Exception
	{
		return runProcess(process, inputType, null, id, processName);	
	}

	public Object runProcess(IProcess<?, ?, ?> process, Class<? extends BaseDTO> inputType, BaseDTO input, Long id, String processName) throws Exception
	{	
		Object result = null;		

		if (processName == null )
		{
			throw new SystemException("ProcessName not defined");
		}

		var loggedUser = AuthenticationService.getLoggedDbUserDTO();
		if (loggedUser.getIsDeleted() == 1) {
			throw new SystemException("Process authorization failed. User is deleted");
		}
		if (loggedUser.getEnabled() != 1) {
			throw new SystemException("Process authorization failed. User is not enabled");
		}
		if(loggedUser.getRoles() == null) {
			throw new SystemException("Process authorization failed. User has no defined roles");
		}
//		var userRoles = loggedUser.getRoles().stream().filter(r -> r.getName().equals("ROLE_USER")).collect(Collectors.toList());
//		if (userRoles.size() == 0) {
//			throw new SystemException("Process authorization failed. User has no access to the processes. No ROLE_USER found");
//		}		
		var authorities = UserService.getUserAuthorities(loggedUser);
		if (authorities == null || authorities.size() == 0) {
			throw new SystemException("Process authorization failed. No access permissions found");
		}		
		var authoritiesList = authorities.stream().map(a -> a.getAuthority()).collect(Collectors.toList());
		if (!authoritiesList.contains(processName)) {
			throw new SystemException("Process authorization failed. User has no access to this process. No access permissions found");
		}		
		
		boolean isProcessSuccess = false;
		String processError = "";
		String processId = ProcessUtil.getActivityProcessId();
		try
		{
			Thread.currentThread().setName(processId);
			result = process.run(input);
			isProcessSuccess = true;
		}
		catch (Exception e)
		{
			processError = e.getMessage();
			if (e.getCause() != null) processError = e.getCause().getMessage();
			throw new SystemException(e);
		}
		finally {
			ActivityLogDTO activityLogDto = new ActivityLogDTO();
			activityLogDto.setProcess(processName);
			activityLogDto.setProcessId(processId);
			activityLogDto.setInputType(inputType);
			activityLogDto.setInput(input);
			activityLogDto.setId(id);
			activityLogDto.setResult(isProcessSuccess ? 
					BaseProcess.PROCESS_RUN_RESULT_SUCCESS 
					: BaseProcess.PROCESS_RUN_RESULT_FAIL);
			activityLogDto.setError(processError);
			ActivityProcess.logActivity(activityLogDto);
			//reset thread's name after process and activity log insert is complete
			Thread.currentThread().setName("");
		}

		return  result;
	}	
}
