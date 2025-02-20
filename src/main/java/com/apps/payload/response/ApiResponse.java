package com.apps.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public ApiResponse(ResponseStatus responseStatus, T data) {
        this.status = responseStatus.getStatusCode();
        this.message = responseStatus.getMessage();
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResponseStatus.SUCCESS, data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(ResponseStatus.CREATED, data);
    }

    public static ApiResponse<Void> noContent() {
        return new ApiResponse<>(ResponseStatus.NO_CONTENT, null);
    }

    public static ApiResponse<String> error(ResponseStatus status, String message) {
        ApiResponse<String> response = new ApiResponse<>();
        response.setStatus(status.getStatusCode());
        response.setMessage(message);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    public static ApiResponse<String> badRequest(String message) {
        return error(ResponseStatus.BAD_REQUEST, message);
    }

    public static ApiResponse<String> notFound(String message) {
        return error(ResponseStatus.NOT_FOUND, message);
    }

    public static ApiResponse<String> unauthorized(String message) {
        return error(ResponseStatus.UNAUTHORIZED, message);
    }

    public static ApiResponse<String> forbidden(String message) {
        return error(ResponseStatus.FORBIDDEN, message);
    }

    public static ApiResponse<String> serverError(String message) {
        return error(ResponseStatus.INTERNAL_SERVER_ERROR, message);
    }
}
