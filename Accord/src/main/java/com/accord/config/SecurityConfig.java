package com.accord.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {
    
    @Autowired
	private AuthenticationSuccessHandler authenticationSuccessHandler;

	@Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())
            .authorizeRequests(req -> req
                .requestMatchers("/css/**", "/images/**").permitAll()
                .requestMatchers("/dashboard_user").hasRole("USER")
                .requestMatchers("/dashboard_admin").hasRole("ADMIN")
                .requestMatchers("/").permitAll())
            .formLogin(form -> form
                .loginPage("/").permitAll()
                .loginProcessingUrl("/").permitAll()
                .successHandler(authenticationSuccessHandler))
            .logout(logout -> logout
                .logoutSuccessUrl("/").permitAll()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID").permitAll())
            .headers(headers -> headers
                .cacheControl().disable());
            
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
