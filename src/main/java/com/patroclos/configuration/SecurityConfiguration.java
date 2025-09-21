package com.patroclos.configuration;

import java.util.Arrays;

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
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter.HeaderValue;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
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

	public final static String[] PUBLIC_URL_MATHCERS = { 
			"/", 
			"/assets/**",
			"/admin/h2",
			"/admin/h2/**",
			"/login",
			"/login.html",
			"/signup", 
			"/signupconfirm"
	};

	public static final String[] PUBLIC_API_MATHCERS = {};

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
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
    AuthenticationSuccessHandler authenticationSuccessHandler() {
		return new CustomAuthenticationSuccessHandler();
	}

    @Bean
    AuthenticationFailureHandler authenticationFailureHandler() {
		return new CustomAuthenticationFailureHandler();
	}

    @Bean
    LogoutSuccessHandler logoutSuccessHandler() {
		return new CustomLogoutSuccessHandler();
	}

    @Bean
    ClearSiteDataHeaderWriter clearSiteDataHandler() {
		return new ClearSiteDataHeaderWriter(Directive.ALL);
	}

    @Bean
    HeaderWriterLogoutHandler logoutHandler() {
		return new HeaderWriterLogoutHandler(clearSiteDataHandler());
	}

    @Bean("authenticationManager")
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

    @Bean
    DefaultWebSecurityExpressionHandler webSecurityExpressionHandler() {
		DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
		expressionHandler.setRoleHierarchy(roleHierarchy());
		return expressionHandler;
	}

    @Bean
    RoleHierarchy roleHierarchy() {
		// Role ADMIN automatically gives the user the privileges of both the STAFF and USER roles.
		// A user with the role MANAGER can only perform MANAGER and USER role actions.
		// Role ADMIN includes the role MANAGER, which in turn includes the role USER.
		// String hierarchy = "ROLE_ADMIN > ROLE_MANAGER \n ROLE_MANAGER > ROLE_USER > ROLE_ANONYMOUS";
		// Autocreate the string of hierarchy ordering
		String hierarchy = UserService.getRoleHierarchyString();
		return RoleHierarchyImpl.fromHierarchy(hierarchy);
	}

    @Bean
    /**
     * Important! When deployed to remote server, always use HTTPS
     * for the authentication/login to work
     * @param http
     * @return
     * @throws Exception
     */
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
		.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers(PUBLIC_URL_MATHCERS).permitAll()
				.requestMatchers(PUBLIC_API_MATHCERS).permitAll()
				.requestMatchers("/**", "/index**").hasRole("USER")
				.anyRequest().authenticated()
				)
		.cors(c -> c.configurationSource(corsConfigurationSource()))
		.formLogin(form -> form
				.loginPage("/login").permitAll()
				.loginProcessingUrl("/login")
				.defaultSuccessUrl("/index?page=dashboard").permitAll()
				.successHandler(authenticationSuccessHandler()).permitAll()
				.failureHandler(authenticationFailureHandler()).permitAll()
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

		http.csrf(csrf -> csrf.disable()); //disable csrf for access to H2 Console	
		http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()) //allow html pages inside html frames, only from same domain
				.httpStrictTransportSecurity(hsts -> hsts
						.includeSubDomains(true)
						.preload(true)
						.maxAgeInSeconds(31536000)
						)
				.defaultsDisabled()
				.cacheControl(cache -> cache.disable())
				.xssProtection(xssProtection -> xssProtection.headerValue(HeaderValue.ENABLED_MODE_BLOCK))
				.contentSecurityPolicy(policy -> policy
						.policyDirectives("form-action 'self' script-src 'self' style-src 'self'")
						.reportOnly()
						)
				.referrerPolicy(referrer -> referrer
						.policy(ReferrerPolicy.SAME_ORIGIN)
						)
				);
		
		return http.build();
	}

	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring()
				.requestMatchers(
						"/assets/**", 
						"/img/**", 
						"/css/**", 
						"/images/**",
						"/js/**", 
						"/webjars/**", 
						"/admin/h2/**")
				.requestMatchers(PUBLIC_API_MATHCERS);
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowCredentials(true);
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}