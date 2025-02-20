package com.apps.security.middleware;

import com.apps.payload.request.LoginRequest;
import com.apps.security.jwt.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class LoginMiddleware extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(LoginMiddleware.class);
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MINUTES = 15;

    private final Map<String, Integer> loginAttempts = new ConcurrentHashMap<>();
    private final Map<String, Long> lockoutTimestamps = new ConcurrentHashMap<>();

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        if (isLoginRequest(request)) {
            String ipAddress = getClientIpAddress(request);
            
            if (isIpLocked(ipAddress)) {
                handleLockedIp(response, ipAddress);
                return;
            }

            try {
                validateLoginRequest(request);
                // If validation passes, increment attempt counter
                incrementLoginAttempt(ipAddress);
                
                // Continue with the filter chain
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                handleLoginError(response, e.getMessage());
                return;
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !isLoginRequest(request);
    }

    private boolean isLoginRequest(HttpServletRequest request) {
        return request.getMethod().equals("POST") && 
               request.getRequestURI().equals("/api/auth/signin");
    }

    private void validateLoginRequest(HttpServletRequest request) throws Exception {
        try {
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            
            if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
                throw new Exception("Username is required");
            }
            
            if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
                throw new Exception("Password is required");
            }
            
            // Reset the input stream for the next filter
            request.setAttribute("loginRequest", loginRequest);
        } catch (IOException e) {
            throw new Exception("Invalid login request format");
        }
    }

    private void incrementLoginAttempt(String ipAddress) {
        loginAttempts.merge(ipAddress, 1, Integer::sum);
        
        if (loginAttempts.get(ipAddress) >= MAX_LOGIN_ATTEMPTS) {
            lockoutTimestamps.put(ipAddress, System.currentTimeMillis());
            logger.warn("IP {} has been locked out due to too many login attempts", ipAddress);
        }
    }

    private boolean isIpLocked(String ipAddress) {
        Long lockoutTimestamp = lockoutTimestamps.get(ipAddress);
        if (lockoutTimestamp == null) {
            return false;
        }

        long elapsedMinutes = TimeUnit.MILLISECONDS.toMinutes(
            System.currentTimeMillis() - lockoutTimestamp
        );

        if (elapsedMinutes >= LOCKOUT_DURATION_MINUTES) {
            // Reset counters if lockout period has expired
            loginAttempts.remove(ipAddress);
            lockoutTimestamps.remove(ipAddress);
            return false;
        }

        return true;
    }

    private void handleLockedIp(HttpServletResponse response, String ipAddress) throws IOException {
        Long lockoutTimestamp = lockoutTimestamps.get(ipAddress);
        long remainingMinutes = LOCKOUT_DURATION_MINUTES - 
            TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - lockoutTimestamp);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Too many login attempts");
        errorResponse.put("message", String.format("Please try again in %d minutes", remainingMinutes));

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }

    private void handleLoginError(HttpServletResponse response, String message) throws IOException {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Login validation failed");
        errorResponse.put("message", message);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
