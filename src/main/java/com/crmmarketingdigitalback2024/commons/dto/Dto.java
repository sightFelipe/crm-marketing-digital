package com.crmmarketingdigitalback2024.commons.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
public class Dto {

    private String name;
    private String email;
    private String password;
}
