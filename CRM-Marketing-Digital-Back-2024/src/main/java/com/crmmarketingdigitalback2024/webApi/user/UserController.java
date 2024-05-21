package com.crmmarketingdigitalback2024.webApi.user;

import com.crmmarketingdigitalback2024.commons.constants.endPoints.user.IUserEndPoint;
import com.crmmarketingdigitalback2024.commons.constants.response.IResponse;
import com.crmmarketingdigitalback2024.commons.constants.response.user.IUserResponse;
import com.crmmarketingdigitalback2024.commons.dto.GenericResponseDTO;
import com.crmmarketingdigitalback2024.commons.dto.user.UserDto;
import com.crmmarketingdigitalback2024.commons.listener.RegistrationCompleteEventListener;
import com.crmmarketingdigitalback2024.commons.util.PasswordRequestUtil;
import com.crmmarketingdigitalback2024.exception.UserNotFoundException;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;


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


    @Operation(summary = "Solicitar Restablecimiento de Contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode  = "200", description = "Solicitud de restablecimiento de contraseña enviada exitosamente"),
            @ApiResponse(responseCode  = "400", description = "Error en la solicitud de restablecimiento de contraseña",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))})})
    @PostMapping(IUserEndPoint.USER_PASSWORD_RESET_REQUEST)
    public String resetPasswordRequest(@RequestBody PasswordRequestUtil passwordRequestUtil,
                                       final HttpServletRequest servletRequest)
            throws MessagingException, UnsupportedEncodingException {
        return userService.resetPasswordRequest(passwordRequestUtil,(servletRequest));
    }

    @Operation(summary = "Restablecer Contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode  = "200", description = "Contraseña restablecida exitosamente"),
            @ApiResponse(responseCode  = "400", description = "Error al restablecer la contraseña",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))})})
    @PostMapping(IUserEndPoint.USER_PASSWORD_RESET)
    public String resetPassword(@RequestBody PasswordRequestUtil passwordRequestUtil,
                                @RequestParam("token") String token) {
        return userService.resetPassword(passwordRequestUtil, token);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(IUserEndPoint.ENABLED_USERS_URL)
    public List<UserEntity> getActiveUsers() {
        return userService.getActiveUsers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(IUserEndPoint.DISABLED_USERS_URL)
    public List<UserEntity> getDisabledUsers() {
        return userService.getDisabledUsers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(IUserEndPoint.TOGGLE_USER_STATUS)
    public ResponseEntity<String> toggleUserStatus(@PathVariable Long id) throws UserNotFoundException {
        userService.toggleUserStatus(id);
        return ResponseEntity.ok("El cambio de estado ha sido realizado con éxito.");
    }

}
