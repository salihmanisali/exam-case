package org.example.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final String message;
    private final ErrorDetails error;

    @JsonCreator
    private ApiResponse(@JsonProperty("success") boolean success, @JsonProperty("data") T data, @JsonProperty("message") String message, @JsonProperty("error") ErrorDetails error) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.error = error;
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message, null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, "Success", null);
    }

    public static <T> ApiResponse<T> error(String message, String code) {
        return new ApiResponse<>(false, null, null, new ErrorDetails(message, code));
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, null, new ErrorDetails(message, null));
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public ErrorDetails getError() {
        return error;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorDetails {
        private final String message;
        private final String code;

        @JsonCreator
        public ErrorDetails(@JsonProperty("message") String message, @JsonProperty("code") String code) {
            this.message = message;
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public String getCode() {
            return code;
        }
    }
}
