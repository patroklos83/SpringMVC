package com.patroclos.process;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.patroclos.dto.BaseDTO;
import com.patroclos.dto.SummaryDTO;
import com.patroclos.service.CRUDService;
import com.patroclos.businessobject.*;

@Component
public class CRUDProcess extends BaseProcess {

	@Autowired
	private CRUDService CRUDService;

	public static final String PROCESS_NAME_SEARCH = com.patroclos.businessobject.PrivilegeBO.PRIVILEGE_NAME_SEARCH;
	public static final String PROCESS_NAME_CREATE = com.patroclos.businessobject.PrivilegeBO.PRIVILEGE_NAME_CREATE;
	public static final String PROCESS_NAME_READ = com.patroclos.businessobject.PrivilegeBO.PRIVILEGE_NAME_READ;
	public static final String PROCESS_NAME_UPDATE = com.patroclos.businessobject.PrivilegeBO.PRIVILEGE_NAME_UPDATE;
	public static final String PROCESS_NAME_DELETE = com.patroclos.businessobject.PrivilegeBO.PRIVILEGE_NAME_DELETE;
	public static final String PROCESS_NAME_CANCEL = com.patroclos.businessobject.PrivilegeBO.PRIVILEGE_NAME_CANCEL;

	/***
	 * Just a dummy process method to execute validation
	 * for user access to search
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public BaseDTO search(BaseDTO input) throws Exception {
		SummaryDTO inputDTO = (SummaryDTO) input;
		//		SqlRowSet result = CRUDService.search(inputDTO.getQuery(), inputDTO.getQueryArgs());
		SummaryDTO resultDTO = new SummaryDTO();
		//resultDTO.setQueryResults(result);
		return resultDTO;
	}

	public BaseDTO load(long id, Class<? extends BaseDTO> input) throws Exception {
		BaseDTO dto = CRUDService.load(id, input);
		return dto;
	}

	public List<BaseDTO> loadAll(Class<? extends BaseDTO> input) throws Exception {
		List<BaseDTO> dto = CRUDService.load(input);
		return dto;
	}

	public BaseDTO delete(BaseDTO input) throws Exception {
		CRUDService.delete(input);
		return input;
	}

	public BaseDTO saveNew(BaseDTO input) throws Exception {
		return save(input);
	}

	public BaseDTO saveUpdate(BaseDTO input) throws Exception {
		return save(input);
	}

	public BaseDTO save(BaseDTO input) throws Exception {
		CRUDService.save(input);
		return CRUDService.load(input.getId(), input.getClass());
	}

	public BaseDTO cancel(BaseDTO input) {
		return input;
	}

	public boolean isCRUDProcess(String processName) {
		if (processName.equals(PROCESS_NAME_CREATE) 
				|| (processName.equals(PROCESS_NAME_READ)
						|| processName.equals(PROCESS_NAME_DELETE)
						|| processName.equals(PROCESS_NAME_UPDATE)))
			return true;

		return false;
	}

}
