package com.patroclos.processmanager;

import org.springframework.stereotype.Component;

/***
 * Strategy Pattern. Inject the implementation of the process to be ran
 * @author Patros
 *
 */
@Component
public class RestProcessManager extends ProcessManager {

//	public ResponseEntity<ResponseDTO> runProcess(TProcess process, Object input)
//	{
//		ResponseEntity<ResponseDTO> result = null;
//		//enforce security validations,loging etc...
//		try
//		{ 
//			result = (ResponseEntity<ResponseDTO>) super.runProcess(process, input);
//		}catch (Exception e)
//		{
//			//loging errors
//
//		}
//		return result;
//	}
//	
//	public ResponseEntity<ResponseDTO> runProcess(TProcess process, Object input,String processName)
//	{
//		ResponseEntity<ResponseDTO> result = null;
//		//enforce security validations,loging etc...
//		try
//		{ 
//			result = (ResponseEntity<ResponseDTO>) super.runProcess(process, input,processName);
//		}catch (Exception e)
//		{
//			//loging errors
//
//		}
//		return result;
//	}
//	
//	public ResponseEntity<ResponseDTO> runProcess(Process process, Object input,String processName)
//	{
//		ResponseEntity<ResponseDTO> httpResult = null;
//		//enforce security validations,loging etc...
//		try
//		{			
//			Object result = super.runProcess(process, input,processName);
//			ResponseDTO responseDTO = new ResponseDTO();
//			responseDTO.setData(result);
//			httpResult = new ResponseEntity<ResponseDTO>(responseDTO,HttpStatus.OK);
//		}catch (Exception e)
//		{
//			//loging errors
//			ResponseDTO responseDTO = new ResponseDTO();
//			responseDTO.setData(e.getMessage());
//			httpResult = new ResponseEntity<ResponseDTO>(responseDTO,HttpStatus.INTERNAL_SERVER_ERROR);
//
//		}
//		return httpResult;
//	}

}
