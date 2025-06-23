package com.govindkulk.user_service.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.govindkulk.user_service.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    private final CustomUserDetailsService customUserDetailsService;

    private final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
    
    public JwtAuthFilter(JwtTokenUtil jwtTokenUtil, CustomUserDetailsService customUserDetailsService){
        this.jwtTokenUtil = jwtTokenUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
            String authHeader = request.getHeader("Authorization");
            String username = null;
            String token = null;

            if(authHeader != null && authHeader.startsWith("Bearer ")){
                try{
                    username = jwtTokenUtil.extractUsername(authHeader.substring(7));
                }catch(Exception e){
                    logger.warn("Invalid token");
                }
            }

            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){

                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                if(jwtTokenUtil.validateToken(token)){
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
    
    
                    authentication.setDetails(new WebAuthenticationDetails(request));
    
                    logger.info("Web Details {}", authentication.getDetails());
    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                }


            }

            doFilter(request, response, filterChain);
            

    }
    
}
