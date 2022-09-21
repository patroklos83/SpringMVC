package com.patroclos.service;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.patroclos.businessobject.ActivityLogDetailBO;
import com.patroclos.businessobject.core.BaseBO;
import com.patroclos.dto.AuditedProperty;
import com.patroclos.dto.BaseDTO;
import com.patroclos.exception.SystemException;
import com.patroclos.model.BaseO;
import com.patroclos.utils.SystemUtil;

@Component
public class CRUDService extends BaseService {
	
	private final String BUSINESS_PACKAGE = SystemUtil.BASE_PACKAGE + "businessobject.";

	@Autowired
	private ActivityLogDetailBO ActivityLogDetailsBO;	
	@Autowired
    ConfigurableApplicationContext applicationContext;

	private BaseBO getBussinessBeanFromDTO(BaseDTO input) throws BeansException, ClassNotFoundException {
		var businessClass = BUSINESS_PACKAGE + input.getClass().getSimpleName().replace("DTO", "BO");
		BaseBO baseBo = (BaseBO) applicationContext.getBean(Class.forName(businessClass));
		return baseBo;
	}
	
	private BaseBO getBussinessBeanFromClassType(Class<?> classType) throws BeansException, ClassNotFoundException {
		var businessClass = BUSINESS_PACKAGE + classType.getSimpleName().replace("DTO", "BO");
		BaseBO baseBo = (BaseBO) applicationContext.getBean(Class.forName(businessClass));
		return baseBo;
	}

	public BaseDTO load(long id, Class<? extends BaseDTO> classType) throws Exception {	
		BaseBO baseBo = getBussinessBeanFromClassType(classType);	
		BaseO o = baseBo.load(id, CustomModelMapper.mapDTOClassToModelClass(classType));
		if (o != null && o.getIsDeleted() == 1) return null;
		return CustomModelMapper.mapModeltoDTO(o, classType);
	}
	
	@Transactional
	public void save(BaseDTO input) throws Exception {	
		save(input, false);
	}

	@Transactional
	public void save(BaseDTO input, boolean saveAsDeleted) throws Exception {	
		BaseBO baseBo = getBussinessBeanFromDTO(input);
		
		BaseO oDirty = CustomModelMapper.mapDTOtoModel(input);
		oDirty.setIsNew(input.isNew());
		
		if (!input.isNew()) {
			BaseO o = baseBo.load(input.getId(), CustomModelMapper.mapDTOClassToModelClass(input.getClass()));
			if (o.getVersion() != oDirty.getVersion()) {
				throw new SystemException("Entity was already modified by another process. Please load again to the newer version");
			}
			CustomModelMapper.mapBaseOToBaseO(oDirty, o);
			if (saveAsDeleted) baseBo.delete(o); else baseBo.save(o);
			input.setId(o.getId());
		}
		else {
			if (saveAsDeleted) baseBo.delete(oDirty); else baseBo.save(oDirty);
			input.setId(oDirty.getId());
		}
	}

	@Transactional
	public void delete(BaseDTO input) throws Exception {	
		save(input, true);
	}

	public Map<String, AuditedProperty> getActivityLogRevisionChanges(Long id)  throws Exception {
		Map<String, AuditedProperty> auditedProperties = ActivityLogDetailsBO.getLogDetailsByEntityId(id);
		return auditedProperties;
	}
}
