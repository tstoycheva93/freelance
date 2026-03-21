package com.thesis.freelance.security.websocket;

import com.thesis.freelance.security.jwt.JwtTokenProvider;
import com.thesis.freelance.security.jwt.JwtAuthenticationFilter;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        try {
            // 1. Check if user is already authenticated by JwtAuthenticationFilter
            if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                !"anonymousUser".equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())) {
                
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                attributes.put("username", username);
                // Try to get ID if principal is UserDetails
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                 if (principal instanceof com.thesis.freelance.features.user.entity.User user) {
                    attributes.put("userId", user.getId());
                }
                log.info("WebSocket handshake: User already authenticated as {}", username);
                return true;
            }

            // 2. Fallback: Validate token from query param manually
            String token = extractToken(request);
            log.info("WebSocket handshake attempt - token present: {}", StringUtils.hasText(token));
            
            if (!StringUtils.hasText(token)) {
                log.warn("WebSocket handshake failed: No token provided");
                return false;
            }

            if (!tokenProvider.validateToken(token)) {
                log.warn("WebSocket handshake failed: Invalid token");
                return false;
            }

            Claims claims = tokenProvider.validateAndGetClaims(token);
            String username = claims.getSubject();
            
            // Set attributes for the session
            String userId = claims.getId(); // Assuming ID is stored in claims, otherwise need to load user
            if (userId != null) {
                attributes.put("userId", UUID.fromString(userId));
            }
            attributes.put("username", username);
            
            // We don't strictly need to set SecurityContext here if we return true, 
            // but for Stomp consistency we might. 
            // However, the Interceptor is for the handshake. The WebSocket session is established after this.
            
            log.info("WebSocket handshake successful for user: {}", username);
            return true;
            
        } catch (Exception e) {
            log.error("WebSocket handshake failed: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }

    private String extractToken(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            String query = httpRequest.getQueryString();
            if (query != null && query.contains("token=")) {
                for (String param : query.split("&")) {
                    if (param.startsWith("token=")) {
                        return param.substring(6);
                    }
                }
            }
        }
        return null;
    }
}
