package com.crmmarketingdigitalback2024.commons.dto.user;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UserDto {
    private Integer idUser;
    private String nameUser;
    private String emailUser;
    private String passwordUser;
}
