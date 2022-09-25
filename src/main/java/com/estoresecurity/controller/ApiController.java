package com.estoresecurity.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController {


   // @PreAuthorize("hasRole('USER')")
    @GetMapping
    public String publicResource(OAuth2AuthenticationToken token){
      log.info("Token data : {}",token);
        return "Public Resource";
    }

}
