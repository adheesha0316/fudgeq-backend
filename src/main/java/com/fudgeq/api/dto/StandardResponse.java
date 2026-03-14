package com.fudgeq.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StandardResponse<T> {

    private int status;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    // Helper method for quick success responses
    public static <T> StandardResponse<T> success(String message, T data) {
        return StandardResponse.<T>builder()
                .status(200)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
