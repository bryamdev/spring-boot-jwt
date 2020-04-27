package com.bolsadeideas.springboot.datajpa.app.auth.service;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import com.bolsadeideas.springboot.datajpa.app.auth.CustomGrantedAuthority;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTServiceImpl implements IJWTService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    @Override
    public String createToken(Authentication auth) throws JsonProcessingException {

        // String username = ((User) auth.getPrincipal()).getUsername();
        String username = auth.getName();
        Collection<? extends GrantedAuthority> roles = auth.getAuthorities();

        logger.info("Key Automatica: " + SECRET_KEY.getEncoded());
        logger.info("Con key automatica");

        Claims claims = Jwts.claims();
        claims.put("roles", new ObjectMapper().writeValueAsString(roles));
        logger.info(claims.get("roles").toString());

        String token = Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .signWith(SECRET_KEY)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 14400000)) // En cuantos milisegundos(Esta en horas)expirará
            .compact();

        return token;
    }

    @Override
    public boolean validateToken(String token) {

        try {

            getClaims(token);

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.info("Error en validación de Token: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Claims getClaims(String token) {

        Claims claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(resolveToken(token))
                .getBody();

        return claims;
    }

    @Override
    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(String token)
            throws JsonMappingException, JsonProcessingException {
        
        String roles = (String) getClaims(token).get("roles");

        Collection<? extends GrantedAuthority> authorities = Arrays.asList(new ObjectMapper().readValue(roles, CustomGrantedAuthority[].class));

        return authorities;
    }

    @Override
    public String resolveToken(String token) {
        return token.replace("Bearer ", "");
    }

}