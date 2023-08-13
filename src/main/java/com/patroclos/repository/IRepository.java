package com.patroclos.repository;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;


import com.patroclos.model.BaseO;

import jakarta.persistence.FlushModeType;

public interface IRepository {

	public void save(BaseO o) throws Exception;
	
	public void save(Object o) throws Exception;

	public <T> T findById(Class<T> t, Long id);
	
	public <T> List<?> query(String query, Class<T> t);
	
	public <T> List<?> query(String query, Class<T> t, FlushModeType flushModeType);
	
	public List<?> query(String hql, Map<String, Object> params);
	
	public List<?> query(String hql, Map<String, Object> params, FlushModeType flushModeType);

	public SqlRowSet customNativeQuery(String query, MapSqlParameterSource args);

	public BaseO saveChildRelations(BaseO baseO, boolean b, BaseO baseO2) throws Exception;
}
