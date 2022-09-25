package com.estoresecurity.configuration;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;

@Configuration
@EnableWebSecurity
@Slf4j
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfiguration{


    @Bean
    public OAuth2LoginConfigurer<HttpSecurity> loginConfigurer(HttpSecurity http) throws Exception{
        return http
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .oauth2Login();
    }


}
