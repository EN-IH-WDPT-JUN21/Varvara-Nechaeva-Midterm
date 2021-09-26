package com.ironhack.midterm.security;

import com.ironhack.midterm.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private CustomUserDetailsService customUserDetailsService;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.httpBasic();
    http.csrf().disable();
    http.authorizeRequests()
        .mvcMatchers(HttpMethod.PATCH, "/account/**")
        .hasRole("ADMIN")
        .mvcMatchers(HttpMethod.PUT, "/account/new/**")
        .hasRole("ADMIN")
        .mvcMatchers(HttpMethod.GET, "/account/**")
        .hasRole("ACCOUNT_HOLDER")
        .mvcMatchers(HttpMethod.POST, "/account/**")
        .hasRole("ACCOUNT_HOLDER")
        .anyRequest()
        .permitAll();
  }
}
