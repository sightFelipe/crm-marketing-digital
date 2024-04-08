package com.crmmarketingdigitalback2024.model.user;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user")
public class UserEntity {
    @Id
    @Column(name = "id_user")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUser;
    @Column(name = "name_user")
    private String nameUser;
    @Column(name = "email_user")
    private String emailUser;
    @Column(name = "password_user")
    private String passwordUser;
}
