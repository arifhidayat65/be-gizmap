package com.apps.controller;

import com.apps.payload.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<ApiResponse<String>> handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
        String path = (String) request.getAttribute("javax.servlet.error.request_uri");

        if (path == null) {
            path = request.getRequestURI();
        }

        HttpStatus status = HttpStatus.valueOf(statusCode != null ? statusCode : 500);
        String message = exception != null ? exception.getMessage() : "Unexpected error occurred";

        ApiResponse<String> errorResponse;
        switch (status) {
            case NOT_FOUND:
                errorResponse = ApiResponse.notFound("Resource not found: " + path);
                break;
            case FORBIDDEN:
                errorResponse = ApiResponse.forbidden("Access denied to: " + path);
                break;
            case UNAUTHORIZED:
                errorResponse = ApiResponse.unauthorized("Authentication required for: " + path);
                break;
            case BAD_REQUEST:
                errorResponse = ApiResponse.badRequest(message);
                break;
            default:
                errorResponse = ApiResponse.serverError("An error occurred processing: " + path);
        }

        return ResponseEntity.status(status).body(errorResponse);
    }
}
