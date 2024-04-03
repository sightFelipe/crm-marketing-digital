package com.crmmarketingdigitalback2024.webApi.LoginController;

import com.crmmarketingdigitalback2024.commons.constants.endPoints.IEndPoints;
import com.crmmarketingdigitalback2024.commons.dto.login.LoginRequest;
import com.crmmarketingdigitalback2024.commons.dto.login.LoginResponse;
import com.crmmarketingdigitalback2024.model.UserEntity.UserEntity;
import com.crmmarketingdigitalback2024.service.Login.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(IEndPoints.LOGIN)
public class LoginController {

    LoginService service;

    public LoginController(LoginService service){
        this.service=service;
    }


    @PreAuthorize("permitAll")
    @PostMapping
    public ResponseEntity<LoginResponse> login (@RequestBody LoginRequest request){
        LoginResponse jwtDto = service.login(request);
        return new ResponseEntity<> (jwtDto, HttpStatus.OK);
    }

    @PreAuthorize("permitAll")
    @GetMapping("/getUser")
    public ResponseEntity<UserEntity> getUser( @RequestBody UserEntity user){
        return new ResponseEntity<>(service.getUsers(user), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAll")
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        List<UserEntity> users = service.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PreAuthorize("permitAll")
    @PostMapping("/register")
     public  ResponseEntity<?> register (@RequestBody UserEntity user){
        return new ResponseEntity<>(service.register(user), HttpStatus.OK);
    }


}



