package com.crmmarketingdigitalback2024.repository.user;

import com.crmmarketingdigitalback2024.model.UserEntity.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, Long> {
    PasswordResetTokenEntity findByToken(String passwordResetToken);
}