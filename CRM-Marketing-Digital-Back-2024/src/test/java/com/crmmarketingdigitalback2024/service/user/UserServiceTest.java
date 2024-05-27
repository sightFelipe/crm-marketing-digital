package com.crmmarketingdigitalback2024.service.user;

import com.crmmarketingdigitalback2024.commons.listener.RegistrationCompleteEventListener;
import com.crmmarketingdigitalback2024.commons.util.PasswordRequestUtil;
import com.crmmarketingdigitalback2024.exception.UserNotFoundException;
import com.crmmarketingdigitalback2024.model.UserEntity.PasswordResetTokenEntity;
import com.crmmarketingdigitalback2024.model.UserEntity.UserEntity;
import com.crmmarketingdigitalback2024.repository.user.IUserRepository;
import com.crmmarketingdigitalback2024.repository.user.PasswordResetTokenRepository;
import com.crmmarketingdigitalback2024.repository.user.TokenGenerator;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.UnsupportedEncodingException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    private UserEntity userEntity;

    @Mock
    private PasswordResetTokenEntity passwordResetTokenEntity;
    @Mock
    private PasswordRequestUtil passwordRequestUtil;
    @Mock
    private IUserRepository userRepository;

    @Mock
    private PasswordResetTokenService passwordResetTokenService;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private HttpServletRequest servletRequest;

    @Mock
    private RegistrationCompleteEventListener registrationCompleteEventListener;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private TokenGenerator tokenGenerator;

    @InjectMocks
    private UserService userService;

    @Test
    void resetPasswordRequest() throws MessagingException, UnsupportedEncodingException {
        // Given
        String email = "user@example.com";
        PasswordRequestUtil passwordRequestUtil = mock(PasswordRequestUtil.class);
        when(passwordRequestUtil.getEmail()).thenReturn(email);

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);

        when(servletRequest.getServerName()).thenReturn("localhost");
        when(servletRequest.getServerPort()).thenReturn(8080);
        when(servletRequest.getContextPath()).thenReturn("/myapp");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        String passwordResetToken = UUID.randomUUID().toString();
        when(tokenGenerator.generateToken()).thenReturn(passwordResetToken);

        String applicationUrl = "http://localhost:8080/myapp";
        String expectedUrl = applicationUrl + "/register/reset-password?token=" + passwordResetToken;

        // When
        String passwordResetUrl = userService.resetPasswordRequest(passwordRequestUtil, servletRequest);

        // Then
        assertEquals(expectedUrl, passwordResetUrl);
        verify(passwordResetTokenService).createPasswordResetTokenForUser(eq(userEntity), eq(passwordResetToken));
        verify(userRepository).findByEmail(email);
    }


    @Test
    public void testResetPasswordWithValidTokenAndUnused() {
        // Given
        String newPassword = "newPassword";
        String token = "tokenFromGenerator";
        UserEntity userEntity = mock(UserEntity.class);
        PasswordResetTokenEntity passwordResetTokenEntity = mock(PasswordResetTokenEntity.class);
        PasswordRequestUtil passwordRequestUtil = mock(PasswordRequestUtil.class);

        when(passwordRequestUtil.getNewPassword()).thenReturn(newPassword);
        when(passwordResetTokenService.validatePasswordResetToken(token)).thenReturn("valid");
        when(passwordResetTokenService.isPasswordResetTokenUsed(token)).thenReturn(false);
        when(passwordResetTokenService.findUserByPasswordToken(token)).thenReturn(Optional.of(userEntity));
        when(passwordResetTokenService.findPasswordResetToken(token)).thenReturn(passwordResetTokenEntity);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedPassword");

        // When
        String resetPasswordResult = userService.resetPassword(passwordRequestUtil, token);

        // Then
        assertEquals("Password has been reset successfully", resetPasswordResult);
        verify(userRepository).save(userEntity); // Verificar que el usuario se guarda con la nueva contraseña
        verify(passwordResetTokenEntity).setUsed(true);
        verify(passwordResetTokenRepository).save(passwordResetTokenEntity);
    }

    @Test
    public void testResetPasswordWithInvalidToken() {
        // Given
        String token = "invalidToken";
        PasswordRequestUtil passwordRequestUtil = mock(PasswordRequestUtil.class);

        when(passwordResetTokenService.validatePasswordResetToken(token)).thenReturn("Invalid token message");

        // When
        String resetPasswordResult = userService.resetPassword(passwordRequestUtil, token);

        // Then
        assertEquals("Invalid password reset token", resetPasswordResult);
        verify(passwordResetTokenService, never()).isPasswordResetTokenUsed(anyString());
        verify(passwordResetTokenService, never()).findUserByPasswordToken(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(passwordResetTokenRepository, never()).save(any(PasswordResetTokenEntity.class));
    }

    @Test
    public void testResetPasswordWithUsedToken() {
        // Given
        String token = "usedToken";
        PasswordRequestUtil passwordRequestUtil = mock(PasswordRequestUtil.class);

        when(passwordResetTokenService.validatePasswordResetToken(token)).thenReturn("valid");
        when(passwordResetTokenService.isPasswordResetTokenUsed(token)).thenReturn(true);

        // When
        String resetPasswordResult = userService.resetPassword(passwordRequestUtil, token);

        // Then
        assertEquals("The password reset token has already been used", resetPasswordResult);
        verify(passwordResetTokenService).validatePasswordResetToken(token);
        verify(passwordResetTokenService).isPasswordResetTokenUsed(token);
        verify(passwordResetTokenService, never()).findUserByPasswordToken(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(passwordResetTokenRepository, never()).save(any(PasswordResetTokenEntity.class));
    }

    @Test
    public void testResetPasswordWithValidUnusedTokenButNoUserFound() {
        // Given
        String token = "validUnusedToken";
        PasswordRequestUtil passwordRequestUtil = mock(PasswordRequestUtil.class);

        when(passwordResetTokenService.validatePasswordResetToken(token)).thenReturn("valid");
        when(passwordResetTokenService.isPasswordResetTokenUsed(token)).thenReturn(false);
        when(passwordResetTokenService.findUserByPasswordToken(token)).thenReturn(Optional.empty()); // No se encuentra el usuario

        // When
        String resetPasswordResult = userService.resetPassword(passwordRequestUtil, token);

        // Then
        assertEquals("Invalid password reset token", resetPasswordResult);
        verify(passwordResetTokenService).validatePasswordResetToken(token);
        verify(passwordResetTokenService).isPasswordResetTokenUsed(token);
        verify(passwordResetTokenService).findUserByPasswordToken(token);
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(passwordResetTokenRepository, never()).save(any(PasswordResetTokenEntity.class));
    }

    @Test
    void changePassword() {
        UserEntity user = new UserEntity();
        user.setPassword("oldPassword");
        String newPassword = "newPassword";
        String encodedPassword = "encodedNewPassword";

        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

        userService.changePassword(user, newPassword);

        assertEquals(encodedPassword, user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void createPasswordResetTokenForUser() {
        UserEntity user = new UserEntity();
        String passwordResetToken = "resetToken";

        userService.createPasswordResetTokenForUser(user, passwordResetToken);

        verify(passwordResetTokenService).createPasswordResetTokenForUser(user, passwordResetToken);
    }

    @Test
    void findByEmail() {
        String email = "test@example.com";
        UserEntity user = new UserEntity();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<UserEntity> result = userService.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void validatePasswordResetToken() {
        String token = "validToken";
        String expectedValidationResult = "valid";

        when(passwordResetTokenService.validatePasswordResetToken(token)).thenReturn(expectedValidationResult);

        String validationResult = userService.validatePasswordResetToken(token);

        assertEquals(expectedValidationResult, validationResult);
        verify(passwordResetTokenService).validatePasswordResetToken(token);
    }

    @Test
    void findUserByPasswordToken() {
        String token = "token";
        UserEntity user = new UserEntity();

        when(passwordResetTokenService.findUserByPasswordToken(token)).thenReturn(Optional.of(user));

        Optional<UserEntity> result = userService.findUserByPasswordToken(token);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(passwordResetTokenService).findUserByPasswordToken(token);
    }

    @Test
    public void testGetActiveUsers() {

        UserEntity user1 = new UserEntity();
        user1.setEnabled(true);
        UserEntity user2 = new UserEntity();
        user2.setEnabled(true);
        List<UserEntity> expectedUsers = Arrays.asList(user1, user2);

        when(userRepository.findByEnabledTrue()).thenReturn(expectedUsers);

        List<UserEntity> actualUsers = userService.getActiveUsers();

        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    public void testGetDisabledUsers() {
        UserEntity user1 = new UserEntity();
        user1.setEnabled(false);
        UserEntity user2 = new UserEntity();
        user2.setEnabled(false);
        List<UserEntity> expectedUsers = Arrays.asList(user1, user2);

        when(userRepository.findByEnabledFalse()).thenReturn(expectedUsers);

        List<UserEntity> actualUsers = userService.getDisabledUsers();

        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    public void testUpdateUserStatus_ActiveUserToActiveStatus() throws UserNotFoundException {
        Long userId = 1L;
        String newStatus = "activo";

        UserEntity user = new UserEntity();
        user.setEnabled(true);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> userService.updateUserStatus(userId, newStatus));
    }

    @Test
    public void testUpdateUserStatus_InactiveUserToInactiveStatus() throws UserNotFoundException {
        Long userId = 1L;
        String newStatus = "inactivo";

        UserEntity user = new UserEntity();
        user.setEnabled(false);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> userService.updateUserStatus(userId, newStatus));
    }

    @Test
    public void testUpdateUserStatus_ActiveUserToInactiveStatus() throws UserNotFoundException {
        Long userId = 1L;
        String newStatus = "inactivo";

        UserEntity user = new UserEntity();
        user.setEnabled(true);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.updateUserStatus(userId, newStatus);

        assertFalse(user.isEnabled());
    }

    @Test
    public void testUpdateUserStatus_InactiveUserToActiveStatus() throws UserNotFoundException {
        Long userId = 1L;
        String newStatus = "activo";

        UserEntity user = new UserEntity();
        user.setEnabled(false);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.updateUserStatus(userId, newStatus);

        assertTrue(user.isEnabled());
    }

    @Test
    public void testUpdateUserStatus_InvalidStatus() {
        Long userId = 1L;
        String newStatus = "otro";

        UserEntity user = new UserEntity();
        user.setEnabled(false);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> userService.updateUserStatus(userId, newStatus));
    }

    @Test
    public void testUpdateUserStatus_UserNotFound() {
        Long userId = 1L;
        String newStatus = "activo";

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUserStatus(userId, newStatus));
    }


}
