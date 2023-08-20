package com.patroclos.repository;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import com.patroclos.utils.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Query;
import jakarta.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import com.patroclos.exception.SystemException;
import com.patroclos.model.ActivityLog;
import com.patroclos.model.ActivityLogDetail;
import com.patroclos.model.BaseO;
import com.patroclos.model.User;
import com.patroclos.service.IAuthenticationService;
import com.patroclos.utils.ProcessUtil;
import com.patroclos.repository.IRepository;
import com.patroclos.utils.CustomModelMapper;

@Component("Repository")
public class Repository implements IRepository {

	@jakarta.persistence.PersistenceContext 
	protected EntityManager em;
	@Autowired
	private NamedParameterJdbcTemplate JdbcTemplate;
	@Autowired
	private IAuthenticationService AuthenticationFacade;	
	@Autowired
	private CustomModelMapper CustomModelMapper;
	
	public EntityManager getEm() {
		return em;
	}

	public void save(BaseO o) throws Exception {
		save(o, false);
	}

	public BaseO save(BaseO o, boolean isChild) throws Exception {
		User user = AuthenticationFacade.getLoggedDbUser();

		String executingThreadName = getUniqueThreadProcessId();

		o.setLastUpdatedByProcessId(executingThreadName);	
		o.setLastUpdatedByuser(user);
		o.setLastUpdatedDate(Instant.now());

		if (o.isNew()) {			
			o.setIsDeleted(0);
			o.setCreatedByuser(user);
			if (user != null && user.getGroups() != null && user.getGroups().size() > 0)
				o.setCreatedByGroup(user.getGroups().get(0)); // set first group as primary user group
			o.setCreatedDate(Instant.now());
			em.persist(o);	
			if (!isChild)
				o = saveChildRelations(o, true, o);
		}
		else {			

			BaseO copyOfo = null;
			if (!isChild) {
				// Workaround to handle One To Many child entities 
				// on parent Entity edit-merge
				// Make an exact copy of the entity first with all
				// child entities including [new and existing]
				copyOfo = CustomModelMapper.mapModelToModel(o);
				o = prepareChildRelationsBeforeMerge(o, false);
			}

			o = em.merge(o);

			if (!isChild) {
				// Save Child One To Many relation entities after merge
				saveChildRelations(copyOfo, false, o);
			}
		}

		addActivityLogDetails(o, user, executingThreadName);

		return o;
	}
	
	@Override
	public void save(Object o) throws Exception {
		em.persist(o);
	}

	public <T> T findById(Class<T> t, Long id) {
		T result =  em.find(t, id);
		return result;
	}
	
	/***
	 * Overload moethod [query] and let user decide
	 * on the Flush Mode.
	 *  - AUTO: Flush changes in db before executing the query.
	 *  - COMMIT: Flush changes in db after the transaction is completed 
	 *    		and committed.
	 * @param <T>
	 * @param query
	 * @param t
	 * @param flushModeType
	 * @return
	 */
	public <T> List<?> query(String query, Class<T> t, FlushModeType flushModeType) {
		Query q = em.createQuery(query, t);
		q.setFlushMode(flushModeType);		
		
		return q.getResultList();
	}
	
	/***
	 * When calling createQuery on entityManager, 
	 * all dirty entities will be flushed first
	 * and then retrieved by the query
	 */
	public <T> List<?> query(String query, Class<T> t) {
		return query(query, t, FlushModeType.AUTO);
	}
	
	/***
	 * Overload moethod [query] and let user decide
	 * on the Flush Mode.
	 *  - AUTO: Flush changes in db before executing the query.
	 *  - COMMIT: Flush changes in db after the transaction is completed 
	 *    		and committed.
	 * @param <T>
	 * @param query
	 * @param t
	 * @param flushModeType
	 * @return
	 */
	public List<?> query(String hql, Map<String, Object> params, FlushModeType flushModeType) {
		Query q = em.createQuery(hql);		
		q.setFlushMode(flushModeType);
		if (params != null) {
			for (String param : params.keySet()) {
				q.setParameter(param, params.get(param));
			}	
		}
		
		List<?> result = q.getResultList();
		return result;
	}
	
	/***
	 * When calling createQuery on entityManager, 
	 * all dirty entities will be flushed first
	 * and then retrieved by the query
	 */
	public List<?> query(String hql, Map<String, Object> params) {
		return query(hql, params, FlushModeType.AUTO);
	}

