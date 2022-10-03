package com.patroclos.utils;

import java.time.LocalDateTime;
import java.util.UUID;

public class ProcessUtil {
	
	public static final String PROCESS_ID_PREFIX = "ProcessRunID_";
	
	public static String getActivityProcessId() {
		String processId = PROCESS_ID_PREFIX
				+ DateUtil.convertDateToString(LocalDateTime.now(), "ddMMyyyy")  + "_" 
				+ UUID.randomUUID().toString().replace("-", "");
		return processId;
	}

}
