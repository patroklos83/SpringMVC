package com.patroclos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.patroclos.repository.IRepository;
import com.patroclos.utils.CustomModelMapper;

@Component
public class BaseService {
	
	@Autowired
	protected CustomModelMapper CustomModelMapper;
	
	@Autowired
	protected IRepository Repository;

}
