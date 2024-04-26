package com.crmmarketingdigitalback2024.webApi.user;

import com.crmmarketingdigitalback2024.commons.constants.endPoints.user.IUserEndPoint;
import com.crmmarketingdigitalback2024.commons.constants.response.IResponse;
import com.crmmarketingdigitalback2024.commons.constants.response.user.IUserResponse;
import com.crmmarketingdigitalback2024.commons.dto.GenericResponseDTO;
import com.crmmarketingdigitalback2024.commons.dto.user.UserDto;
import com.crmmarketingdigitalback2024.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(IUserEndPoint.USER_BASE_URL)
@Tag(name = "Sistema de Gestión de Usuario", description = "Crear, eliminar y actualizar Usuario")
@Log4j2
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
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
}
