package com.patroclos.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.patroclos.dto.BaseDTO;
import com.patroclos.service.CRUDService;

@Component
public class CRUDProcess extends BaseProcess {

	@Autowired
	private CRUDService CRUDService;

	public final String PROCESS_NAME_CREATE = "CREATE";
	public final String PROCESS_NAME_READ = "READ";
	public final String PROCESS_NAME_UPDATE = "UPDATE";
	public final String PROCESS_NAME_DELETE = "DELETE";
	public final String PROCESS_NAME_CANCEL = "CANCEL";

	public BaseDTO load(long id, Class<? extends BaseDTO> input) throws Exception {
		BaseDTO dto = CRUDService.load(id, input);
		return dto;
	}

	public Object delete(BaseDTO input) throws Exception {
		CRUDService.delete(input);
		return input;
	}

	public Object saveNew(BaseDTO input) throws Exception {
		return save(input);
	}

	public Object saveUpdate(BaseDTO input) throws Exception {
		return save(input);
	}

	public Object save(BaseDTO input) throws Exception {
		CRUDService.save(input);
		return CRUDService.load(input.getId(), input.getClass());
	}

	public Object cancel(BaseDTO input) {
		return input;
	}

}
