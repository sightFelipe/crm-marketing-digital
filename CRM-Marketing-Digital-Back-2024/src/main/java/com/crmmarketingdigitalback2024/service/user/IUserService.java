package com.crmmarketingdigitalback2024.service.user;

import com.crmmarketingdigitalback2024.model.UserEntity.UserEntity;

import java.util.Optional;

public interface IUserService {

    void changePassword(UserEntity theUser, String newPassword);

    String validatePasswordResetToken(String token);

    Optional<UserEntity> findUserByPasswordToken(String token);

    void createPasswordResetTokenForUser(UserEntity user, String passwordResetToken);

    Optional<UserEntity> findByEmail(String email);
}
