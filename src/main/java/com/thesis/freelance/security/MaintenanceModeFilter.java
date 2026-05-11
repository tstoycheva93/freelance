package com.thesis.freelance.security;

import com.thesis.freelance.features.system.service.SystemConfigService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class MaintenanceModeFilter extends OncePerRequestFilter {

    private final SystemConfigService systemConfigService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        if (!systemConfigService.isMaintenanceMode()) {
            chain.doFilter(request, response);
            return;
        }

        String fullPath = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = fullPath;
        if (!contextPath.isEmpty()) {
            path = fullPath.substring(contextPath.length());
        }

        if (path.startsWith("/auth/")
                || path.startsWith("/system/public/")
                || path.startsWith("/ws/")
                || path.startsWith("/images/")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.equals("/favicon.ico")
                || path.startsWith("/swagger-ui/")
                || path.startsWith("/v3/api-docs/")
                || path.startsWith("/cert/scheduler/")
                || path.equals("/error")
                || path.equals("/roles")
                || path.equals("/home/")) {
            chain.doFilter(request, response);
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            chain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        response.setContentType("application/json");
        response.getWriter().write(
                "{\"maintenanceMode\":true,\"message\":\"Platform is currently under maintenance. Please try again later.\"}"
        );
    }
}
