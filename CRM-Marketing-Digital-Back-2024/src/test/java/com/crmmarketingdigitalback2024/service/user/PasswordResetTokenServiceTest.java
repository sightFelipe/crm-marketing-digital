package com.crmmarketingdigitalback2024.service.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.crmmarketingdigitalback2024.model.UserEntity.PasswordResetTokenEntity;
import com.crmmarketingdigitalback2024.model.UserEntity.UserEntity;
import com.crmmarketingdigitalback2024.repository.user.PasswordResetTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Calendar;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PasswordsResetTokenServiceTest {

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @InjectMocks
    private PasswordResetTokenService passwordResetTokenService;

    private final String passwordToken = "passwordToken";
    private UserEntity userEntity;
    private PasswordResetTokenEntity passwordResetTokenEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        passwordResetTokenEntity = new PasswordResetTokenEntity(passwordToken, userEntity);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 1); // Token válido por una hora
        passwordResetTokenEntity.setExpirationTime(calendar.getTime());
    }

    @Test
    void createPasswordResetTokenForUserTest() {
        passwordResetTokenService.createPasswordResetTokenForUser(userEntity, passwordToken);
        verify(passwordResetTokenRepository).save(any(PasswordResetTokenEntity.class));
    }

    @Test
    void validatePasswordResetTokenTest_Valid() {
        when(passwordResetTokenRepository.findByToken(passwordToken)).thenReturn(passwordResetTokenEntity);
        String result = passwordResetTokenService.validatePasswordResetToken(passwordToken);
        assertEquals("valid", result);
    }

    @Test
    void validatePasswordResetTokenTest_InvalidToken() {
        when(passwordResetTokenRepository.findByToken(passwordToken)).thenReturn(null);
        String result = passwordResetTokenService.validatePasswordResetToken(passwordToken);
        assertEquals("Invalid verification token", result);
    }

    @Test
    void validatePasswordResetTokenTest_ExpiredToken() {
        // Establece un tiempo de expiración en el pasado para simular un token expirado
        Calendar pastExpiration = Calendar.getInstance();
        pastExpiration.add(Calendar.HOUR, -1);
        passwordResetTokenEntity.setExpirationTime(pastExpiration.getTime());

        when(passwordResetTokenRepository.findByToken(passwordToken)).thenReturn(passwordResetTokenEntity);
        String result = passwordResetTokenService.validatePasswordResetToken(passwordToken);
        assertEquals("Link already expired, resend link", result);
    }

    @Test
    void findUserByPasswordTokenTest() {
        when(passwordResetTokenRepository.findByToken(passwordToken)).thenReturn(passwordResetTokenEntity);
        Optional<UserEntity> result = passwordResetTokenService.findUserByPasswordToken(passwordToken);
        assertTrue(result.isPresent());
        assertEquals(userEntity, result.get());
    }

    @Test
    void findPasswordResetTokenTest() {
        // Configure the mock to return the password reset token entity when findByToken is called
        when(passwordResetTokenRepository.findByToken(passwordToken)).thenReturn(passwordResetTokenEntity);

        // Call the method under test
        PasswordResetTokenEntity result = passwordResetTokenService.findPasswordResetToken(passwordToken);

        // Assert the result is the one returned by the repository
        assertEquals(passwordResetTokenEntity, result);

        // Verify that findByToken was called on the repository
        verify(passwordResetTokenRepository).findByToken(passwordToken);
    }

    @Test
    void isPasswordResetTokenUsedTest_Used() {
        passwordResetTokenEntity.setUsed(true);
        when(passwordResetTokenRepository.findByToken(passwordToken)).thenReturn(passwordResetTokenEntity);
        assertTrue(passwordResetTokenService.isPasswordResetTokenUsed(passwordToken));
    }

    @Test
    void isPasswordResetTokenUsedTest_NotUsed() {
        passwordResetTokenEntity.setUsed(false);
        when(passwordResetTokenRepository.findByToken(passwordToken)).thenReturn(passwordResetTokenEntity);
        assertFalse(passwordResetTokenService.isPasswordResetTokenUsed(passwordToken));
    }

}