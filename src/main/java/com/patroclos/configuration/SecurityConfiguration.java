package com.patroclos.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive;
import com.patroclos.security.CustomAuthenticationFailureHandler;
import com.patroclos.security.CustomAuthenticationSuccessHandler;
import com.patroclos.security.CustomLogoutSuccessHandler;

import com.patroclos.service.*;

@EnableWebSecurity
@Configuration
@ImportResource("classpath*:startup-servlet.xml")
public class SecurityConfiguration {

	@Autowired
	public DataSource dataSource;
	@Autowired
	public UserDetailsService customUserDetailService;
	@Autowired
	public UserService UserService;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/***
	 *  'RememberMe' cookie contains the username, expiration time and MD5 hash containing 
	 *  the password. Because it contains a hash of the password, this solution is potentially vulnerable 
	 *  if the cookie is captured.
	 *  Let's use PersistentTokenBasedRememberMeServices instead to store the persisted login information 
	 *  in a database table between sessions.
	 *  
	 */
	public PersistentTokenRepository persistentTokenRepository(){
		JdbcTokenRepositoryImpl tokenRepositoryImpl = new JdbcTokenRepositoryImpl();
		tokenRepositoryImpl.setDataSource(dataSource);
		return tokenRepositoryImpl;
	}

	@Bean
	public AuthenticationSuccessHandler authenticationSuccessHandler() {
		return new CustomAuthenticationSuccessHandler();
	}

	@Bean
	public AuthenticationFailureHandler authenticationFailureHandler() {
		return new CustomAuthenticationFailureHandler();
	}

	@Bean
	public LogoutSuccessHandler logoutSuccessHandler() {
		return new CustomLogoutSuccessHandler();
	}

	@Bean
	public ClearSiteDataHeaderWriter clearSiteDataHandler() {
		return new ClearSiteDataHeaderWriter(Directive.ALL);
	}

	@Bean
	public HeaderWriterLogoutHandler logoutHandler() {
		return new HeaderWriterLogoutHandler(clearSiteDataHandler());
	}

	@Bean("authenticationManager")
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public DefaultWebSecurityExpressionHandler webSecurityExpressionHandler() {
		DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
		expressionHandler.setRoleHierarchy(roleHierarchy());
		return expressionHandler;
	}

	@Bean
	public RoleHierarchy roleHierarchy() {
		// Role ADMIN automatically gives the user the privileges of both the STAFF and USER roles.
		// A user with the role MANAGER can only perform MANAGER and USER role actions.
		// Role ADMIN includes the role MANAGER, which in turn includes the role USER.
		RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
		// String hierarchy = "ROLE_ADMIN > ROLE_MANAGER \n ROLE_MANAGER > ROLE_USER > ROLE_ANONYMOUS";
		// Autocreate the string of hierarchy ordering
		String hierarchy = UserService.getRoleHierarchyString();
		roleHierarchy.setHierarchy(hierarchy);
		return roleHierarchy;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
		.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers("/", "/admin/h2","/admin/h2/**","/login", "/signup", "/signupconfirm").permitAll()
				.requestMatchers("/**", "/index**").hasRole("USER")
				.anyRequest().authenticated()
				)
		.formLogin(form -> form
				.loginPage("/login")
				.defaultSuccessUrl("/index?page=dashboard").successHandler(authenticationSuccessHandler())
				.failureHandler(authenticationFailureHandler())
				.permitAll()
				)
		.logout((logout) -> logout
				.logoutUrl("/logout")
				.clearAuthentication(true)
				.deleteCookies("JSESSIONID").deleteCookies("remember-me")
				.invalidateHttpSession(true)
				.addLogoutHandler(new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(Directive.COOKIES)))
				.logoutSuccessHandler(logoutSuccessHandler())
				)
		.sessionManagement(session -> session
				.sessionConcurrency(c -> c.maximumSessions(1).maxSessionsPreventsLogin(true))
				.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).sessionFixation().newSession()
				)
		.rememberMe(remember -> remember
				.key("uniqueAndSecret")
				.alwaysRemember(true)
				.tokenRepository(persistentTokenRepository()).tokenValiditySeconds(60 * 60)
				);
	
		//http.csrf(csrf -> csrf.disable()); //disable csrf for access to H2 Console

		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers("/images/**", "/js/**", "/webjars/**", "/admin/h2/**");
	}
}