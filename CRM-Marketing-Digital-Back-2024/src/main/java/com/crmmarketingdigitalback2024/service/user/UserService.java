package com.crmmarketingdigitalback2024.service.user;

import com.crmmarketingdigitalback2024.commons.constants.response.IResponse;
import com.crmmarketingdigitalback2024.commons.constants.response.user.IUserResponse;
import com.crmmarketingdigitalback2024.commons.converter.user.UserConverter;
import com.crmmarketingdigitalback2024.commons.dto.GenericResponseDTO;
import com.crmmarketingdigitalback2024.commons.dto.user.UserDto;
import com.crmmarketingdigitalback2024.commons.listener.RegistrationCompleteEventListener;
import com.crmmarketingdigitalback2024.commons.util.PasswordRequestUtil;
import com.crmmarketingdigitalback2024.model.UserEntity.PasswordResetTokenEntity;
import com.crmmarketingdigitalback2024.model.UserEntity.UserEntity;
import com.crmmarketingdigitalback2024.repository.user.IUserRepository;
import com.crmmarketingdigitalback2024.repository.user.PasswordResetTokenRepository;
import com.crmmarketingdigitalback2024.repository.user.TokenGenerator;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.antlr.v4.runtime.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Log4j2
@Service
public class UserService implements IUserService {
    private final IUserRepository iUserRepository;
    private final UserConverter userConverter;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordResetTokenService passwordResetTokenService;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    private final TokenGenerator tokenGenerator;

    public UserService(IUserRepository iUserRepository, UserConverter userConverter, PasswordResetTokenRepository verificationTokenRepository, PasswordResetTokenRepository passwordResetTokenRepository, PasswordResetTokenService passwordResetTokenService, JavaMailSender mailSender, PasswordEncoder passwordEncoder, TokenGenerator tokenGenerator) {
        this.iUserRepository = iUserRepository;
        this.userConverter = userConverter;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordResetTokenService = passwordResetTokenService;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;

    }


