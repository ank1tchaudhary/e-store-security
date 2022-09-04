package com.estoresecurity.model;

import com.estoresecurity.domain.Role;
import com.estoresecurity.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
public class CustomUserDetails implements UserDetails {


    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean isActive;
    private boolean isLoggedIn;
    private Collection<Role> roles=new ArrayList<>();

    public CustomUserDetails(User user){
        this.firstName = user.getFirstName();
        this.lastName=user.getLastName();
        this.username=user.getUsername();
        this.password=user.getPassword();
        this.isActive = user.isActive();
        this.isLoggedIn=user.isLoggedIn();
        this.roles=user.getRoles();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getRoleName())).collect(Collectors.toList());

    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isActive();
    }

}
