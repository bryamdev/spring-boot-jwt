package com.bolsadeideas.springboot.datajpa.app.auth.service;

import java.util.Collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import io.jsonwebtoken.Claims;

public interface IJWTService {

    public String createToken(Authentication auth) throws JsonProcessingException;

    public boolean validateToken(String token);

    public Claims getClaims(String token);

    public String getUsername(String token);

    public Collection<? extends GrantedAuthority> getAuthorities(String token) throws JsonMappingException, JsonProcessingException;

    public String resolveToken(String token);

}