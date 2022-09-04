package com.estoresecurity.model;

import com.estoresecurity.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {

    private String username;
    private Collection<Role> roles;
    private String accessToken;
    private String refreshToken;
}
