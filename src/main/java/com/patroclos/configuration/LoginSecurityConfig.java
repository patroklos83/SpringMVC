package com.patroclos.configuration;

import org.springframework.security.core.userdetails.UserDetailsService;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.patroclos.security.CustomAuthenticationFailureHandler;
import com.patroclos.security.CustomAuthenticationSuccessHandler;
import com.patroclos.security.CustomLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
@ImportResource("/WEB-INF/startup-servlet.xml")
@Order(1000)
public class LoginSecurityConfig {//extends WebSecurityConfigurerAdapter {

	@Autowired
	public DataSource dataSource;
	@Autowired
	public UserDetailsService customUserDetailService;

//	@Bean
//	public PasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
//
//	@Autowired
//	public void configureGlobal(AuthenticationManagerBuilder authenticationMgr) throws Exception {
//		authenticationMgr.jdbcAuthentication().passwordEncoder(passwordEncoder()).dataSource(dataSource); 
//		authenticationMgr.userDetailsService(customUserDetailService);
//	}
//
////	@Bean("authenticationManager")
////	@Override
////	public AuthenticationManager authenticationManagerBean() throws Exception {
////		return super.authenticationManagerBean();
////	}
//
//	@Bean
//	public DefaultWebSecurityExpressionHandler webSecurityExpressionHandler() {
//		DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
//		expressionHandler.setRoleHierarchy(roleHierarchy());
//		return expressionHandler;
//	}
//
//	@Bean
//	public RoleHierarchy roleHierarchy() {
//		// Role ADMIN automatically gives the user the privileges of both the STAFF and USER roles.
//		// A user with the role STAFF can only perform STAFF and USER role actions.
//		// Role ADMIN includes the role STAFF, which in turn includes the role USER.
//		RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
//		String hierarchy = "ROLE_ADMIN > ROLE_USER \n ROLE_STAFF > ROLE_USER";
//		roleHierarchy.setHierarchy(hierarchy);
//		return roleHierarchy;
//	}
//
//	@Override
//	public void configure(WebSecurity web) throws Exception {
//		web
//		.ignoring()
//		.antMatchers("/admin/h2/**");
//	}
//
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {	
//
//		http.authorizeRequests()
//		.antMatchers("/","/admin/h2/**","/h2-console/**").permitAll()
//		.antMatchers("/index**").access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")//.hasRole("USER") //.access("hasRole('ROLE_USER')")
//		.and()
//		.formLogin().loginPage("/login").defaultSuccessUrl("/index?page=dashboard").successHandler(authenticationSuccessHandler())
//		.failureHandler(authenticationFailureHandler())
//		.and()
//		.logout().logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler()).invalidateHttpSession(true).deleteCookies("JSESSIONID")
//		.and()
//		.sessionManagement(session -> session
//				.maximumSessions(1) //set maximum concurrent user logins
//				.maxSessionsPreventsLogin(true) //second login will be prevented
//				)
//		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).sessionFixation().migrateSession(); //on authentication create a new session to avoid session fixation attack
//		//.and()
//		//.rememberMe().key("uniqueAndSecret").rememberMeParameter("remember-me").rememberMeCookieName("my-remember-me-cookie")
//		//.tokenRepository(persistentTokenRepository()).tokenValiditySeconds(60 * 60);
//
//		//http.csrf();
//		//http.headers().frameOptions()
//		//.sameOrigin() //allow html pages inside html frames, only from same domain
//		//.xssProtection().block(false); // Reflected XSS attack protection - https://wiki.owasp.org/index.php/Testing_for_Reflected_Cross_site_scripting_(OTG-INPVAL-001)
//
//		//http.headers().contentSecurityPolicy("form-action 'self' script-src 'self' https://baeldung.com; style-src 'self';");
//
//	}
//
//	public PersistentTokenRepository persistentTokenRepository(){
//		JdbcTokenRepositoryImpl tokenRepositoryImpl = new JdbcTokenRepositoryImpl();
//		tokenRepositoryImpl.setDataSource(dataSource);
//		return tokenRepositoryImpl;
//	}
//
//	@Bean
//	public AuthenticationSuccessHandler authenticationSuccessHandler() {
//		return new CustomAuthenticationSuccessHandler();
//	}
//
//	@Bean
//	public AuthenticationFailureHandler authenticationFailureHandler() {
//		return new CustomAuthenticationFailureHandler();
//	}
//
//	@Bean
//	public LogoutSuccessHandler logoutSuccessHandler() {
//		return new CustomLogoutSuccessHandler();
//	}

}
