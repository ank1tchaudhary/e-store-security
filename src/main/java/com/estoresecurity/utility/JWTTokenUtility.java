package com.estoresecurity.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.estoresecurity.domain.Role;
import com.estoresecurity.domain.User;
import io.jsonwebtoken.*;
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




    public String generateJWTToken(User user){

        Algorithm algorithm=Algorithm.HMAC256(secret);

        return JWT.create()
                .withAudience("BackOfficeClientApp")
                .withSubject(user.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis()+ jwtExpiration))
                .withIssuer(jwtIssuer)
                .withClaim("roles",user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()))
                .sign(algorithm);
    }

    public boolean validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            String tokenIssuer=decodedJWT.getIssuer();
            if(jwtIssuer.equals(tokenIssuer)){
                return true;
            }
        } catch (ExpiredJwtException ex) {
            log.error("JWT expired --> {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("Token is null, empty or only whitespace --> {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("JWT is invalid --> {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("JWT is not supported --> {}", ex.getMessage());
        } catch (SignatureException ex) {
            log.error("Signature validation failed");
        }

        return false;
    }

    public JWTVerifier jwtVerifier(){
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.require(algorithm).build();
    }


    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }


}
