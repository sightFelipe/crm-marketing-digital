package com.crmmarketingdigitalback2024.repository.user;

import com.crmmarketingdigitalback2024.model.UserEntity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<UserEntity,Long> {

    Optional<UserEntity> findByEmail (String email);
    Optional<UserEntity> findByEmailAndPassword(String email, String password);

}
