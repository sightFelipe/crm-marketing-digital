package com.crmmarketingdigitalback2024.service.user;
import com.crmmarketingdigitalback2024.model.UserEntity.PasswordResetTokenEntity;
import com.crmmarketingdigitalback2024.model.UserEntity.UserEntity;
import com.crmmarketingdigitalback2024.repository.user.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;


    public void createPasswordResetTokenForUser(UserEntity user, String passwordToken) {
        PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity(passwordToken, user);
        passwordResetTokenRepository.save(passwordResetTokenEntity);
    }

    public String validatePasswordResetToken(String passwordResetToken) {
        PasswordResetTokenEntity passwordToken = passwordResetTokenRepository.findByToken(passwordResetToken);
        if(passwordToken == null){
            return "Invalid verification token";
        }
        UserEntity user = passwordToken.getUser();
        Calendar calendar = Calendar.getInstance();
        if ((passwordToken.getExpirationTime().getTime()-calendar.getTime().getTime())<= 0){
            return "Link already expired, resend link";
        }
        return "valid";
    }
    public Optional<UserEntity> findUserByPasswordToken(String passwordResetToken) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(passwordResetToken).getUser());
    }

    public PasswordResetTokenEntity findPasswordResetToken(String token){
        return passwordResetTokenRepository.findByToken(token);
    }
    public boolean isPasswordResetTokenUsed(String passwordResetToken) {
        PasswordResetTokenEntity passwordToken = passwordResetTokenRepository.findByToken(passwordResetToken);
        return passwordToken != null && passwordToken.isUsed();
    }

}