    public ResponseEntity<GenericResponseDTO> serviceUser(UserDto userDTO) {
        try {
            List<UserEntity> users = iUserRepository.findAll();
            if (!users.isEmpty()) {
                for (UserEntity user : users) {
                    UserDto userDecode = userConverter.convertUserEntityToUserDTO(user);
                    if (userDecode.getPasswordUser().equals(userDTO.getPasswordUser()) &&
                            userDecode.getEmailUser().equals(userDTO.getEmailUser())) {
                        return ResponseEntity.ok(GenericResponseDTO.builder()
                                .message(IResponse.OPERATION_SUCCESS)
                                .objectResponse(IUserResponse.AUTENTIFICATION_SUCESS)
                                .objectId(userDecode.getIdUser())
                                .statusCode(HttpStatus.OK.value())
                                .build());
                    }
                }
                return ResponseEntity.badRequest().body(GenericResponseDTO.builder()
                        .message(IUserResponse.USER_FAIL_PASSWORD)
                        .objectResponse(null)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build());
            } else {
                return ResponseEntity.badRequest().body(GenericResponseDTO.builder()
                        .message(IUserResponse.USER_FAIL)
                        .objectResponse(null)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build());
            }
        } catch (Exception e) {
            log.error(IResponse.INTERNAL_SERVER, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponseDTO.builder()
                            .message(IResponse.INTERNAL_SERVER)
                            .objectResponse(null)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build());
        }
    }
    public ResponseEntity<GenericResponseDTO> createUser(UserDto userDTO) {
        System.out.println("entro al servicio");
        try {
            System.out.println("try");
            Optional<UserEntity> existeLogin = iUserRepository.findById(Long.valueOf(userDTO.getIdUser()));
            System.out.println("existe: " + existeLogin);
            if (existeLogin.isEmpty()) {
                UserEntity userEntity = userConverter.convertUserDTOToUserEntity(userDTO);
                iUserRepository.save(userEntity);
                return ResponseEntity.ok(GenericResponseDTO.builder()
                        .message(IResponse.OPERATION_SUCCESS)
                        .objectResponse(IResponse.CREATE_SUCCESS)
                        .statusCode(HttpStatus.OK.value())
                        .build());
            } else {
                return ResponseEntity.badRequest().body(GenericResponseDTO.builder()
                        .message(IResponse.OPERATION_FAIL)
                        .objectResponse(IUserResponse.USER_SUCCESS)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build());
            }
        } catch (Exception e) {
            log.error(IResponse.INTERNAL_SERVER + e);
            return new ResponseEntity<>(GenericResponseDTO.builder()
                    .message(IResponse.INTERNAL_SERVER)
                    .objectResponse(null)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public ResponseEntity<GenericResponseDTO> updateUser(UserDto userDTO)
    {
        try{
            Optional<UserEntity> userExist = iUserRepository.findById(Long.valueOf(userDTO.getIdUser()));
            if(userExist.isPresent()) {
                UserEntity userEntity = userConverter.convertUserDTOToUserEntity(userDTO);
                iUserRepository.save(userEntity);
                return ResponseEntity.ok(GenericResponseDTO.builder()
                        .message(IResponse.OPERATION_SUCCESS)
                        .objectResponse(IResponse.CREATE_SUCCESS)
                        .statusCode(HttpStatus.OK.value())
                        .build());
            } else {
                return ResponseEntity.badRequest().body(GenericResponseDTO.builder()
                        .message(IResponse.OPERATION_FAIL)
                        .objectResponse(IUserResponse.USER_SUCCESS)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build());
            }
        } catch (Exception e) {
            log.error(IResponse.INTERNAL_SERVER + e);
            return new ResponseEntity<>(GenericResponseDTO.builder()
                    .message(IResponse.INTERNAL_SERVER)
                    .objectResponse(null)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public ResponseEntity<GenericResponseDTO> deleteUser(Integer userId)
    {
        try{
            Optional<UserEntity> userExist = iUserRepository.findById(Long.valueOf(userId));
            if(userExist.isPresent()) {
                iUserRepository.delete(userExist.get());
                return ResponseEntity.ok(GenericResponseDTO.builder()
                        .message(IResponse.OPERATION_SUCCESS)
                        .objectResponse(IResponse.CREATE_SUCCESS)
                        .statusCode(HttpStatus.OK.value())
                        .build());
            } else {
                return ResponseEntity.badRequest().body(GenericResponseDTO.builder()
                        .message(IResponse.OPERATION_FAIL)
                        .objectResponse(IUserResponse.USER_SUCCESS)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build());
            }
        } catch (Exception e) {
            log.error(IResponse.INTERNAL_SERVER + e);
            return new ResponseEntity<>(GenericResponseDTO.builder()
                    .message(IResponse.INTERNAL_SERVER)
                    .objectResponse(null)
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String resetPasswordRequest(PasswordRequestUtil passwordRequestUtil, HttpServletRequest servletRequest)
            throws MessagingException, UnsupportedEncodingException {
        Optional<UserEntity> user = findByEmail(passwordRequestUtil.getEmail());
        String passwordResetUrl = "";
        if (user.isPresent()) {
            String passwordResetToken = tokenGenerator.generateToken();
            createPasswordResetTokenForUser(user.get(), passwordResetToken);

            passwordResetUrl = passwordResetEmailLink(user.get(), applicationUrl(servletRequest), passwordResetToken);
        }
        return passwordResetUrl;
    }

    public String resetPassword(PasswordRequestUtil passwordRequestUtil, String token) {
        String tokenVerificationResult = validatePasswordResetToken(token);
        if (!tokenVerificationResult.equalsIgnoreCase("valid")) {
            return "Invalid password reset token";
        }

        if (passwordResetTokenService.isPasswordResetTokenUsed(token)) {
            return "The password reset token has already been used";
        }

        Optional<UserEntity> theUser = findUserByPasswordToken(token);
        if (theUser.isPresent()) {
            UserEntity userEntity = theUser.get();
            changePassword(userEntity, passwordRequestUtil.getNewPassword());

            PasswordResetTokenEntity passwordResetToken = passwordResetTokenService.findPasswordResetToken(token);
            passwordResetToken.setUsed(true);
            passwordResetTokenRepository.save(passwordResetToken);

            return "Password has been reset successfully";
        }

        return "Invalid password reset token";
    }

    private String passwordResetEmailLink(UserEntity user, String applicationUrl, String passwordToken)
            throws MessagingException, UnsupportedEncodingException {
        String url = applicationUrl + "/register/reset-password?token=" + passwordToken;

        RegistrationCompleteEventListener eventListener = new RegistrationCompleteEventListener(mailSender);
        eventListener.setTheUser(user);
        eventListener.sendPasswordResetVerificationEmail(url);

        log.info("Click the link to reset your password: {}", url);
        return url;
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":"
                + request.getServerPort() + request.getContextPath();
    }

    public void changePassword(UserEntity theUser, String newPassword) {
        theUser.setPassword(passwordEncoder.encode(newPassword));
        iUserRepository.save(theUser);
    }

    @Override
    public void createPasswordResetTokenForUser(UserEntity user, String passwordResetToken) {
        passwordResetTokenService.createPasswordResetTokenForUser(user, passwordResetToken);
    }


    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return iUserRepository.findByEmail(email);
    }

    @Override
    public String validatePasswordResetToken(String token) {
        return passwordResetTokenService.validatePasswordResetToken(token);
    }

    @Override
    public Optional<UserEntity> findUserByPasswordToken(String token) {
        return passwordResetTokenService.findUserByPasswordToken(token);
    }
}
