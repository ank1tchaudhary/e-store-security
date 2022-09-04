package com.estoresecurity.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.estoresecurity.domain.Role;
import com.estoresecurity.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JWTTokenUtility {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.issuer}")
    private String jwtIssuer;

    @Value("${app.jwt.expiration}")
    private Long jwtExpiration;

    @Value("${app.jwt.refreshTimeout}")
    private Long jwtRefreshTime;

    @Value("${app.jwt.refreshExtendedTimeout}")
    private Long jwtRefreshTokenExtended;


    public String generateJWTToken(User user) {

        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withAudience("BackOfficeClientApp")
                .withSubject(user.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpiration))
                .withIssuer(jwtIssuer)
                .withClaim("roles", user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()))
                .sign(algorithm);
    }


    public String generateRefreshJWTToken(User user) {

        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withAudience("BackOfficeClientApp")
                .withSubject(user.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtRefreshTime))
                .withIssuer(jwtIssuer)
                .withClaim("roles", user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()))
                .sign(algorithm);
    }


    public String generateRefreshJWTTokenExtended(User user) {

        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withAudience("BackOfficeClientApp")
                .withSubject(user.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtRefreshTokenExtended))
                .withIssuer(jwtIssuer)
                .withClaim("roles", user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()))
                .sign(algorithm);
    }

    public boolean validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            String tokenIssuer = decodedJWT.getIssuer();
            if (tokenIssuer.equals(jwtIssuer)) {
                return true;
            }
        } catch (RuntimeException ex) {
           throw new RuntimeException("Token is invalid");
        }

        return false;
    }


    public DecodedJWT getDecodedJWT(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            String tokenIssuer = decodedJWT.getIssuer();
            if (!tokenIssuer.equals(jwtIssuer)) {
                throw new RuntimeException("Invalid Token");
            } else {
                return decodedJWT;
            }
        } catch (RuntimeException ex) {
            throw new RuntimeException("Token is invalid");
        }
    }

}
