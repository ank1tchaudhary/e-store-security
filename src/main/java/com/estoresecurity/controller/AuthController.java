package com.estoresecurity.controller;

import com.estoresecurity.domain.User;
import com.estoresecurity.exception.NoDataFoundException;
import com.estoresecurity.model.CustomUserDetails;
import com.estoresecurity.model.LoginRequest;
import com.estoresecurity.model.LoginResponse;
import com.estoresecurity.repository.UserRepository;
import com.estoresecurity.utility.JWTTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTTokenUtility jwtTokenUtility;

    @Autowired
    private UserRepository userRepository;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword());
        Authentication authenticatedUser = authenticationManager.authenticate(authenticationToken);
        CustomUserDetails user = (CustomUserDetails) authenticatedUser.getPrincipal();
        SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
        User dbUser = userRepository.findByUsername(user.getUsername()).get();
        String token = jwtTokenUtility.generateJWTToken(dbUser);

        LoginResponse response = LoginResponse.builder().username(dbUser.getUsername()).roles(dbUser.getRoles()).token(token).build();
        return ResponseEntity.ok().body(response);
    }




    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String token = authorizationHeader.substring("Bearer ".length());
                SecurityContextHolder.clearContext();

                return ResponseEntity.ok().body("user logged out successfully");
            } catch (RuntimeException e) {
                throw new RuntimeException(e.getMessage());
            }

        } else {
            throw new NoDataFoundException(HttpHeaders.AUTHORIZATION+" header token missing");
        }

    }

}
