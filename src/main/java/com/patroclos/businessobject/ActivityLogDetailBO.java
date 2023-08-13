package com.patroclos.businessobject;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.patroclos.utils.ReflectionUtil;
import com.patroclos.utils.SystemUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ManyToMany;
import com.patroclos.model.ActivityLog;
import com.patroclos.repository.AuditEnversRepository;
import com.patroclos.businessobject.core.BaseBO;
import com.patroclos.dto.AuditedPropertyDTO;
import com.patroclos.model.ActivityLogDetail;
import com.patroclos.model.BaseO;
import com.patroclos.model.enums.AuditedPropertyFieldType;

@Component
public class ActivityLogDetailBO extends BaseBO {

	@Autowired
	private AuditEnversRepository AuditEnversRepository;

	@Autowired
	private ActivityLogBO ActivityLogBO;

	@jakarta.persistence.PersistenceContext 
	protected EntityManager em;

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
	
	@Override
	public List<BaseO> loadAll() {
		return super.load(ActivityLogDetail.class);
	}

	@Transactional
	@Override
	public void save(BaseO baseO) throws Exception {
		super.saveBaseO(baseO);		
	}
	
	@Override
	public void saveAll(List<BaseO> baseOs) throws Exception {
		super.saveAllBaseO(baseOs);
	}

	@SuppressWarnings("unchecked")
	public List<BaseO> getActivityLogDetailsByProcessId(String processId) {
		String hql = "FROM ActivityLogDetail A WHERE A.processId = :processId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("processId", processId);
		return (List<BaseO>) Repository.query(hql, params);
	}

