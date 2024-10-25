package com.accord.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.accord.config.AuthSuccessHandlerImpl;
import com.accord.config.AuthFailHandlerImpl;

@Configuration
public class SecurityConfig {
    
    @Autowired
	private AuthSuccessHandlerImpl authSuccessHandlerImpl;
    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    @Lazy
    private AuthFailHandlerImpl authFailHandlerImpl;

	@Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())
            .authorizeRequests(req -> req
                .requestMatchers("/css/**", "/images/**").permitAll()
                .requestMatchers("/dashboard_user", "/manage-profile").hasRole("USER")
                .requestMatchers("/dashboard_user", "/manage-profile").authenticated()
                .requestMatchers("/dashboard_admin").hasRole("ADMIN")
                .requestMatchers("/", "/login", "/register", "/register_page_admin", "/forgotPassword_page").permitAll())
            .formLogin(form -> form
                .loginPage("/").permitAll()
                .loginProcessingUrl("/").permitAll()
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authFailHandlerImpl))
            .logout(logout -> logout
                .logoutSuccessUrl("/").permitAll()
                .deleteCookies("JSESSIONID").permitAll());
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
    }

    @Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}
}
