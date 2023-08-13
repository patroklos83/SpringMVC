package com.patroclos.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.patroclos.processmanager.IProcessManager;
import com.patroclos.processmanager.IProcess;
import com.patroclos.processmanager.ResponseDTO;
import com.patroclos.processmanager.RestProcessManager;

@Component
public class RestFacade {

	@Autowired
	private IProcessManager restProcessManager;	
//	@Autowired
//	private CRUDProcess CRUDProcess;
	
//	public ResponseEntity<ResponseDTO> export(Object input){
//		Process export = b -> UIProcess.export(b);
//		return (ResponseEntity<ResponseDTO>) restProcessManager.runProcess(export,input,UIProcess.PROCESS_NAME_EXPORT);
//	}
//	
//	public ResponseEntity<ResponseDTO> getImages(){
//		Process export = b -> UIProcess.getImages(false);
//		return (ResponseEntity<ResponseDTO>) restProcessManager.runProcess(export,null,UIProcess.PROCESS_NAME_GETIMAGES);
//	}
//	
//	public ResponseEntity<ResponseDTO> getBackroundImages(){
//		Process export = b -> UIProcess.getImages(true);
//		return (ResponseEntity<ResponseDTO>) restProcessManager.runProcess(export,null,UIProcess.PROCESS_NAME_GETIMAGES);
//	}
//	
//	public ResponseEntity<ResponseDTO> createNewUser(){
//		Process export = b -> UIProcess.createNewUser();
//		return (ResponseEntity<ResponseDTO>) restProcessManager.runProcess(export,null,UIProcess.PROCESS_CREATE_NEW_USER);
//	}

}
