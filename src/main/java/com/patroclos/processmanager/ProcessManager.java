package com.patroclos.processmanager;

import java.util.List;
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
	@Autowired
	private CRUDProcess CRUDProcess;

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
		boolean isSummaryDTO = input instanceof SummaryDTO;

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
		if (authorities == null) {
			throw new SystemException("Process authorization failed. No access permissions found");
		}		
		var authoritiesList = authorities.stream().map(a -> a.getAuthority()).collect(Collectors.toList());
		if (!authoritiesList.contains(processName)) {
			throw new SystemException("Process authorization failed. User has no access to this process. No access permissions found");
		}	

		// Validate if user has access to the specific entity CRUD operations
		if (CRUDProcess.isCRUDProcess(processName)) {
			String entity = inputType.getSimpleName().replace("DTO", "").toUpperCase();
			boolean foundEntityAccessPermissions = false;
			boolean hasAccess = false;
			for (RoleDTO role : loggedUser.getRoles()) {
				List<RoleEntityAccessDTO> entityAccess = role.getRoleEntityAccess();
				if (entityAccess != null && entityAccess.size() > 0) {
					foundEntityAccessPermissions = true;
					switch (processName.toUpperCase()) {
					case com.patroclos.process.CRUDProcess.PROCESS_NAME_READ -> hasAccess = entityAccess.stream().anyMatch(e -> e.getEntityAccess().getName().equalsIgnoreCase(entity) && e.isReadAccess() == true);
					case com.patroclos.process.CRUDProcess.PROCESS_NAME_CREATE -> hasAccess = entityAccess.stream().anyMatch(e -> e.getEntityAccess().getName().equalsIgnoreCase(entity) && e.isCreateAccess() == true);
					case com.patroclos.process.CRUDProcess.PROCESS_NAME_DELETE -> hasAccess = entityAccess.stream().anyMatch(e -> e.getEntityAccess().getName().equalsIgnoreCase(entity) && e.isDeleteAccess() == true);
					case com.patroclos.process.CRUDProcess.PROCESS_NAME_UPDATE -> hasAccess = entityAccess.stream().anyMatch(e -> e.getEntityAccess().getName().equalsIgnoreCase(entity) && e.isUpdateAccess() == true);		
					}
				}
			}

			if (foundEntityAccessPermissions == false)
				throw new SystemException("No Entity Access Permissions defined for this user's Role(s) [entity: %s]".formatted(entity));

			if (hasAccess == false)
				throw new SystemException("User's Role(s) has no Entity Access Permission to [%s] entity [%s]".formatted(processName, entity));
		}

		boolean isProcessSuccess = false;
		String processError = "";
		String processId = ProcessUtil.getActivityProcessId();
		try
		{
			Thread.currentThread().setName(processId);
			result = !isSummaryDTO ? process.run(input) : null;
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
			if (isSummaryDTO && ((SummaryDTO) input).getReferencesEntityDTO() != null) 
				activityLogDto.setInputType(((SummaryDTO) input).getReferencesEntityDTO());
			else
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
