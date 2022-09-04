package com.estoresecurity.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.estoresecurity.repository.UserRepository;
import com.estoresecurity.utility.JWTTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@Component
public class CustomAuthorizationFilter extends OncePerRequestFilter {


    @Autowired
    private JWTTokenUtility utility;

    @Autowired
    private UserRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getServletPath().equals("/auth/login") || request.getServletPath().equals("/auth/refreshToken")){
            filterChain.doFilter(request,response);
        }else {


            if (!hasAuthorizationBearer(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = getAccessToken(request);

            if (!utility.validateToken(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            setAuthenticationContext(token, request);
            filterChain.doFilter(request, response);
        }
    }

    private boolean hasAuthorizationBearer(HttpServletRequest request) {
        String authorizationHeader= request.getHeader(HttpHeaders.AUTHORIZATION);
        return authorizationHeader != null && authorizationHeader.startsWith("Bearer ");
    }

    private String getAccessToken(HttpServletRequest request) {
        String authorizationHeader= request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            return authorizationHeader.substring("Bearer ".length());
        }
       else {
           throw new RuntimeException("Token not present in header");
        }
    }

    private void setAuthenticationContext(String token, HttpServletRequest request) {
        DecodedJWT decodedJWT = utility.getDecodedJWT(token);

        String username= decodedJWT.getSubject();
        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        Arrays.stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));

        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }




}
