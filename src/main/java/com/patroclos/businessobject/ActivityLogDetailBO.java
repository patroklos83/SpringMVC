package com.patroclos.businessobject;


import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.envers.DefaultRevisionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.patroclos.businessobject.core.BaseBO;
import com.patroclos.dto.AuditedProperty;
import com.patroclos.model.ActivityLog;
import com.patroclos.model.ActivityLogDetail;
import com.patroclos.model.BaseO;
import com.patroclos.repository.*;
import com.patroclos.utils.SystemUtil;

@Component
public class ActivityLogDetailBO extends BaseBO {
	

	@Autowired
	private AuditEnversRepository AuditEnversRepository;
	@Autowired
	private ActivityLogBO ActivityLogBO;
	
	private final String MODEL_PACKAGE_NAME_PREFIX = SystemUtil.BASE_PACKAGE + "model.";

	@Override
	public BaseO load(long id, Class<? extends BaseO> baseO) {
		ActivityLogDetail activityLogDetail = (ActivityLogDetail) super.loadBaseO(id, baseO);
		ActivityLog activityLog = (ActivityLog) ActivityLogBO.loadByProcessId(activityLogDetail.getProcessId());
		if (activityLog != null) {
			activityLogDetail.setActivityLog(activityLog);
		}
		return activityLogDetail;
	}

	@Transactional
	@Override
	public void save(BaseO baseO) throws Exception {
		super.saveBaseO(baseO);		
	}

	@SuppressWarnings("unchecked")
	public List<BaseO> getActivityLogDetailsByProcessId(String processId) {
		String hql = "FROM ActivityLogDetail A WHERE A.processId = :processId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("processId", processId);
		return (List<BaseO>) Repository.query(hql, params);
	}

	@Transactional
	public Map<String, AuditedProperty> getLogDetailsByEntityId(Long id) throws Exception {
		Map<String, AuditedProperty> auditedProperties = null;
		BaseO o = load(id, ActivityLogDetail.class);
		ActivityLogDetail activityLogDetails = (ActivityLogDetail)o;

		@SuppressWarnings("unchecked")
		Class<? extends BaseO> entity = (Class<? extends BaseO>) 
		Class.forName(MODEL_PACKAGE_NAME_PREFIX + activityLogDetails.getEntity());

		long entityId = activityLogDetails.getEntityId();
		String processId = activityLogDetails.getProcessId();

		List<Object[]> revResults = AuditEnversRepository.getEntityRevisionsByProcessId(entity, entityId, processId);
		//get new values from changed entity
		auditedProperties = getAuditedPropertiesNewValues(revResults, auditedProperties);

		//get old values from changed entity
		//A process could change the state of an entity multiple times, so find the first revision that changed
		//during a save on an entity
		Optional<AuditedProperty> minRevision = auditedProperties
				.values().stream().sorted(Comparator.comparing(AuditedProperty::getNewRevision)).findFirst();

		if (!minRevision.isEmpty()) {
			revResults = AuditEnversRepository.getEntityRevisionsBeforeRevision(entity, entityId, minRevision.get().getNewRevision());
			if (revResults != null)
			{
				auditedProperties = getAuditedPropertiesPreviousValues(revResults, auditedProperties);
			}			
		}
		
		if (auditedProperties != null && auditedProperties.size() > 0) {
			Iterator<AuditedProperty> itr = auditedProperties.values().iterator();
			while(itr.hasNext()) {
				AuditedProperty p = (AuditedProperty) itr.next();
				p.setEntity(activityLogDetails.getEntity());
				p.setEntityId(activityLogDetails.getEntityId());
			}
		}
		
      return auditedProperties;
	}

	private Map<String, AuditedProperty> getAuditedPropertiesNewValues(List<Object[]> revResults, Map<String, AuditedProperty> auditedProperties) throws Exception {
		if (revResults == null || revResults.size() == 0)
			return auditedProperties; 

		auditedProperties = auditedProperties == null ? new HashMap<String, AuditedProperty>() : auditedProperties;
		
		for ( Object entry : revResults ) {
			final Object[] array = (Object[]) entry;
			final BaseO revObject = (BaseO)array[0];
			final DefaultRevisionEntity revision = (DefaultRevisionEntity)array[1];
			final Set<String> propertiesChanged = (Set<String>) array[3];
			for (String propertyName : propertiesChanged) {
				AuditedProperty auditedProp = new AuditedProperty();
				auditedProp.setNewRevision(revision.getId());
				auditedProp.setPropName(propertyName);
				Object propVal = getFieldValueFromClassFields(revObject, propertyName);
				auditedProp.setNewValue(propVal.toString());
				auditedProperties.put(propertyName, auditedProp);
			}

		}
		return auditedProperties;
	}

	private Map<String, AuditedProperty> getAuditedPropertiesPreviousValues(List<Object[]> revResults, Map<String, AuditedProperty> auditedProperties) throws Exception {
		if (revResults == null || revResults.size() == 0)
			return auditedProperties; 

		for ( Object entry : revResults ) {
			final Object[] array = (Object[]) entry;
			final DefaultRevisionEntity revision = (DefaultRevisionEntity)array[1];
			final BaseO revObject = (BaseO)array[0];
			for ( String propertyName : auditedProperties.keySet()) {
				AuditedProperty auditedProp = auditedProperties.get(propertyName);
				auditedProp.setPrevRevision(revision.getId());
				auditedProp.setPropName(propertyName);
				Object propVal = getFieldValueFromClassFields(revObject, propertyName);
				auditedProp.setPrevValue(propVal.toString());			
				auditedProperties.put(propertyName, auditedProp);
			}

		}
		return auditedProperties;
	}

	private Object getFieldValueFromClassFields(Object revObject, String propertyName) throws Exception {
		Object value = null;

		Class<?> clazz = Class.forName(MODEL_PACKAGE_NAME_PREFIX + revObject.getClass().getSimpleName());
		Field[] baseFields = clazz.getSuperclass().getDeclaredFields();
		Field[] fields = clazz.getDeclaredFields();

		List<Field> r = Arrays.stream(baseFields).filter(f -> f.getName().equals(propertyName)).collect(Collectors.toList());
		if (r != null && r.size() > 0) {
			Field modifField = (Field)(r.get(0));
			modifField.setAccessible(true);
			Object propVal = modifField.get(revObject);
			value = propVal;
		}

		r = Arrays.stream(fields).filter(f -> f.getName().equals(propertyName)).collect(Collectors.toList());
		if (r != null && r.size() >0) {
			Field modifField = (Field)(r.get(0));
			modifField.setAccessible(true);
			Object propVal = modifField.get(revObject);
			value = propVal;
		}

		return value;
	}

	@Override
	public void validateOnSave(BaseO baseO) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(BaseO baseO) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validateOnDelete(BaseO baseO) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
