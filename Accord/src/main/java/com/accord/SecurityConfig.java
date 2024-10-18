package com.accord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.accord.service.UserService;

import jakarta.servlet.DispatcherType;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
	private UserService userService;
    
	@Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        /*http
            .authorizeRequests()
                .requestMatchers("/static/**", "/images/**").permitAll()
                .requestMatchers("/register", "/login", "/forgotPassword_page").permitAll()
                .anyRequest().authenticated()
            .and()
            .formLogin()
                .loginPage("/login").permitAll()
                .failureUrl("/login?error=true")
            .and()
            .logout().permitAll();
        
        // For H2 Console
        http.csrf().disable();
        http.headers().frameOptions().disable();

        return http.build();*/
        
        http.csrf(csrf -> csrf
                .disable())
                .authorizeRequests(requests -> requests
                        .requestMatchers("/register", "/login", "/forgotPassword_page").permitAll())
                .formLogin(login -> login
                        //.usernameParameter("email")
                        //.passwordParameter("password")
                        .loginPage("/login")
                        .failureUrl("/login?error=true")
                        .defaultSuccessUrl("/dashboard_user"))
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID"));
        return http.build();
        	/*.authorizeHttpRequests(authorize -> authorize
        			.requestMatchers("/register", "/login", "/forgotPassword_page").permitAll());*/
		/*http.formLogin(form -> form
	            .loginPage("/login").permitAll()
	            .successForwardUrl("/dashboard_user")
	            .defaultSuccessUrl("/dashboard_user", true)
	            .failureUrl("/login?error=true"))
	        .logout(logout -> logout
	            .logoutSuccessUrl("/login")
	            .deleteCookies("JSESSIONID"));
	        //.exceptionHandling(exception -> exception
	            //.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login?loginRequired=true")));
        return http.build();*/
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(
            final AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
