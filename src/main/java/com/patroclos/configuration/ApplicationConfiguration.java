package com.patroclos.configuration;

import jakarta.persistence.EntityManagerFactory;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;

import jakarta.servlet.Filter;
import javax.sql.DataSource;

import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.patroclos.configuration.filter.SessionFilter;
import com.patroclos.envers.MyEnversPostCollectionRecreateEventListener;
import com.patroclos.envers.MyEnversPostInsertEventListener;
import com.patroclos.envers.MyEnversPostUpdateEventListener;
import com.patroclos.envers.MyEnversPreCollectionRemoveEventListener;
import com.patroclos.envers.MyEnversPreCollectionUpdateEventListener;
import com.patroclos.envers.MyEnversPreUpdateEventListener;

@EnableWebMvc
@Configuration
@ImportResource("classpath*:startup-servlet.xml")
public class ApplicationConfiguration implements WebMvcConfigurer {

	// Spring + Thymeleaf need this
	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	public DataSource dataSource;

	// Spring + Thymeleaf
	@Bean
	public SpringResourceTemplateResolver templateResolver() {
		SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
		templateResolver.setApplicationContext(this.applicationContext);
		templateResolver.setPrefix("/WEB-INF/views/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCacheable(true);
		return templateResolver;
	}

	// Spring + Thymeleaf
	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver());
		templateEngine.setEnableSpringELCompiler(true);
		templateEngine.addDialect(new LayoutDialect()); 
		return templateEngine;
	}

	// Spring + Thymeleaf
	// Configure Thymeleaf View Resolver
	@Bean
	public ThymeleafViewResolver viewResolver() {
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		viewResolver.setTemplateEngine(templateEngine());
		return viewResolver;
	}

	@Bean
	@Primary
	public ObjectMapper objectMapper() {
		ObjectMapper om = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
		om.registerModule(new JavaTimeModule());
		return om;
	}

	@Bean
	public EventListenerRegistry listenerRegistry(EntityManagerFactory entityManagerFactory) {
		ServiceRegistryImplementor serviceRegistry = entityManagerFactory.unwrap(SessionFactoryImpl.class)
				.getServiceRegistry();

		final EnversService enversService = serviceRegistry.getService(EnversService.class);
		EventListenerRegistry listenerRegistry = serviceRegistry.getService(EventListenerRegistry.class);

		listenerRegistry.setListeners(EventType.PRE_UPDATE, new MyEnversPreUpdateEventListener(enversService));
		listenerRegistry.setListeners(EventType.POST_INSERT, new MyEnversPostInsertEventListener(enversService));
		listenerRegistry.setListeners(EventType.PRE_COLLECTION_UPDATE, new MyEnversPreCollectionUpdateEventListener(enversService));
		listenerRegistry.setListeners(EventType.PRE_COLLECTION_REMOVE, new MyEnversPreCollectionRemoveEventListener(enversService));
		listenerRegistry.setListeners(EventType.POST_COLLECTION_RECREATE, new MyEnversPostCollectionRecreateEventListener(enversService));
		listenerRegistry.setListeners(EventType.POST_UPDATE, new MyEnversPostUpdateEventListener(enversService));		
		
		return listenerRegistry;
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration()
		.setMatchingStrategy(MatchingStrategies.LOOSE)
		.setCollectionsMergeEnabled(false);
		return modelMapper;
	}

	@Bean
	public Filter sessionFilter() {
		return new SessionFilter();
	}

}