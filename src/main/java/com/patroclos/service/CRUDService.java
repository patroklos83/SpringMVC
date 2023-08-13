package com.patroclos.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.patroclos.utils.SystemUtil;
import com.patroclos.businessobject.ActivityLogDetailBO;
import com.patroclos.businessobject.core.BaseBO;
import com.patroclos.dto.AuditedPropertyDTO;
import com.patroclos.dto.BaseDTO;
import com.patroclos.exception.SystemException;
import com.patroclos.model.BaseO;

@Component
public class CRUDService extends BaseService {
	
	private final String BUSINESS_PACKAGE = SystemUtil.BASE_PACKAGE + "businessobject.";

	@Autowired
	private ActivityLogDetailBO ActivityLogDetailsBO;	
	@Autowired
    private ConfigurableApplicationContext ApplicationContext;
	
	public SqlRowSet search(String query, MapSqlParameterSource args) {
		return Repository.customNativeQuery(query, args);
	}

	/***
	 * TODO: Class.forName(businessClass) creates a new instance of the object each time
	 * Better to implement a functionality to create and use only one instance of the BO
	 * object
	 * @param input
	 * @return
	 * @throws BeansException
	 * @throws ClassNotFoundException
	 */
	private BaseBO getBussinessBeanFromDTO(BaseDTO input) throws BeansException, ClassNotFoundException {
		var businessClass = BUSINESS_PACKAGE + input.getClass().getSimpleName().replace("DTO", "BO");
		BaseBO baseBo = (BaseBO) ApplicationContext.getBean(Class.forName(businessClass));
		return baseBo;
	}
	
	/***
	 * Find corresponding Business object (BO) from Ioc container
	 * @param classType
	 * @return
	 * @throws BeansException
	 * @throws ClassNotFoundException
	 */
	private BaseBO getBussinessBeanFromClassType(Class<?> classType) throws BeansException, ClassNotFoundException {
		String beanName = classType.getSimpleName().replace("DTO", "BO");
		beanName = beanName.substring(0, 1).toLowerCase().concat(beanName.substring(1));
		BaseBO baseBo = (BaseBO) ApplicationContext.getBean(beanName);
		return baseBo;
	}

	public BaseDTO load(long id, Class<? extends BaseDTO> classType) throws Exception {	
		BaseBO baseBo = getBussinessBeanFromClassType(classType);	
		BaseO o = baseBo.load(id, CustomModelMapper.mapDTOClassToModelClass(classType));
		if (o != null && o.getIsDeleted() == 1) return null;
		return CustomModelMapper.mapModeltoDTO(o, classType);
	}
	
	public List<BaseDTO> load(Class<? extends BaseDTO> classType) throws Exception {	
		BaseBO baseBo = getBussinessBeanFromClassType(classType);	
		List<BaseO> list = baseBo.load(CustomModelMapper.mapDTOClassToModelClass(classType));
		if (list != null)
			list = list.stream().filter(e -> e.getIsDeleted() == 0).collect(Collectors.toList());
		
		return list != null ? list.stream().map(e -> {
			try {
				return CustomModelMapper.mapModeltoDTO(e, classType);
			} catch (Exception e1) {
				e1.printStackTrace();
				throw new SystemException(e1);
			}			
		}).collect(Collectors.toList()) : null;
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
				throw new SystemException("Entity was already modified by another process. Please load again the newer version");
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

	public Map<String, AuditedPropertyDTO> getActivityLogRevisionChanges(Long id)  throws Exception {
		Map<String, AuditedPropertyDTO> auditedProperties = ActivityLogDetailsBO.getLogDetailsByEntityId(id);
		return auditedProperties;
	}
}
