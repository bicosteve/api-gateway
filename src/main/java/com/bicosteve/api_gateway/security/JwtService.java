package com.bicosteve.api_gateway.security;


import com.bicosteve.api_gateway.models.Profile;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {
    private final JwtConfig jwtConfig;

    // Used to generically generate token
    private String generateToken(Profile profile, long expiration){
        return Jwts.builder().
                subject(profile.getProfileId().toString())
                .claim("phone_number",profile.getPhoneNumber())
                .claim("profile_id",profile.getProfileId())
                .claim("status",profile.getProfileSettings().getStatus())
                .claim("is_verified",profile.getProfileSettings().getIsVerified())
                .claim("is_deleted",profile.getProfileSettings().getIsDeleted())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * expiration))
                .signWith(Keys.hmacShaKeyFor(this.jwtConfig.getSecret().getBytes()))
                .compact();
    }

    // Used to verify the claims
    private Claims getClaims(String token){
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(this.jwtConfig.getSecret().getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Used to generate accessToken
    public String generateAccessToken(Profile profile){
        return this.generateToken(profile,this.jwtConfig.getAccessTokenExpiration());
    }

    // Used to generate refreshToken
    public String generateRefreshToken(Profile profile){
        return this.generateToken(profile,this.jwtConfig.getRefreshTokenExpiration());
    }

    // Used to get the owner from the token
    public Long getProfileIdFromToken(String token){
        return Long.valueOf(this.getClaims(token).getSubject());
    }

    // Used to get phoneNumber from the token
    public String getPhoneNumberFromToken(String token){
        return String.valueOf(this.getClaims(token).get("phone_number"));
    }

    // Used to get status from the token
    public Integer getProfileStatus(String token){
        return (Integer) this.getClaims(token).get("status");
    }

    // Used to get isVerified from the token
    public Integer getProfileIsVerified(String token){
        return (Integer) this.getClaims(token).get("is_verified");
    }

    // Used to get isDeleted from the token
    public Integer getProfileIsDeleted(String token){
        return (Integer) this.getClaims(token).get("is_verified");
    }

    public boolean validateToken(String token){
        try{
           return this.getClaims(token).getExpiration().after(new Date());
        } catch(JwtException | IllegalArgumentException e) {
            log.error("JwtService::There was error {} while validating token", e.getMessage());
            return false;
        }
    }


}