	/***
	 * Collect the Audited logs per field changed of a specific entity.
	 * The input Id parameter refers to the ActivityLogDetails table 
	 * primary key.
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public Map<String, AuditedPropertyDTO> getLogDetailsByEntityId(Long id) throws Exception {
		Map<String, AuditedPropertyDTO> auditedProperties = null;

		// Step 1. Load ActivityDetailsLog
		BaseO o = load(id, ActivityLogDetail.class);
		ActivityLogDetail activityLogDetails = (ActivityLogDetail)o;

		// Step 2. Collect Entity affected
		@SuppressWarnings("unchecked")
		Class<? extends BaseO> entity = (Class<? extends BaseO>) 
		Class.forName(MODEL_PACKAGE_NAME_PREFIX + activityLogDetails.getEntity());

		// Step 3. Collect entity's unique Id and ProcessId which was involved in
		// the CRUD operation
		long entityId = activityLogDetails.getEntityId();
		String processId = activityLogDetails.getProcessId();

		// Step 4. Collect the Audited Logs using Hibernate Envers AuditReader
		List<Object[]> revResults = AuditEnversRepository.getEntityRevisionsByProcessId(entity, entityId, processId);

		final Object[] array = (Object[]) revResults.get(0);
		final BaseO revObject = (BaseO)array[0];
		final DefaultRevisionEntity revision = (DefaultRevisionEntity)array[1];
		revObject.setIsNew(false);

		// Step 5. Collect the new and previous values of the fields
		// Collect the new values from the changed entity
		auditedProperties = getAuditedPropertiesNewValues(revResults, auditedProperties);

		// Collect the previous values from the changed entity		
		// A process could change the state of an entity multiple times, so find the first revision that changed
		// during a CRUD operstion on the entity.
		// For example
		Optional<AuditedPropertyDTO> minRevision = auditedProperties
				.values().stream().sorted(Comparator.comparing(AuditedPropertyDTO::getNewRevision)).findFirst();

		// Check whether this entity was created with only 1 revision and no other update events
		var entityPreviousRevisions = AuditEnversRepository.getEntityRevisionsBeforeRevision(entity, entityId, revision.getId());
		boolean isNewlyCreatedEntity = entityPreviousRevisions == null || entityPreviousRevisions.size() == 0;

		if (isNewlyCreatedEntity) {
			revObject.setIsNew(true);
			// If this is a [create new entity] event, this is the first revision, therefore no changes are present
			auditedProperties = new HashMap<String, AuditedPropertyDTO>();
			Map<String, AuditedPropertyDTO> auditedProps = getNewCreatedEntityValues(revObject, revision.getId());
			if (auditedProps != null)
				auditedProperties.putAll(auditedProps);
		}	
		else if (!minRevision.isEmpty()) {
			revResults = AuditEnversRepository.getEntityRevisionsBeforeRevision(entity, entityId, minRevision.get().getNewRevision());
			if (revResults != null && revResults.size() > 0)
			{
				auditedProperties = getAuditedPropertiesPreviousValues(revResults, auditedProperties);
			}
			
			// Step 6. Collect the manyToMany/OneToMany relation parent-child field changes - if any
			Map<String, AuditedPropertyDTO> auditedPropsLists = getAuditedPropertiesForEntityListValuesChanged(
					entity, 
					entityId, 
					minRevision.get().getPrevRevision(),
					minRevision.get().getNewRevision());

			if (auditedPropsLists != null)
				auditedProperties.putAll(auditedPropsLists);
		}
		else
		{		
			// Usually this scenario is possible only when 1 manyToMany/OneToMany relation is affected on an entity
			// No other fields were affected
			var revResultsPrev = AuditEnversRepository.getEntityRevisionsBeforeRevision(entity, entityId, revision.getId());
			if (revResultsPrev != null && revResultsPrev.size() > 0) {
				
				final Object[] arrayPrev = (Object[]) revResultsPrev.get(revResultsPrev.size() - 1);
				final BaseO revObjectPrev = (BaseO)arrayPrev[0];
				final DefaultRevisionEntity revisionPrev = (DefaultRevisionEntity)arrayPrev[1];

				// Step 6. Collect the Many/Many relation parent-child field changes - if any
				Map<String, AuditedPropertyDTO> auditedPropsLists = getAuditedPropertiesForEntityListValuesChanged(
						entity, 
						entityId, 
						revisionPrev.getId(),
						revision.getId());

				if (auditedPropsLists != null)
					auditedProperties.putAll(auditedPropsLists);
			}
		}

		// Step 7. Include Entity/EntityId in the AuditedProperties
		if (auditedProperties != null && auditedProperties.size() > 0) {
			Iterator<AuditedPropertyDTO> itr = auditedProperties.values().iterator();
			while(itr.hasNext()) {
				AuditedPropertyDTO p = (AuditedPropertyDTO) itr.next();
				p.setEntity(activityLogDetails.getEntity());
				p.setEntityId(activityLogDetails.getEntityId());
			}
		}

		return auditedProperties;
	}

	private Map<String, AuditedPropertyDTO> getNewCreatedEntityValues(BaseO revObject, int revisionId) throws Exception {

		Map<String, AuditedPropertyDTO> auditedProperties = new HashMap<String, AuditedPropertyDTO>();

		BaseO newlyCreatedEntity = loadEntityListValues(revObject, 0);
		var publicFields = ReflectionUtil.getFieldList(newlyCreatedEntity.getClass());
		for(var field : publicFields)
		{		
			String propertyName = field.getName();
			AuditedPropertyDTO auditedProp = new AuditedPropertyDTO();
			auditedProp.setFieldType(AuditedPropertyFieldType.Single);
			auditedProp.setNewRevision(revisionId);
			auditedProp.setPropName(propertyName);
			Object propVal = getFieldValueFromClassFields(newlyCreatedEntity, propertyName);
			if (propVal != null)
				auditedProp.setNewValue(propVal.toString());
			auditedProperties.put(propertyName, auditedProp);
		}

		Map<String, AuditedPropertyDTO> auditedPropsLists = getAuditedPropertiesForEntityListValuesChanged(
				newlyCreatedEntity,
				newlyCreatedEntity,
				newlyCreatedEntity.getClass(), 
				newlyCreatedEntity.getId(), 
				revisionId,
				revisionId);

		if (auditedPropsLists != null)
			auditedProperties.putAll(auditedPropsLists);

		// set previous values to null since there weren't any data before. This is a new version of the entity
		if (auditedProperties != null && auditedProperties.size() > 0) {
			Iterator<AuditedPropertyDTO> itr = auditedProperties.values().iterator();
			while(itr.hasNext()) {
				AuditedPropertyDTO p = (AuditedPropertyDTO) itr.next();
				p.setPrevValue(null);
				p.setPrevValues(null);
			}
		}

		return auditedProperties;
	}

	private Map<String, AuditedPropertyDTO> getAuditedPropertiesNewValues(List<Object[]> revResults, Map<String, AuditedPropertyDTO> auditedProperties) throws Exception {
		if (revResults == null || revResults.size() == 0)
			return auditedProperties; 

		auditedProperties = auditedProperties == null ? new HashMap<String, AuditedPropertyDTO>() : auditedProperties;

		for (Object entry : revResults) {
			final Object[] array = (Object[]) entry;
			final BaseO revObject = (BaseO)array[0];
			final DefaultRevisionEntity revision = (DefaultRevisionEntity)array[1];
			final Set<String> propertiesChanged = (Set<String>) array[3];
			for (String propertyName : propertiesChanged) {
				AuditedPropertyDTO auditedProp = new AuditedPropertyDTO();
				auditedProp.setFieldType(AuditedPropertyFieldType.Single);
				auditedProp.setNewRevision(revision.getId());
				auditedProp.setPropName(propertyName);
				Object propVal = getFieldValueFromClassFields(revObject, propertyName);
				if (propVal != null)
					auditedProp.setNewValue(propVal.toString());
				auditedProperties.put(propertyName, auditedProp);
			}

		}

		return auditedProperties;
	}

	private Map<String, AuditedPropertyDTO> getAuditedPropertiesPreviousValues(List<Object[]> revResults, Map<String, AuditedPropertyDTO> auditedProperties) throws Exception {
		if (revResults == null || revResults.size() == 0)
			return auditedProperties; 

		for ( Object entry : revResults ) {
			final Object[] array = (Object[]) entry;
			final DefaultRevisionEntity revision = (DefaultRevisionEntity)array[1];
			final BaseO revObject = (BaseO)array[0];
			for (String propertyName : auditedProperties.keySet()) {
				AuditedPropertyDTO auditedProp = auditedProperties.get(propertyName);
				auditedProp.setFieldType(AuditedPropertyFieldType.Single);
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
			if (propVal instanceof BaseO) {
				propVal = String.format("Id #%s", ((BaseO)propVal).getId());
			}
			value = propVal;
		}

		r = Arrays.stream(fields).filter(f -> f.getName().equals(propertyName)).collect(Collectors.toList());
		if (r != null && r.size() >0) {
			Field modifField = (Field)(r.get(0));
			modifField.setAccessible(true);
			Object propVal = modifField.get(revObject);
			if (propVal instanceof BaseO) {
				propVal = String.format("Id #%s", ((BaseO)propVal).getId());
			}
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


	private Map<String, AuditedPropertyDTO> getAuditedPropertiesForEntityListValuesChanged(
			Class<? extends BaseO> entity, 
			Long entityId, 
			int previousRevisionNum,
			int newRevisionNum) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException {

		BaseO previousEntityVersion = null;
		BaseO newEntityVersion = null;

		// Find the manyToMany/OneToMany relation changes
		// Load newest version of entity
		if (newRevisionNum > 0)
		{
			newEntityVersion = (BaseO) AuditEnversRepository.getEntityAtRevision(entity, entityId,  newRevisionNum);
			if (newEntityVersion != null)
				newEntityVersion = loadEntityListValues(newEntityVersion, newRevisionNum);
		}

		// Load previous version of entity
		if (previousRevisionNum > 0)
		{
			previousEntityVersion = (BaseO) AuditEnversRepository.getEntityAtRevision(entity, entityId, previousRevisionNum);
			if (previousEntityVersion != null)
				previousEntityVersion = loadEntityListValues(previousEntityVersion, previousRevisionNum);
		}
		
		newEntityVersion.setIsNew(false);
		previousEntityVersion.setIsNew(false);

		return getAuditedPropertiesForEntityListValuesChanged(
				previousEntityVersion
				,newEntityVersion
				,entity
				,entityId
				,previousRevisionNum
				,newRevisionNum);
	}

	/***
	 * This method scans the entity audited
	 * and retrieves the manyTomany relation
	 * entities affected from the previous version 
	 * @param entity
	 * @param entityId
	 * @param previousRevisionNum
	 * @param newRevisionNum
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	private Map<String, AuditedPropertyDTO> getAuditedPropertiesForEntityListValuesChanged(			
			BaseO previousEntityVersion,
			BaseO newEntityVersion,
			Class<? extends BaseO> entity, 
			Long entityId, 
			int previousRevisionNum,
			int newRevisionNum) throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException {

		Map<String, AuditedPropertyDTO> auditedPropertes = null;

		if (newEntityVersion.isNew())
			newEntityVersion = loadEntityListValues(newEntityVersion, newRevisionNum);

		if (previousEntityVersion == null || newEntityVersion == null) return auditedPropertes;

		Field[] fields = previousEntityVersion.getClass().getDeclaredFields();
		for(var field : fields)
		{
			if (field.getType().equals(List.class))
			{
				field.setAccessible(true);
				@SuppressWarnings("unchecked")
				List<BaseO> previousValues = (List<BaseO>) field.get(previousEntityVersion);
				@SuppressWarnings("unchecked")
				List<BaseO> newValues = (List<BaseO>) field.get(newEntityVersion);

				if (previousValues == null) previousValues = new ArrayList<BaseO>();
				if (newValues == null) newValues = new ArrayList<BaseO>();

				// Get list type arguments - Child Entity
				ParameterizedType listType = (ParameterizedType) field.getGenericType();
				@SuppressWarnings("unchecked")
				Class<? extends BaseO> dataListType = (Class<? extends BaseO>) listType.getActualTypeArguments()[0];

				var previousVals = previousValues.stream()
				.map(i -> i.getId())
				.collect(Collectors.toList());		
				previousVals.sort(Comparator.naturalOrder());

				var newVals = newValues.stream()
				.map(i -> i.getId())
				.collect(Collectors.toList());
				newVals.sort(Comparator.naturalOrder());

				if ((newEntityVersion.isNew() && newVals != null && newVals.size() > 0)
						|| (previousVals.size() != newVals.size() || !previousVals.toString().equals(newVals.toString()))) {

					var previousValuesDTOs = previousValues.stream().map(v ->
					{
						try {
							return CustomModelMapper.mapModeltoDTO(v, CustomModelMapper.mapModelClassToDTOClass(dataListType));
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}		).collect(Collectors.toList());

					var newValuesDTOs = newValues.stream().map(v ->
					{
						try {
							return CustomModelMapper.mapModeltoDTO(v, CustomModelMapper.mapModelClassToDTOClass(dataListType));
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}		).collect(Collectors.toList());


					var dataListTypeDTO = CustomModelMapper.mapModelClassToDTOClass(dataListType);

					AuditedPropertyDTO auditedProp = new AuditedPropertyDTO();
					auditedProp.setFieldType(AuditedPropertyFieldType.Multiple);
					auditedProp.setDataListType(dataListTypeDTO);
					auditedProp.setPrevRevision(previousRevisionNum);
					auditedProp.setNewRevision(newRevisionNum);
					auditedProp.setPropName(field.getName());
					auditedProp.setPrevValues(previousValuesDTOs);	
					auditedProp.setNewValues(newValuesDTOs);	
					if (auditedPropertes == null) auditedPropertes = new HashMap<String, AuditedPropertyDTO>();
					auditedPropertes.put(field.getName(), auditedProp);
				}
			}
		}

		return auditedPropertes;
	}

	/***
	 * Temporary workaround method to load List<Proxy> where
	 * entities are not loaded by Hibernate Envers query
	 * @param entity
	 * @param id
	 * @param revision
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private BaseO loadEntityListValues(BaseO entity, int revision) throws IllegalArgumentException, IllegalAccessException {

		//perform db operations
		String joinTableAud = null;

		Field[] fields = entity.getClass().getDeclaredFields();
		for(var field : fields)
		{
			if (field.getType().equals(List.class))
			{
				field.setAccessible(true);
				ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
				if (manyToMany == null) continue;

				// proceed and load proxy list entities manually
				joinTableAud = field.getAnnotation(AuditJoinTable.class).name();

				var loadedList = loadList(entity.getId(), revision, joinTableAud);
				field.set(entity, loadedList);
			}
		}


		return entity;
	}

	/***
	 * Loads the corresponding Model entity from the database from the revision number given
	 * @param entityId
	 * @param revision
	 * @param joinTableAud
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private List<? extends BaseO> loadList(Long entityId, int revision, String joinTableAud) throws IllegalArgumentException, IllegalAccessException {

		List<? extends BaseO> result = new ArrayList<>();

		// go with the convention, middle join table to have foreign keys format like [entityname]_[id]
		String parentEntityId = joinTableAud.split("_")[0] + "_id";
		String childEntity = joinTableAud.split("_")[1];
		String childEntityId = childEntity + "_id";

		// Step 1
		// select all the revisions of the object starting from the first
		// use the ids to repeat/replay all events as to end up
		// with the items relation at the specific revision requested
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("rev", revision);
		sqlParams.addValue("id", entityId);
		var l = Repository.customNativeQuery("select " + childEntityId + ", revtype " 
				+ " from " + joinTableAud 
				+ " where rev <= :rev and " + parentEntityId + " = :id " 
				+ " order by rev asc", sqlParams);

		// Step 2
		// Replay all events and collect ids up to the point of requested revision
		if (l != null)
		{
			List<Long> ids = new ArrayList<Long>();

			l.beforeFirst();
			while (l.next())
			{
				Long idField = l.getLong(childEntityId);
				RevisionType revType = RevisionType.fromRepresentation(l.getByte("REVTYPE"));
				if (revType == RevisionType.ADD)
					ids.add(idField);
				else if (revType == RevisionType.DEL)
					ids.remove(idField);
			}

			if (ids.size() > 0) {
				Map<String, Object> params = new HashMap<String, Object>();
    			params.put("id", ids);

				// go with the normal class name with the first letter to be capital ex. Citation
				// if Audit table name has 's' at the end, for example articles_citations, strip last 's'
				childEntity = childEntity.toLowerCase();
				childEntity = childEntity.endsWith("s") ? childEntity.substring(0, childEntity.length() - 2) : childEntity;
				childEntity = childEntity.substring(0, 1).toUpperCase() + childEntity.substring(1);

				@SuppressWarnings("unchecked")
				List<? extends BaseO> models = (List<? extends BaseO>) Repository.query("from " + childEntity + " where id in (:id)", params);	
				if (models != null) {

					// Issue here is that for duplicate ids, that might exist in a List changed,
					// Hibernate query will bring distinct entities.
					// For example, for Ids [1,2,4,4,4,4] the query will return entity with id
					// 1,2, and 4, so 3 entities in total. But we want all 6 instances.
					// Solution, is to iterate the ids list and retrieve each entity from the
					// list retrived from the Hibernate query
					@SuppressWarnings("unchecked")
					List<? extends BaseO> listModel = (List<? extends BaseO>) ids.stream().map(i ->
					{
						return models.stream().filter(e -> e.getId() == i).findFirst().get();
					}).collect(Collectors.toList());

					result = listModel;
				}
			}
		}

		return result;
	}
}
