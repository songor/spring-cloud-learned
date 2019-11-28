package com.spring.cloud.example.configserver;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 解決 spring cloud config server /encrypt Forbidden 错误
 */
@Configuration
public class BasicSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests().antMatchers("/actuator/**").permitAll()
                .antMatchers("/**").authenticated().and().httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("user").password("02795da9-8f86-4b22-aeec-7351a8bde965").roles("USER")
                .and().passwordEncoder(new CustomPasswordEncoder());
    }

}
