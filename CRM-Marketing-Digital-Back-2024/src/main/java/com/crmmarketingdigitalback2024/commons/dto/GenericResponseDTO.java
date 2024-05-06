package com.crmmarketingdigitalback2024.commons.dto;

import lombok.*;
import org.aspectj.internal.lang.annotation.ajcDeclareEoW;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class GenericResponseDTO implements Serializable {
    public String message;
    public Object objectResponse;
    public int objectId;
    public int statusCode;

    public GenericResponseDTO(String userDeletedSuccessfully, boolean b) {
    }
}
