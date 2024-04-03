package com.crmmarketingdigitalback2024.service.Login;

import com.crmmarketingdigitalback2024.commons.dto.login.LoginRequest;
import com.crmmarketingdigitalback2024.commons.dto.login.LoginResponse;
import com.crmmarketingdigitalback2024.model.UserEntity.UserEntity;
import com.crmmarketingdigitalback2024.repository.user.IUserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class LoginService {

    private final IUserRepository repository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserEntity register (UserEntity user){
        user.setName(user.getName());
        user.setEmail(user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(user.getRole());
        return repository.save(user);
    }

    public LoginResponse login(LoginRequest userRequest) {

        verifyEmailStructure(userRequest.getEmail());
        verifyPasswordStructure(userRequest.getPassword());

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userRequest.getEmail(), userRequest.getPassword());
        authenticationManager.authenticate(authToken);

        UserEntity userEntity = repository.findByEmail(userRequest.getEmail()).get();

        String jwt = jwtService.generateToken(userEntity, generateClaims(userEntity));

        return new LoginResponse(jwt);
    }

    private Map<String, Object> generateClaims (UserEntity user){
        Map<String,Object> claims = new HashMap<>();
        claims.put("name", user.getName());
        claims.put("role", user.getRole().getName());
        return claims;
    }


    public UserEntity getUsers (UserEntity user){
        return repository.findByEmailAndPassword(user.getEmail(),user.getPassword()).orElseThrow(
                ()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario o contraseña incorrectos")
        );
    }

    public List<UserEntity> getAllUsers() {
        return repository.findAll();
    }


    private void verifyEmailStructure(String email) throws RuntimeException {
        String regex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email no válido");
        }
    }

    private void verifyPasswordStructure(String password) throws RuntimeException {
        String regex = "^\\S{4,100}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        if (!matcher.matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contraseña no válida");
        }
    }

}
