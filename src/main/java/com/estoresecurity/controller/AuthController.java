package com.estoresecurity.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.estoresecurity.configuration.CustomAuthenticationProvider;
import com.estoresecurity.domain.User;
import com.estoresecurity.exception.NoDataFoundException;
import com.estoresecurity.model.CustomUserDetails;
import com.estoresecurity.model.LoginRequest;
import com.estoresecurity.model.LoginResponse;
import com.estoresecurity.model.RefreshTokenResponse;
import com.estoresecurity.repository.UserRepository;
import com.estoresecurity.utility.JWTTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
        UsernamePasswordAuthenticationToken authenticationProvider = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        try {
            Authentication authentication = authenticationManager.authenticate(authenticationProvider);
            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
            SecurityContextHolder.getContext().setAuthentication(authentication);
            Optional<User> dbUser = userRepository.findByUsername(request.getUsername());
            if(dbUser.isPresent()) {
                dbUser.get().setLoggedIn(true);
                User updatedUser = userRepository.save(dbUser.get());

                String access_token = jwtTokenUtility.generateJWTToken(updatedUser);
                String refresh_token = jwtTokenUtility.generateRefreshJWTToken(updatedUser);

                LoginResponse response = LoginResponse.builder().username(updatedUser.getUsername()).roles(updatedUser.getRoles()).accessToken(access_token).refreshToken(refresh_token).build();
                return ResponseEntity.ok().body(response);
            }else {
                throw new NoDataFoundException("User does not exist in db.");
            }
        } catch (RuntimeException e) {
            throw new NoDataFoundException("User does not exist in db.");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String token = authorizationHeader.substring("Bearer ".length());
                boolean isTokenValidated = jwtTokenUtility.validateToken(token);
                if (isTokenValidated) {
                    DecodedJWT decodedJWT = jwtTokenUtility.getDecodedJWT(token);
                    String username = decodedJWT.getSubject();
                    Optional<User> user = userRepository.findByUsername(username);
                    if (user.isPresent()) {
                        user.get().setLoggedIn(false);
                        User loggedOutUser = userRepository.save(user.get());
                        return ResponseEntity.ok().body("User " + loggedOutUser.getFirstName() + " " + loggedOutUser.getLastName() + " logged out successfully");
                    }
                } else {
                    throw new RuntimeException("Invalid User");
                }

            } catch (RuntimeException e) {
                throw new RuntimeException(e.getMessage());
            }

        }
        return ResponseEntity.badRequest().body("Header/Token missing");
    }


    @PostMapping("/refreshToken")
    public ResponseEntity<RefreshTokenResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String token = authorizationHeader.substring("Bearer ".length());
                boolean isTokenValidated = jwtTokenUtility.validateToken(token);
                if (isTokenValidated) {
                    DecodedJWT decodedJWT = jwtTokenUtility.getDecodedJWT(token);
                    String username = decodedJWT.getSubject();
                    Optional<User> user = userRepository.findByUsername(username);
                    if (user.isPresent()) {
                        String refreshToken = jwtTokenUtility.generateRefreshJWTTokenExtended(user.get());

                        return ResponseEntity.ok().body(RefreshTokenResponse.builder().accessToken(refreshToken).build());
                    }
                } else {
                    throw new RuntimeException("Invalid User");
                }

            } catch (RuntimeException e) {
                throw new RuntimeException(e.getMessage());
            }

        }
        return ResponseEntity.badRequest().build();
    }


}
