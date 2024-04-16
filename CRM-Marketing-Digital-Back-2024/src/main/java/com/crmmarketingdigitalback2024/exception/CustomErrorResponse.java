package com.crmmarketingdigitalback2024.exception;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class CustomErrorResponse {

    private String description;
    private LocalDateTime timestamp;

}
