package com.apps.security.interceptor;

import com.apps.security.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);
    private static final String REQUEST_ID_ATTRIBUTE = "requestId";
    private static final String REQUEST_START_TIME = "requestStartTime";

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Generate unique request ID for tracking
        String requestId = UUID.randomUUID().toString();
        request.setAttribute(REQUEST_ID_ATTRIBUTE, requestId);
        request.setAttribute(REQUEST_START_TIME, System.currentTimeMillis());

        // Log request details
        logRequestDetails(request, requestId);

        // Add security headers
        addSecurityHeaders(response);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        String requestId = (String) request.getAttribute(REQUEST_ID_ATTRIBUTE);
        Long startTime = (Long) request.getAttribute(REQUEST_START_TIME);
        long duration = System.currentTimeMillis() - startTime;

        // Log response details
        logResponseDetails(request, response, requestId, duration);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ex != null) {
            String requestId = (String) request.getAttribute(REQUEST_ID_ATTRIBUTE);
            logger.error("Request {} failed with exception: {}", requestId, ex.getMessage());
        }
    }

    private void logRequestDetails(HttpServletRequest request, String requestId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : "anonymous";

        logger.info("Request {} - User: {}, Method: {}, URI: {}, IP: {}",
            requestId,
            username,
            request.getMethod(),
            request.getRequestURI(),
            getClientIpAddress(request)
        );

        // Log headers if needed
        if (logger.isDebugEnabled()) {
            request.getHeaderNames().asIterator().forEachRemaining(headerName ->
                logger.debug("Request {} - Header {}: {}", 
                    requestId, 
                    headerName, 
                    request.getHeader(headerName))
            );
        }
    }

    private void logResponseDetails(HttpServletRequest request, HttpServletResponse response, String requestId, long duration) {
        logger.info("Response {} - Status: {}, Duration: {}ms, URI: {}",
            requestId,
            response.getStatus(),
            duration,
            request.getRequestURI()
        );
    }

    private void addSecurityHeaders(HttpServletResponse response) {
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        response.setHeader("Cache-Control", "no-store, max-age=0");
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
