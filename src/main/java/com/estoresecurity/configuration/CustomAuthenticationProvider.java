package com.estoresecurity.configuration;

import com.estoresecurity.domain.User;
import com.estoresecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username= authentication.getName();
        String password= authentication.getCredentials().toString();
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent()){
            if(passwordEncoder.matches(password,user.get().getPassword())){
                List<GrantedAuthority> authorities= new ArrayList<>();
                user.get().getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getRoleName())));
                return new UsernamePasswordAuthenticationToken(username,password,authorities);
            }else {
                throw new RuntimeException("Invalid password for user : "+username);
            }

        }
        else {
            throw new RuntimeException("No Data found for user : "+username);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
