package com.patroclos.processmanager;

import com.patroclos.dto.BaseDTO;

public abstract class TProcess implements IProcess<BaseDTO, Object, Exception> {
		
	public String processName;

}
