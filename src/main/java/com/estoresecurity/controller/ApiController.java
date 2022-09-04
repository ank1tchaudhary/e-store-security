package com.estoresecurity.controller;

import com.estoresecurity.domain.User;
import com.estoresecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @GetMapping
    public String publicResource(){
        return "Public Resource";
    }

    @PostMapping
    public String protectedResource(@PathVariable String value){
        return "Protected Resource : "+value;
    }


    @PostMapping("/save")
    public ResponseEntity<User> saveUser(@RequestBody User user){
        user.setPassword(encoder.encode(user.getPassword()));
       return new ResponseEntity<>(userRepository.save(user), HttpStatus.CREATED);
    }

    @PostMapping("/listUsers")
    public ResponseEntity<List<User>> listUser(){
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.CREATED);
    }
}
