package com.patroclos.processmanager;

import com.patroclos.dto.BaseDTO;
import com.patroclos.exception.SystemException;
import com.patroclos.model.BaseO;

public interface IProcessManager {
	
	public Object runProcess(IProcess<?, ?, ?> process, BaseDTO input, String processName) throws Exception;

	public Object runProcess(IProcess<?, ?, ?> process, Class<? extends BaseDTO> inputType, Long id, String processName) throws Exception;
	
	public Object runProcess(IProcess<?, ?, ?> process, Class<? extends BaseDTO> inputType ,BaseDTO input, Long id, String processName) throws Exception;

}
