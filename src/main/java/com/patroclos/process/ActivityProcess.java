package com.patroclos.process;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.patroclos.dto.ActivityLogDTO;
import com.patroclos.dto.AuditedPropertyDTO;
import com.patroclos.dto.BaseDTO;
import com.patroclos.dto.SummaryDTO;
import com.patroclos.dto.UserDTO;
import com.patroclos.model.BaseO;
import com.patroclos.model.User;
import com.patroclos.service.*;
import com.patroclos.utils.*;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class ActivityProcess extends BaseProcess {

	@Autowired
	private CRUDService CRUDService;
	@Autowired
	private UserService UserService;
	@Autowired
	private ActivityService ActivityService;
	@Autowired
	private CustomModelMapper CustomModelMapper;
	@Autowired
	private IAuthenticationService AuthenticationService;

	public final String PROCESS_NAME_LOGIN = "LOGIN";
	public final String PROCESS_NAME_LOGOUT = "LOGOUT";
	public final String PROCESS_NAME_LOGIN_FAIL = "LOGIN FAIL";
	public final String PROCESS_NAME_LOCK_ACCOUNT = "LOCK ACCOUNT";
	public final String PROCESS_NAME_ACTIVITYLOG_REVISION_CHANGES = "RETRIEVE REVISION CHANGES";

	public void logActivity(ActivityLogDTO activityLogDto) throws Exception {

		String clientIp = WebUtils.getClientIpAddressIfServletRequestExist();
		User user = AuthenticationService.getLoggedDbUser();
		
		String entityId = "";
		String entityOrDto = "";
		if (activityLogDto.getInputType() != null) {
			entityOrDto = activityLogDto.getInputType().getTypeName();
			if (entityOrDto.contains(".")) { //remove package name from Type Name
				String[] entityArr = entityOrDto.split("\\.");
				entityOrDto = entityArr[entityArr.length-1];
			}
		}

		String summary = user.getUsername() + " ran process ["+ activityLogDto.getProcess() +"] on ["+ entityOrDto +"] with id ["+ activityLogDto.getId() +"]";
		BaseO o = null;
		if (activityLogDto.getInput() != null) {
			entityOrDto = activityLogDto.getInput().getClass().getSimpleName();
			try {
				if (!(activityLogDto.getInput() instanceof SummaryDTO))
					o = CustomModelMapper.mapDTOtoModel(activityLogDto.getInput());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			if (o != null) {
				//entityOrDto = entityOrDto.replace("DTO", "");
				entityId = activityLogDto.getId() != null ? activityLogDto.getId().toString() : o.getId().toString();
				summary = user.getUsername() + " ran process ["+ activityLogDto.getProcess() +"] on entity ["+ entityOrDto +"] with id ["+ entityId +"]";
			}
		}

		activityLogDto.setSummary(summary);
		activityLogDto.setClientIp(clientIp);
		CRUDService.save(activityLogDto);
	}

	public void logLoginActivity(jakarta.servlet.http.HttpServletRequest request) throws Exception {
		String clientIp = WebUtils.getClientIpAddress(request);
		User user = AuthenticationService.getLoggedDbUser();
		String processId = ProcessUtil.getActivityProcessId();
		//Login is an internal process where it not executed from the Facade, therefore
		//set the current thread's processid from this method
		Thread.currentThread().setName(processId);

		ActivityLogDTO activityLogDto = new ActivityLogDTO();
		activityLogDto.setSummary(String.format("User [%s] logged in at %s", user.getUsername(), DateUtil.getUICurrentDateTime()));
		activityLogDto.setProcess(PROCESS_NAME_LOGIN);
		activityLogDto.setResult(PROCESS_RUN_RESULT_SUCCESS);
		activityLogDto.setProcessId(processId);
		activityLogDto.setClientIp(clientIp);
		try {
			CRUDService.save(activityLogDto);
		}
		finally {
			//reset thread's name after process and activity log insert is complete
			Thread.currentThread().setName("");
		}
	}

	public void logLogoutActivity(HttpServletRequest request,  User user) throws Exception {
		String clientIp = WebUtils.getClientIpAddress(request);
		String processId = ProcessUtil.getActivityProcessId();
		//Logout is an internal process where it not executed from the Facade, therefore
		//set the current thread's processid from this method
		Thread.currentThread().setName(processId);

		ActivityLogDTO activityLogDto = new ActivityLogDTO();
		activityLogDto.setSummary(String.format("User [%s] logged out at %s", user.getUsername(), DateUtil.getUICurrentDateTime()));
		activityLogDto.setProcess(PROCESS_NAME_LOGOUT);
		activityLogDto.setProcessId(processId);
		activityLogDto.setResult(PROCESS_RUN_RESULT_SUCCESS);
		activityLogDto.setClientIp(clientIp);
		activityLogDto.setCreatedByuser(user);
		try {
			CRUDService.save(activityLogDto);
		}
		finally {
			//reset thread's name after process and activity log insert is complete
			Thread.currentThread().setName("");
		}
	}

	@Transactional
	public void loginFailActivity(jakarta.servlet.http.HttpServletRequest request, String userName, String exceptionError) throws Exception {
		String clientIp = WebUtils.getClientIpAddress(request);
		String processId = ProcessUtil.getActivityProcessId();
		//Login is an internal process where it not executed from the Facade, therefore
		//set the current thread's processid from this method
		Thread.currentThread().setName(processId);

		ActivityLogDTO activityLogDto = new ActivityLogDTO();
		activityLogDto.setSummary(String.format("A failed login attempt with username [%s] . Reason: [%s]", userName, exceptionError));
		activityLogDto.setProcess(PROCESS_NAME_LOGIN_FAIL);
		activityLogDto.setProcessId(processId);
		activityLogDto.setResult(PROCESS_RUN_RESULT_FAIL);
		activityLogDto.setClientIp(clientIp);
		try {
			CRUDService.save(activityLogDto);
			ActivityService.logFailedLoginAttempt(userName);
			int countLoginFailedAttemps = ActivityService.countLoginFailedAttempsByUserName(userName);
			if (countLoginFailedAttemps > 10) { 
				// After more than 10 failed attempts, 
				// in a specified period of time, disable/lock account
				//https://owasp.org/www-project-web-security-testing-guide/latest
				//4-Web_Application_Security_Testing/04-Authentication_Testing/03-Testing_for_Weak_Lock_Out_Mechanism
				UserDTO userDTO = (UserDTO) UserService.loadUserByUsername(userName);
				if (userDTO != null) {
					UserService.disableUserByUsername(userDTO);
					activityLogDto = new ActivityLogDTO();
					activityLogDto.setSummary(String.format("Existing Account with username [%s] was set to disabled", userName));
					activityLogDto.setProcess(PROCESS_NAME_LOCK_ACCOUNT);
					activityLogDto.setProcessId(processId);
					activityLogDto.setClientIp(clientIp);
					CRUDService.save(activityLogDto);
				}
			}
		}
		finally {
			//reset thread's name after process and activity log insert is complete
			Thread.currentThread().setName("");
		}
	}

	public Map<String, AuditedPropertyDTO> getActivityLogRevisionChanges(BaseDTO input) throws Exception {
		return CRUDService.getActivityLogRevisionChanges(input.getId());
	}
}
