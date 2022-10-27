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
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.patroclos.security.CustomAuthenticationFailureHandler;
import com.patroclos.security.CustomAuthenticationSuccessHandler;
import com.patroclos.security.CustomLogoutSuccessHandler;

@EnableWebSecurity
@Configuration
@ImportResource("classpath*:/WEB-INF/startup-servlet.xml")
public class SecurityConfiguration {

	@Autowired
	public DataSource dataSource;
	@Autowired
	public UserDetailsService customUserDetailService;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

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
		// A user with the role STAFF can only perform STAFF and USER role actions.
		// Role ADMIN includes the role STAFF, which in turn includes the role USER.
		RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
		String hierarchy = "ROLE_ADMIN > ROLE_USER \n ROLE_STAFF > ROLE_USER";
		roleHierarchy.setHierarchy(hierarchy);
		return roleHierarchy;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.authorizeRequests()
		.antMatchers("/login", "/signup", "/signupconfirm").permitAll()
		.antMatchers("/**", "/index**").access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
		.and()
		.formLogin().loginPage("/login").defaultSuccessUrl("/index?page=dashboard").successHandler(authenticationSuccessHandler())
		.failureHandler(authenticationFailureHandler())
		.and()
		.logout().logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler()).invalidateHttpSession(true).deleteCookies("JSESSIONID")
		.and()
		.sessionManagement(session -> session
				.maximumSessions(1) //set maximum concurrent user logins
				.maxSessionsPreventsLogin(true) //second login will be prevented
				)
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).sessionFixation().migrateSession(); //on authentication create a new session to avoid session fixation attack
 
		http.csrf(); // if webapp is not running in localhost, for the csrf to work, must use https
		
		http.headers()
		.frameOptions()
		.sameOrigin() //allow html pages inside html frames, only from same domain
		.xssProtection().block(false); // Reflected XSS attack protection - https://wiki.owasp.org/index.php/Testing_for_Reflected_Cross_site_scripting_(OTG-INPVAL-001)

		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**", "/admin/h2/**");
	}
}