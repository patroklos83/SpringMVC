package com.patroclos.configuration;

import javax.persistence.EntityManagerFactory;
import javax.servlet.Filter;
import javax.sql.DataSource;

import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patroclos.configuration.filter.SessionFilter;
import com.patroclos.envers.MyEnversPreUpdateEventListener;

@EnableWebMvc
@Configuration
@ImportResource("classpath:startup-servlet.xml")
public class ApplicationConfiguration implements WebMvcConfigurer {
	
	@Autowired
	public DataSource dataSource;
	
	@Bean
	@Primary
	public ObjectMapper objectMapper() {
		return new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	@Bean
	public EventListenerRegistry listenerRegistry(EntityManagerFactory entityManagerFactory) {
		ServiceRegistryImplementor serviceRegistry = entityManagerFactory.unwrap(SessionFactoryImpl.class)
				.getServiceRegistry();

		final EnversService enversService = serviceRegistry.getService(EnversService.class);
		EventListenerRegistry listenerRegistry = serviceRegistry.getService(EventListenerRegistry.class);

		listenerRegistry.setListeners(EventType.PRE_UPDATE, new MyEnversPreUpdateEventListener(enversService));
		return listenerRegistry;
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
		return modelMapper;
	}
	
	@Bean
	public Filter sessionFilter() {
	    return new SessionFilter();
	}

}