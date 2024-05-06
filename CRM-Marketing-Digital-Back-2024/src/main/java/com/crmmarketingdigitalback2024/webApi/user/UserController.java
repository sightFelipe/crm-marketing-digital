package com.crmmarketingdigitalback2024.webApi.user;

import com.crmmarketingdigitalback2024.commons.constants.endPoints.user.IUserEndPoint;
import com.crmmarketingdigitalback2024.commons.constants.response.IResponse;
import com.crmmarketingdigitalback2024.commons.constants.response.user.IUserResponse;
import com.crmmarketingdigitalback2024.commons.dto.GenericResponseDTO;
import com.crmmarketingdigitalback2024.commons.dto.user.UserDto;
import com.crmmarketingdigitalback2024.commons.listener.RegistrationCompleteEventListener;
import com.crmmarketingdigitalback2024.commons.util.PasswordRequestUtil;
import com.crmmarketingdigitalback2024.model.UserEntity.PasswordResetTokenEntity;
import com.crmmarketingdigitalback2024.model.UserEntity.UserEntity;
import com.crmmarketingdigitalback2024.repository.user.PasswordResetTokenRepository;
import com.crmmarketingdigitalback2024.service.user.PasswordResetTokenService;
import com.crmmarketingdigitalback2024.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(IUserEndPoint.USER_BASE_URL)
@Tag(name = "Sistema de Gestión de Usuario", description = "Crear, eliminar y actualizar Usuario")
@Log4j2
public class UserController {

    @Autowired
    private JavaMailSender mailSender;
    private final UserService userService;

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordResetTokenService passwordResetTokenService;
    private final RegistrationCompleteEventListener eventListener;

    public UserController(UserService userService, PasswordResetTokenRepository passwordResetTokenRepository, PasswordResetTokenService passwordResetTokenService, RegistrationCompleteEventListener eventListener) {
        this.userService = userService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordResetTokenService = passwordResetTokenService;
        this.eventListener = eventListener;
    }
    @Operation(summary = "Autenticacion de Usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode  = "200", description = IUserResponse.AUTENTIFICATION_SUCESS,
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = IResponse.class))}),
            @ApiResponse(responseCode  = "400", description = IUserResponse.AUTENTIFICACION_FAIL,
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode  = "404", description = IResponse.NOT_FOUND,
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode  = "500", description = IResponse.INTERNAL_SERVER,
                    content = {@Content(mediaType = "application/json")})})
    @PostMapping(IUserEndPoint.USER_SERVICE)
    public ResponseEntity<GenericResponseDTO> serviceUser(@RequestBody UserDto userDTO) {
        return this.userService.serviceUser(userDTO);
    }
    @Operation(summary = "Crear un nuevo Usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode  = "200", description = IResponse.CREATE_SUCCESS,
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericResponseDTO.class))}),
            @ApiResponse(responseCode  = "400", description = IResponse.CREATE_FAIL,
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode  = "404", description = IResponse.NOT_FOUND,
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode  = "500", description = IResponse.INTERNAL_SERVER,
                    content = {@Content(mediaType = "application/json")})})
    @PostMapping(IUserEndPoint.USER_CRATE)
    public ResponseEntity<GenericResponseDTO> createUser(@RequestBody UserDto userDTO) {
        System.out.println("entro al controlador");
        return userService.createUser(userDTO);
    }
    @Operation(summary = "Actualizar un Usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode  = "200", description = IResponse.CREATE_SUCCESS,
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericResponseDTO.class))}),
            @ApiResponse(responseCode  = "400", description = IResponse.CREATE_FAIL,
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode  = "404", description = IResponse.NOT_FOUND,
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode  = "500", description = IResponse.INTERNAL_SERVER,
                    content = {@Content(mediaType = "application/json")})})
    @PutMapping(IUserEndPoint.USER_UPDATE)
    public ResponseEntity<GenericResponseDTO> updateUser(@RequestBody UserDto userDTO) {
        return this.userService.updateUser(userDTO);
    }
    @Operation(summary = "Eliminar un Usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode  = "200", description = IResponse.CREATE_SUCCESS,
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericResponseDTO.class))}),
            @ApiResponse(responseCode  = "400", description = IResponse.CREATE_FAIL,
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode  = "404", description = IResponse.NOT_FOUND,
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode  = "500", description = IResponse.INTERNAL_SERVER,
                    content = {@Content(mediaType = "application/json")})})
    @DeleteMapping(IUserEndPoint.USER_DELETE)
    public ResponseEntity<GenericResponseDTO> deleteUser(@PathVariable Integer userId) {
        return this.userService.deleteUser(userId);
    }


    @PostMapping(IUserEndPoint.USER_PASSWORD_RESET_REQUEST)
    public String resetPasswordRequest(@RequestBody PasswordRequestUtil passwordRequestUtil,
                                       final HttpServletRequest servletRequest)
            throws MessagingException, UnsupportedEncodingException {

        Optional<UserEntity> user = userService.findByEmail(passwordRequestUtil.getEmail());
        String passwordResetUrl = "";
        if (user.isPresent()) {
            String passwordResetToken = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user.get(), passwordResetToken);

            passwordResetUrl = passwordResetEmailLink(user.get(), applicationUrl(servletRequest), passwordResetToken, mailSender);
        }
        return passwordResetUrl;
    }

    @PostMapping(IUserEndPoint.USER_PASSWORD_RESET)
    public String resetPassword(@RequestBody PasswordRequestUtil passwordRequestUtil,
                                @RequestParam("token") String token) {
        String tokenVerificationResult = userService.validatePasswordResetToken(token);
        if (!tokenVerificationResult.equalsIgnoreCase("valid")) {
            return "Invalid password reset token";
        }

        if (passwordResetTokenService.isPasswordResetTokenUsed(token)) {
            return "The password reset token has already been used";
        }

        Optional<UserEntity> theUser = userService.findUserByPasswordToken(token);
        if (theUser.isPresent()) {
            userService.changePassword(theUser.get(), passwordRequestUtil.getNewPassword());

            PasswordResetTokenEntity passwordResetToken = passwordResetTokenService.findPasswordResetToken(token);
            passwordResetToken.setUsed(true);
            passwordResetTokenRepository.save(passwordResetToken);

            return "Password has been reset successfully";
        }

        return "Invalid password reset token";
    }

    private String passwordResetEmailLink(UserEntity user, String applicationUrl, String passwordToken, JavaMailSender mailSender)
            throws MessagingException, UnsupportedEncodingException {
        String url = applicationUrl + "/register/reset-password?token=" + passwordToken;

        RegistrationCompleteEventListener eventListener = new RegistrationCompleteEventListener(mailSender);
        eventListener.setTheUser(user);
        eventListener.sendPasswordResetVerificationEmail(url);

        log.info("Click the link to reset your password: {}", url);
        return url;
    }

    public String applicationUrl(HttpServletRequest request) {
        return "http://"+request.getServerName()+":"
                +request.getServerPort()+request.getContextPath();
    }
}
