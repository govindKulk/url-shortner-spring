package com.govindkulk.api_gateway.filter;

import com.govindkulk.api_gateway.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // If JwtUtil is not available, just continue
        // if (jwtUtil == null) {
        //     return chain.filter(exchange);
        // }
        
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();
        
        // Skip authentication for these paths
        if (shouldSkipAuthentication(path)) {
            return chain.filter(exchange);
        }
        
        // Extract JWT token from Authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            
            System.out.println("authHeader is null");
            return chain.filter(exchange);
        }
        
        System.out.println("authHeader: " + authHeader);
        String token = authHeader.substring(7);
        
        if (jwtUtil.validateToken(token)) {

            System.out.println("Token is valid");
            // Extract user ID from token
            Long userId = jwtUtil.extractUserId(token);
            String username = jwtUtil.extractUsername(token);

            System.out.println("username: " + username);
            System.out.println("userId: " + userId);
            
                // Add user info to headers
                ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-Username", username)
                    .header("X-User-ID", userId.toString())
                    .build();
                
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
        
        }
        
        // Token is invalid, continue without user info
        return chain.filter(exchange);
    }
    
    private boolean shouldSkipAuthentication(String path) {

        // Skip authentication for redirects (short URLs)
        if (path.matches("/[a-zA-Z0-9]+")) {
            return true;
        }
        
        // Skip authentication for user service auth endpoints
        if (path.startsWith("/api/auth/")) {
            return true;
        }
        
        return false;
    }

    @Override
    public int getOrder() {
        return -100; // High priority
    }
}
