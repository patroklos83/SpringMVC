package com.patroclos.processmanager;

import com.patroclos.dto.BaseDTO;

@FunctionalInterface
public interface IProcess<T, R, E extends Exception>  {
	
	public R run(BaseDTO input) throws Exception;

}
