package com.crmmarketingdigitalback2024.webApi.user;

import com.crmmarketingdigitalback2024.commons.constants.endPoints.user.IUserEndPoint;
import com.crmmarketingdigitalback2024.commons.dto.GenericResponseDTO;
import com.crmmarketingdigitalback2024.service.user.PasswordResetTokenService;
import com.crmmarketingdigitalback2024.service.user.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordResetTokenService passwordResetTokenService;



//    @BeforeEach
//    public void setup() {
//        mvc = MockMvcBuilders.standaloneSetup(apiController)
//                .setControllerAdvice(new ErrorResponseController())
//                .build();
//    }
    @Test
    public void testDeleteUser_Success() throws Exception {
        // Given
        Integer userId = 1;
        GenericResponseDTO responseDTO = new GenericResponseDTO("User deleted successfully", true);
        given(userService.deleteUser(userId)).willReturn(ResponseEntity.ok(responseDTO));

        // When & Then
        mockMvc.perform(delete(IUserEndPoint.USER_DELETE, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"))
                .andExpect(jsonPath("$.success").value(true));

        // Verify
        verify(userService).deleteUser(userId);
    }

//    @Test
//    public void testResetPasswordRequest() throws Exception {
//        // Given
//        PasswordRequestUtil passwordRequestUtil = new PasswordRequestUtil();
//        passwordRequestUtil.setEmail("user@example.com");
//        UserEntity userEntity = new UserEntity();
//        userEntity.setEmail("user@example.com");
//
//        when(userService.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
//        // You would also need to mock the behavior of other methods called within this method
//
//        // When & Then
//        mockMvc.perform(post(IUserEndPoint.USER_PASSWORD_RESET_REQUEST)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"email\": \"user@example.com\"}"))
//                .andExpect(status().isOk());
//
//    }

}