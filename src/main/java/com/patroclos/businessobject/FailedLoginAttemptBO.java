package com.patroclos.businessobject;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.patroclos.businessobject.core.BaseBO;
import com.patroclos.model.BaseO;
import com.patroclos.model.FailedLoginAttempt;

@Component
public class FailedLoginAttemptBO extends BaseBO{
	
	public final int FAILED_LOGIN_ATTEMPTS_TIME_SECONDS_THRESHOLD = 60 * 10;

	public void save(String userName) throws Exception {
		FailedLoginAttempt failedLoginAttempt = new FailedLoginAttempt();
		failedLoginAttempt.setUsername(userName);
		failedLoginAttempt.setTimestamp(Instant.now());
		Repository.save(failedLoginAttempt);	
	}

	@SuppressWarnings("unchecked")
	public int countFailedLoginsByUsername(String userName) throws Exception {
		int countFailedLoginAttempts = 0;
		Map<String, Object> params = Map.of("userName", userName, "recentTimestamp", Instant.now().minusSeconds(FAILED_LOGIN_ATTEMPTS_TIME_SECONDS_THRESHOLD));
		List<FailedLoginAttempt> failedLoginAttempts = (List<FailedLoginAttempt>) Repository
				.query("from FailedLoginAttempt F where F.username = :userName and timestamp > :recentTimestamp", params);	
	    if (failedLoginAttempts != null) {
	    	countFailedLoginAttempts = failedLoginAttempts.size();
	    }
	    return countFailedLoginAttempts;
	}

	@Override
	public BaseO load(long id, Class<? extends BaseO> baseO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(BaseO baseO) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateOnSave(BaseO baseO) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(BaseO baseO) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateOnDelete(BaseO baseO) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
