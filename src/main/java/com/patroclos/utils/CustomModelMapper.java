package com.patroclos.utils;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.patroclos.dto.BaseDTO;
import com.patroclos.model.BaseO;

@Component
public class CustomModelMapper {
	
	@Autowired
	private ModelMapper ModelMapper;
	
	public ModelMapper getModelMapper() {
		return this.ModelMapper;
	}
	
	public String getModelEntityNameFromDTO(Class<? extends BaseDTO> classType) {
		var entity = classType.getSimpleName().replace("DTO", "");
		return entity;
	}

	@SuppressWarnings("unchecked")
	public Class<? extends BaseO> mapDTOClassToModelClass(Class<? extends BaseDTO> classType) throws ClassNotFoundException {
		var entity = SystemUtil.MODEL_PACKAGE + classType.getSimpleName().replace("DTO", "");
		return (Class<? extends BaseO>)Class.forName(entity);
	}
	
	@SuppressWarnings("unchecked")
	public Class<? extends BaseDTO> mapModelClassToDTOClass(String modelClassName) throws ClassNotFoundException {
		var entity = SystemUtil.DTO_PACKAGE + modelClassName + "DTO";
		return (Class<? extends BaseDTO>)Class.forName(entity);
	}
	
	@SuppressWarnings("unchecked")
	public Class<? extends BaseDTO> mapModelClassToDTOClass(Class<? extends BaseO> classType) throws ClassNotFoundException {
		var entity = SystemUtil.DTO_PACKAGE + classType.getSimpleName() + "DTO";
		return (Class<? extends BaseDTO>)Class.forName(entity);
	}
	
	@SuppressWarnings("unchecked")
	public BaseO mapDTOtoModel(BaseDTO input) throws Exception {	
		var entityClass =SystemUtil. MODEL_PACKAGE + input.getClass().getSimpleName().replace("DTO", "");
		BaseO o = ModelMapper.map(input,(Class<? extends BaseO>)Class.forName(entityClass));	
		return o;
	}
	
	@SuppressWarnings("unchecked")
	public BaseDTO mapDTOtoDTO(BaseDTO input) throws Exception {	
		var entityClass = SystemUtil.DTO_PACKAGE + input.getClass().getSimpleName();
		BaseDTO o = ModelMapper.map(input, (Class<? extends BaseDTO>)Class.forName(entityClass));		
		return o;
	}
	
	@SuppressWarnings("unchecked")
	public BaseO mapModelToModel(BaseO input) throws Exception {	
		var entityClass = SystemUtil.MODEL_PACKAGE + input.getClass().getSimpleName();
		
		// When maaping to the same Type, create a new Instance-copy
		TypeMap<?, ?> typeMap = ModelMapper.getTypeMap(input.getClass(), (Class<? extends BaseO>)Class.forName(entityClass));
		if (typeMap == null) { // if not  already added
			ModelMapper.createTypeMap(input.getClass(), (Class<? extends BaseO>)Class.forName(entityClass));	
		}
		
		BaseO o = ModelMapper.map(input, (Class<? extends BaseO>)Class.forName(entityClass));	
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
