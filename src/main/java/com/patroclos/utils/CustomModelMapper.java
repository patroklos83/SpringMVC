package com.patroclos.utils;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.patroclos.dto.BaseDTO;
import com.patroclos.model.BaseO;

@Component
public class CustomModelMapper {
	
	private final String MODEL_PACKAGE = SystemUtil.BASE_PACKAGE + "model.";
	
	@Autowired
	private ModelMapper ModelMapper;
	
	public String getModelEntityNameFromDTO(Class<? extends BaseDTO> classType) {
		var entity = classType.getSimpleName().replace("DTO", "");
		return entity;
	}

	@SuppressWarnings("unchecked")
	public Class<? extends BaseO> mapDTOClassToModelClass(Class<? extends BaseDTO> classType) throws ClassNotFoundException {
		var entity = MODEL_PACKAGE + classType.getSimpleName().replace("DTO", "");
		return (Class<? extends BaseO>)Class.forName(entity);
	}
	
	@SuppressWarnings("unchecked")
	public BaseO mapDTOtoModel(BaseDTO input) throws Exception {	
		var entityClass = MODEL_PACKAGE + input.getClass().getSimpleName().replace("DTO", "");
		BaseO o = ModelMapper.map(input,(Class<? extends BaseO>)Class.forName(entityClass));		
		return o;
	}
	
	public BaseDTO mapModeltoDTO(BaseO input, Class<? extends BaseDTO> classType) throws Exception {	
		return ModelMapper.map(input, classType);
	}

	public BaseO mapDTOtoModel(BaseDTO input, Class<? extends BaseO> classType) throws Exception {	
		return ModelMapper.map(input, classType);
	}
	
	public void mapBaseOToBaseO(BaseO input, BaseO output) throws Exception {	
		ModelMapper.map(input, output);
	}
}
