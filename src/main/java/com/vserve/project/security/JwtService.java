package com.vserve.project.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private final Key key = Keys.hmacShaKeyFor(
            "THIS_IS_A_256_BITS_SECRET_KEY_FOR_SECURITY".getBytes()
    );

    private final long EXPIRATION = 1000L * 60 * 60 * 24 * 60 ; // 12 hours

    public String generateUserToken(UserPrincipal user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getUserId())
                .claim("accountType", "USER")
                .claim("roles", user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key)
                .compact();
    }

    public String generateOrganizationToken(OrganizationPrincipal org) {
        return Jwts.builder()
                .subject(org.getUsername())
                .claim("orgId", org.getOrgId())
                .claim("accountType", "ORGANIZATION")
                .claim("roles", org.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return parse(token).getBody().getSubject();
    }

    public boolean isValid(String token, UserDetails user) {
        return extractUsername(token).equals(user.getUsername())
                && !parse(token).getBody().getExpiration().before(new Date());
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token);
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getBody();
    }
}
