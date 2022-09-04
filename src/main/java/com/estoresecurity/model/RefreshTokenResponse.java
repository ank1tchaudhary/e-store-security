package com.estoresecurity.model;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshTokenResponse {

    private String accessToken;

}
