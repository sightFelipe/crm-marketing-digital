package com.crmmarketingdigitalback2024.service.user;

import com.crmmarketingdigitalback2024.repository.user.TokenGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDTokenGenerator implements TokenGenerator {
    @Override
    public String generateToken() {
        return UUID.randomUUID().toString();
    }
}
