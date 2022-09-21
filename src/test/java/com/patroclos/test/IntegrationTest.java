package com.patroclos.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import javax.servlet.ServletContext;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.patroclos.businessobject.ArticleBO;
import com.patroclos.dto.ArticleDTO;
import com.patroclos.facade.Facade;
import com.patroclos.model.Article;
import com.patroclos.repository.IRepository;

import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("Test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfigTest.class })
@WebAppConfiguration("WebContent")
@TestInstance(Lifecycle.PER_CLASS)
public class IntegrationTest {
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	 
	private MockMvc mockMvc;
	
	@InjectMocks
	private Facade facade;

	@InjectMocks
	private ArticleBO articleBO;

	@Mock
	private IRepository repository;
	
	@BeforeAll
	public void init(){		
		ServletContext servletContext = webApplicationContext.getServletContext();
	    assertNotNull(servletContext);
	    assertTrue(servletContext instanceof MockServletContext);	    
	    facade = (Facade) webApplicationContext.getBean("facade");
	    articleBO = new ArticleBO();
		repository = new com.patroclos.repository.Repository();
		articleBO = new ArticleBO();		
		MockitoAnnotations.openMocks(this);
	}
	
	@BeforeEach
	public void setup() throws Exception {		
	    this.mockMvc = MockMvcBuilders
	    		.webAppContextSetup(this.webApplicationContext)
	    		.defaultRequest(get("/").with(user("user").roles("ADMIN")))
	    		.apply(springSecurity())
	    		.build();
	}
	
	@Test
	@WithUserDetails(value="user", userDetailsServiceBeanName="CustomUserDetailsService")
	public void testLoadArticle() throws Exception {	     
	    var article = facade.load(1, ArticleDTO.class);  
	    assertNotNull(article);
	}
	
	@Test
	@WithUserDetails(value="user", userDetailsServiceBeanName="CustomUserDetailsService")
	public void testArticleController() throws Exception {				
		MvcResult mvcResult = this.mockMvc.perform(get("/article/1")).andDo(print())
				.andExpect(status().isOk())
				.andExpect(view().name("/pages/article")).andReturn();
		
		assertEquals("/pages/article", mvcResult.getModelAndView().getViewName());
	}
    
}