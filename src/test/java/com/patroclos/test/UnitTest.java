package com.patroclos.test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.patroclos.businessobject.ArticleBO;
import com.patroclos.facade.Facade;
import com.patroclos.model.Article;
import com.patroclos.repository.IRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@TestInstance(Lifecycle.PER_CLASS)
public class UnitTest {

	@Mock
	private Facade Facade;

	@InjectMocks
	private ArticleBO articleBO;

	@Mock
	private IRepository repository;

	@BeforeAll
	public void init(){
		repository = new com.patroclos.repository.Repository();
		articleBO = new ArticleBO();		
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("Unit Test load Article")
	public void getArticleProcess() throws Exception {   
		var articleMock = new Article();
		articleMock.setAuthor("Mock");

		Mockito.when(repository.findById(eq(Article.class), any(Long.class))).thenReturn(articleMock);
		
		Article article = (Article) articleBO.load(1L, Article.class);	
		
		assertNotNull(article);
		verify(repository, times(1)).findById(eq(Article.class), any(Long.class));
		
		assertEquals(articleMock.getAuthor(), article.getAuthor());
	}
}