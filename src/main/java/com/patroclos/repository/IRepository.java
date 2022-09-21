package com.patroclos.repository;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.patroclos.dto.AuditedProperty;
import com.patroclos.model.BaseO;

public interface IRepository {

	public void save(BaseO o) throws Exception;
	
	public void save(Object o) throws Exception;

	public <T> T findById(Class<T> t, Long id);
	
	public <T> List<?> customQuery(String query, Class<T> t);
	
	public List<?> query(String hql, Map<String, Object> params);

	public SqlRowSet customNativeQuery(String query, MapSqlParameterSource args);
}
