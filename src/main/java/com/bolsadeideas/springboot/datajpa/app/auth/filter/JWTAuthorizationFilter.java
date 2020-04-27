package com.bolsadeideas.springboot.datajpa.app.auth.filter;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bolsadeideas.springboot.datajpa.app.auth.service.IJWTService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private IJWTService jwtService;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, IJWTService jwtService) {
        super(authenticationManager);
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        String header = request.getHeader("Authorization");

        if(!requiresAuthentication(header)){
            chain.doFilter(request, response);//Contienuario con la cadena de filtros hasta llegar al metdodo Handler
            return;//se sale del filtro
        }

        String token = jwtService.resolveToken(header);       
        
        //para authenticarse en cada peticion
        UsernamePasswordAuthenticationToken authentication = null;

        if(jwtService.validateToken(token)){

            String username = jwtService.getUsername(token);

            Collection<? extends GrantedAuthority> authorities = jwtService.getAuthorities(token);

            authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);

            logger.info("Username desde token: " + username);

        }    

        //asigna el objeto de autenticacion al conexto de seguridad actual de Spring
        //autentica al usuario dentro del request
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);//continua con la cadena de ejecucion del request


    }
    
    protected boolean requiresAuthentication(String header) {
        
        if(header == null || !header.startsWith("Bearer ")){
            return false;
        }
        return true;
    }


}