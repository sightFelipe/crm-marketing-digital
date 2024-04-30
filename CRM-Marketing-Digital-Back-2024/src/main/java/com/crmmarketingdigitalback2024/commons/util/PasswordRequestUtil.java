package com.crmmarketingdigitalback2024.commons.util;

import lombok.Data;
@Data
public class PasswordRequestUtil {
    private String email;
    private String oldPassword;
    private String newPassword;
}