	/***
	 * Workaround for the One to Many child relations,
	 * when parent entity is edited, try to remove the
	 * new child entities (not persisted yet) from the
	 * One to Many collection.
	 * Persisting of new Child entities to be done after
	 * the merging (em.merge) of the parent entity.
	 * 
	 * Hibernate and Hibernate envers has no out-of the box solution
	 * to edit child relations and keep an audit log with constraint 
	 * Integrity.This is due to the entity being detacHed because 
	 * of the Service layer conversion from-to DTO.
	 * @param parent
	 * @param parentIsNew
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public BaseO prepareChildRelationsBeforeMerge(BaseO parent, boolean parentIsNew) throws Exception {
		Field[] fields = parent.getClass().getDeclaredFields();
		for(var field : fields)
		{
			if (field.getType().equals(List.class))
			{
				field.setAccessible(true);
				OneToMany oneToMany = field.getAnnotation(OneToMany.class);
				if (oneToMany == null) continue;

				List<BaseO> childObjects = (List<BaseO>) field.get(parent);
				if (childObjects == null || childObjects.size() == 0) continue;

				List<BaseO> persistedChildObjects = new ArrayList<>();
				for (BaseO child : childObjects) {

					// find the parent bi-directional relation in child entity
					// If parent entity was not set [is null], proceed and
					// update the field with the parent entity
					Field[] fieldsOfChild = child.getClass().getDeclaredFields();
					boolean isChildNew = child.isNew();
					for(var f : fieldsOfChild)
					{
						f.setAccessible(true);
						ManyToOne manyToOne = f.getAnnotation(ManyToOne.class);
						if (manyToOne == null) continue;

						if (!f.getType().getTypeName().equals(parent.getClass().getTypeName())) continue;

						BaseO parentEntity = (BaseO) f.get(child);
						if (parentEntity == null)
							f.set(child, parent);
					}

					if (!isChildNew) {		
						persistedChildObjects.add(child);
					}
				}

				field.set(parent, persistedChildObjects);
			}
		}

		return parent;

	}

	/***
	 * Workaround to persist ManyToOne relation child
	 * entities. Issue here, is because entities are coming detached 
	 * from the upper layers [Service layer], we need a way to handle
	 * the save-new persist events.
	 * @param parent
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public BaseO saveChildRelations(BaseO parent, boolean parentIsNew, BaseO originalParent) throws Exception {		

		Field[] fields = parent.getClass().getDeclaredFields();
		for(var field : fields)
		{
			if (field.getType().equals(List.class))
			{
				field.setAccessible(true);
				OneToMany oneToMany = field.getAnnotation(OneToMany.class);
				if (oneToMany == null) continue;

				List<BaseO> childObjects = (List<BaseO>) field.get(parent);
				
				// If entity is edited, update parent to null for the Many To One child relations which 
				// were removed from this collection
				if (!parentIsNew) {
					// Get list type arguments - Child Entity
					ParameterizedType listType = (ParameterizedType) field.getGenericType();
					Class<? extends BaseO> dataListType = (Class<? extends BaseO>) listType.getActualTypeArguments()[0];
					removeChildsSetOrphans(parent, childObjects, dataListType);
				}

				if (childObjects == null || childObjects.size() == 0) continue;

				List<BaseO> persistedChildObjects = new ArrayList<>();
				for (BaseO child : childObjects) {

					// find the parent bi-directional relation in child entity
					// If parent entity was not set [is null], proceed and
					// update the field with the parent entity
					Field[] fieldsOfChild = child.getClass().getDeclaredFields();
					boolean isChildNew = child.isNew();
					for(var f : fieldsOfChild)
					{
						f.setAccessible(true);
						ManyToOne manyToOne = f.getAnnotation(ManyToOne.class);
						if (manyToOne == null) continue;

						if (!f.getType().getTypeName().equals(parent.getClass().getTypeName())) continue;

						BaseO parentEntity = (BaseO) f.get(child);
						if (parentEntity == null)
							f.set(child, originalParent);
					}

					if (!isChildNew) {
						save(child, true);		
						persistedChildObjects.add(child);
					}
					else {
						save(child, true);		
						persistedChildObjects.add(child);
					}
				}

				// remove the relation from the parent list because
				// child entities were already persisted from the code above
				if (parentIsNew)
					field.set(parent, null);
				else
					field.set(parent, persistedChildObjects);
			}
		}

		return parent;
	}

	@SuppressWarnings("unchecked")
	public void removeChildsSetOrphans(BaseO parent, List<BaseO> childsCurrent, Class<? extends BaseO> dataListType) throws Exception  {

		String foreignKeyColumnName = getParentForeingKey(parent, dataListType);
		if (foreignKeyColumnName == null) {
			throw new Exception("Repository: Cannot find foreignKey annotation on entity [%s] for parent [%s]".formatted(dataListType, parent.getClass().getSimpleName()));
		}
		
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValue("Id", parent.getId());
		if (childsCurrent != null && childsCurrent.size() > 0)
			sqlParams.addValue("newIds", childsCurrent.stream().map(c -> c.getId()).collect(Collectors.toList()));
		else
			sqlParams.addValue("newIds", new ArrayList<Long>());	
		
		Table tableAnnotation = dataListType.getAnnotation(Table.class);
		var l = customNativeQuery("SELECT id "
				+ " FROM " + tableAnnotation.name() + " "
				+ " where " + foreignKeyColumnName + " = :Id"
				+ " and id not in (:newIds)", sqlParams);

		List<Long> idsToDelete = new ArrayList<>();
		if (l != null)
		{
			l.beforeFirst();
			while (l.next())
			{
				Long idField = l.getLong("id");
				idsToDelete.add(idField);
			}
		}

		// No existing Child Many to One items to delete
		if (idsToDelete == null || idsToDelete.size() == 0) 
			return;

		String hql = "FROM " + dataListType.getSimpleName() + " A WHERE A.Id in (:id)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", idsToDelete);
		List<BaseO> itemsToDelete = (List<BaseO>) query(hql, params);

		for(BaseO childToDelete : itemsToDelete)
		{
			// find the parent bi-directional relation in child entity
			// If parent entity was not set [is null], proceed and
			// update the field with the parent entity
			Field[] fieldsOfChild = childToDelete.getClass().getDeclaredFields();
			for(var f : fieldsOfChild)
			{
				f.setAccessible(true);
				ManyToOne manyToOne = f.getAnnotation(ManyToOne.class);
				if (manyToOne == null) continue;

				if (!f.getType().getTypeName().equals(parent.getClass().getTypeName())) continue;

				BaseO parentEntity = (BaseO) f.get(childToDelete);
				f.set(childToDelete, null);
			}

			save(childToDelete, true);			
		}
	}
	
	/***
	 * Returns the ManyToOne parent object field name
	 * in the Child entity (foreign key column name)
	 * @param parent
	 * @param childDataType
	 * @return
	 */
	private String getParentForeingKey(BaseO parent, Class<? extends BaseO> childDataType) {
		
		Field[] fieldsOfChild = childDataType.getDeclaredFields();
		for(var f : fieldsOfChild)
		{
			f.setAccessible(true);
			ManyToOne manyToOne = f.getAnnotation(ManyToOne.class);
			MapsId mapsId = f.getAnnotation(MapsId.class);
			if (manyToOne == null) continue;// || mapsId == null) continue;

			if (!f.getType().getTypeName().equals(parent.getClass().getTypeName())) continue;

			JoinColumn joinColumn = f.getAnnotation(JoinColumn.class);
			if (joinColumn == null && mapsId != null)
				return mapsId.value();
			
			return joinColumn.name();
		}
		
		return null;
	}

