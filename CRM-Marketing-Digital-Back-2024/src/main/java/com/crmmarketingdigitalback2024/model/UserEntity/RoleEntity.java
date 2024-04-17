package com.crmmarketingdigitalback2024.model.UserEntity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "roles")
@Data
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_roles")
    private int id;
    @Column(name = "name")
    private String name;
}
