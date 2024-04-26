package com.crmmarketingdigitalback2024.commons.converter.user;

import com.crmmarketingdigitalback2024.commons.dto.user.UserDto;
import com.crmmarketingdigitalback2024.commons.helperMapper.HelperMapper;
import com.crmmarketingdigitalback2024.model.UserEntity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Log4j2
@Component
@AllArgsConstructor
public class UserConverter {

    PasswordEncoder passwordEncoder;

    public UserDto convertUserEntityToUserDTO(UserEntity userEntity) {
        UserDto userDTO = new UserDto();
        try {
            userDTO = HelperMapper.modelMapper().map(userEntity, UserDto.class);
            byte[] decodedStringByte = Base64.getDecoder().decode(userEntity.getPassword());
            String decodedString = new String(decodedStringByte);
            userDTO.setPasswordUser(decodedString);
        } catch (Exception e) {
            log.error("GeneralResponse.DOCUMENT_FAIL + e");
        }
        return userDTO;
    }

    public UserEntity convertUserDTOToUserEntity(UserDto userDTO) {
        UserEntity userEntity = new UserEntity();
        try {
            userEntity = HelperMapper.modelMapper().map(userDTO, UserEntity.class);
            userEntity.setPassword( passwordEncoder.encode(userDTO.getPasswordUser()));
        } catch (Exception e) {
            log.error("GeneralResponse.DOCUMENT_FAIL + e");
        }
        return userEntity;
    }
}