	public SqlRowSet customNativeQuery(String query, MapSqlParameterSource args) {
		return JdbcTemplate.queryForRowSet(query, args);
	}

	private void addActivityLogDetails(BaseO o, User dbUser, String executingThreadName) throws Exception {
		if (!(o instanceof ActivityLog)) {
			ActivityLogDetail activityLogDetail = new ActivityLogDetail();
			activityLogDetail.setCreatedByuser(dbUser);
			activityLogDetail.setCreatedByGroup(dbUser.getGroups().get(0));
			activityLogDetail.setLastUpdatedByuser(dbUser);
			activityLogDetail.setEntity(o.getClass().getSimpleName());
			activityLogDetail.setEntityId(o.getId());
			activityLogDetail.setEntityRevision(o.getVersion());
			activityLogDetail.setLastUpdatedDate(Instant.now());
			activityLogDetail.setCreatedDate(Instant.now());
			activityLogDetail.setProcessId(executingThreadName);
			em.persist(activityLogDetail);
		}
	}

	private String getUniqueThreadProcessId() {
		String executingThreadName = Thread.currentThread().getName();
		if (!executingThreadName.startsWith(ProcessUtil.PROCESS_ID_PREFIX)) {
			throw new SystemException("Executing Process thread's invalid name [" + executingThreadName + "]");
		}
		return executingThreadName;
	}

}