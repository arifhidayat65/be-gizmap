package com.apps.security.middleware;

import com.apps.security.jwt.JwtUtils;
import com.apps.security.services.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class DashboardMiddleware extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(DashboardMiddleware.class);
    
    private static final Set<String> DASHBOARD_PATHS = Set.of(
        "/api/dashboard",
        "/api/admin",
        "/api/reports"
    );

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        if (isDashboardRequest(request)) {
            try {
                validateDashboardAccess(request);
                logDashboardAccess(request);
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                handleAccessError(response, e.getMessage());
                return;
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !isDashboardRequest(request);
    }

    private boolean isDashboardRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        return DASHBOARD_PATHS.stream().anyMatch(path::startsWith);
    }

    private void validateDashboardAccess(HttpServletRequest request) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new Exception("Authentication required for dashboard access");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        boolean hasAdminAccess = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!hasAdminAccess) {
            logger.warn("Unauthorized dashboard access attempt by user: {}", userDetails.getUsername());
            throw new Exception("Insufficient privileges for dashboard access");
        }

        // Validate token freshness
        String token = jwtUtils.parseJwt(request);
        if (token != null && !jwtUtils.validateJwtToken(token)) {
            throw new Exception("Invalid or expired token");
        }
    }

    private void logDashboardAccess(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        logger.info("Dashboard access - User: {}, Path: {}, Method: {}, IP: {}",
            userDetails.getUsername(),
            request.getRequestURI(),
            request.getMethod(),
            getClientIpAddress(request)
        );
    }

    private void handleAccessError(HttpServletResponse response, String message) throws IOException {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Dashboard access denied");
        errorResponse.put("message", message);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
