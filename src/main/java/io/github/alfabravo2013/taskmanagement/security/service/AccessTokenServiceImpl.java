package io.github.alfabravo2013.taskmanagement.security.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AccessTokenServiceImpl implements AccessTokenService {
    private final JwtEncoder jwtEncoder;

    @Value("${access-token.ttl}")
    private int tokenTtl;

    public AccessTokenServiceImpl(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public String generateAccessToken(Authentication authentication) {
        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            var authorities = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                    .claim("scope", authorities)
                    .issuedAt(Instant.now())
                    .expiresAt(Instant.now().plus(tokenTtl, ChronoUnit.SECONDS))
                    .subject(userDetails.getUsername())
                    .build();

            return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
        }

        throw new RuntimeException("Principal не является объектом UserDetails");
    }
}
