package com.social_media.commonsecurity.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtProvider {

    @Value("${application.security.jwt.secretKey}")
    private String signerKey;

    public String generateToken(String username){
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .issuer("social-media.com")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader,payload);

        try{
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        }
        catch(JOSEException e){
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    public boolean verifyToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(signerKey.getBytes());

            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            boolean verified = signedJWT.verify(verifier);

            return verified && expirationTime.after(new Date());
        } catch (Exception e) {
            log.error("Token verification failed: {}", e.getMessage());
            return false;
        }
    }
}
