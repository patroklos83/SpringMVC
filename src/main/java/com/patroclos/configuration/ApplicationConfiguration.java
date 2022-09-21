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
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patroclos.configuration.filter.SessionFilter;
import com.patroclos.envers.MyEnversPreUpdateEventListener;

@EnableWebMvc
@Configuration
@ImportResource("classpath*:/WEB-INF/startup-servlet.xml")
//@EnableJdbcHttpSession
//@PropertySource("classpath:application.properties")
public class ApplicationConfiguration implements WebMvcConfigurer {
	
	@Autowired
	public DataSource dataSource;
	
//	@Bean
//	public EmbeddedDatabase dataSource() {
//		return new EmbeddedDatabaseBuilder() 
//				.setType(EmbeddedDatabaseType.H2)
//				.addScript("org/springframework/session/jdbc/schema-h2.sql")
//				.build();
//	}
//
//	@Bean
//	public PlatformTransactionManager transactionManager(DataSource dataSource) {
//		return new DataSourceTransactionManager(dataSource); 
//	}

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

//	@Override
//	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
//		argumentResolvers.add(new CustomHandlerMethodArgumentResolver());
//		argumentResolvers.add(new CustomRequestParamArgumentResolver());
//	}

	//
	// @Bean
	// public ViewResolver jspViewResolver() {
	// InternalResourceViewResolver bean = new InternalResourceViewResolver();
	// bean.setPrefix("/WEB-INF/views/");
	// bean.setSuffix(".jsp");
	// return bean;
	// }
	//
	// @Bean
	// public SpringResourceTemplateResolver thymeleafTemplateResolver() {
	// SpringResourceTemplateResolver templateResolver
	// = new SpringResourceTemplateResolver();
	// templateResolver.setPrefix("/WEB-INF/views/");
	// templateResolver.setSuffix(".html");
	// templateResolver.setTemplateMode("HTML5");
	// return templateResolver;
	// }
	//
	// @Bean
	// private ITemplateResolver htmlTemplateResolver() {
	// SpringResourceTemplateResolver resolver = new
	// SpringResourceTemplateResolver();
	// resolver.setApplicationContext(applicationContext);
	// resolver.setPrefix("/WEB-INF/views/");
	// resolver.setCacheable(false);
	// resolver.setTemplateMode(TemplateMode.HTML);
	// return resolver;
	// }

	//
	// @Override
	// public void addResourceHandlers(ResourceHandlerRegistry registry) {
	// registry.addResourceHandler("/assets/**")
	// .addResourceLocations("/resources/assets/");
	//// .setCachePeriod(31556926)
	//// .resourceChain(true)
	//// .addResolver(new PathResourceResolver());
	//
	// // Register resource handler for CSS and JS
	// //
	// registry.addResourceHandler("/resources/**").addResourceLocations("classpath:/statics/",
	// "D:/statics/")
	// // .setCacheControl(CacheControl.maxAge(2, TimeUnit.HOURS).cachePublic());
	//
	// // Register resource handler for images
	// //
	// registry.addResourceHandler("/assets/**").addResourceLocations("/WEB-INF/assets/")
	// // .setCacheControl(CacheControl.maxAge(2, TimeUnit.HOURS).cachePublic());
	// }

}