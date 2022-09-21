package com.patroclos.repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.patroclos.dto.UserDTO;
import com.patroclos.exception.SystemException;
import com.patroclos.model.ActivityLog;
import com.patroclos.model.ActivityLogDetail;
import com.patroclos.model.BaseO;
import com.patroclos.model.User;
import com.patroclos.service.IAuthenticationService;
import com.patroclos.utils.*;

@Component("Repository")
public class Repository implements IRepository{

	@PersistenceContext 
	protected EntityManager em;
	@Autowired
	private NamedParameterJdbcTemplate JdbcTemplate;
	@Autowired
	private IAuthenticationService AuthenticationFacade;

	public void save(BaseO o) throws Exception {
		User user = AuthenticationFacade.getLoggedDbUser();

		String executingThreadName = getUniqueThreadProcessId();

		o.setLastUpdatedByProcessId(executingThreadName);	
		o.setLastUpdatedByuser(user);
		o.setLastUpdatedDate(Instant.now());
		if (o.isNew()) {
			o.setIsDeleted(0);
			o.setCreatedByuser(user);
			o.setCreatedDate(Instant.now());
			em.persist(o);
		}
		else {
			o = em.merge(o);
		}

		addActivityLogDetails(o, user, executingThreadName);
	}

	public void save(Object o) throws Exception {
		em.persist(o);
	}

	public <T> T findById(Class<T> t, Long id) {
		T result =  em.find(t, id);
		return result;
	}

	public <T> List<?> customQuery(String query, Class<T> t) {
		Query q = em.createQuery(query, t);
		return q.getResultList();
	}

	public List<?> query(String hql, Map<String, Object> params) {
		Query q = em.createQuery(hql);		
		if (params != null) {
			for (String param : params.keySet()) {
				q.setParameter(param, params.get(param));
			}	
		}
		return q.getResultList();
	}

	public SqlRowSet customNativeQuery(String query, MapSqlParameterSource args) {
		return JdbcTemplate.queryForRowSet(query, args);
	}

	private void addActivityLogDetails(BaseO o, com.patroclos.model.User dbUser, String executingThreadName) throws Exception {
		if (!(o instanceof ActivityLog)) {
			ActivityLogDetail activityLogDetail = new ActivityLogDetail();
			activityLogDetail.setCreatedByuser(dbUser);
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