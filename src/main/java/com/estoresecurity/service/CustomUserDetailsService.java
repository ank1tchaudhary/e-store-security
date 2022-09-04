package com.estoresecurity.service;

import com.estoresecurity.domain.User;
import com.estoresecurity.model.CustomUserDetails;
import com.estoresecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userFromDB = userRepository.findByUsername(username);
        User user = userFromDB.orElseThrow(() -> new UsernameNotFoundException("User does not exist in db"));
        return new CustomUserDetails(user);
    }
}
