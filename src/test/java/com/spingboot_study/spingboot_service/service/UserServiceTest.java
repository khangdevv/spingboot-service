package com.spingboot_study.spingboot_service.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.time.LocalDate;
import java.util.Optional;

import com.spingboot_study.spingboot_service.dto.request.UserCreationRequest;
import com.spingboot_study.spingboot_service.dto.response.UserResponse;
import com.spingboot_study.spingboot_service.entity.User;
import com.spingboot_study.spingboot_service.exception.AppException;
import com.spingboot_study.spingboot_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
@ActiveProfiles("test")
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    private UserCreationRequest userCreationRequest;
    private UserResponse userResponse;
    private User user;

    @BeforeEach
    void initData() {
        userCreationRequest = UserCreationRequest.builder()
                .username("testuser1")
                .firstName("testuser1")
                .lastName("testuser1")
                .password("12345678")
                .dob(LocalDate.of(1990, 1, 1))
                .build();

        userResponse = UserResponse.builder()
                .id("9c7d3dff0b5f")
                .username("testuser1")
                .firstName("testuser1")
                .lastName("testuser1")
                .build();

        user = User.builder()
                .id("9c7d3dff0b5f")
                .username("testuser1")
                .firstName("testuser1")
                .lastName("testuser1")
                .password("12345678")
                .dob(LocalDate.of(1990, 1, 1))
                .build();
    }

    @Test
    // Create User - Success
    void createUser_validRequest_success() throws Exception {
        // given
        Mockito.when(userRepository.existsByUsername(anyString())).thenReturn(false);
        Mockito.when(userRepository.save(any())).thenReturn(user);

        // when
        var response = userService.createUser(userCreationRequest);

        // then
        Assertions.assertThat(response.getId()).isEqualTo("9c7d3dff0b5f");
        Assertions.assertThat(response.getUsername()).isEqualTo("testuser1");
        Assertions.assertThat(response.getFirstName()).isEqualTo("testuser1");
        Assertions.assertThat(response.getLastName()).isEqualTo("testuser1");
    }

    @Test
    // Create User - Success
    void createUser_userExisted_fail() throws Exception {
        // given
        Mockito.when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // when
        var exception = assertThrows(AppException.class, () -> {
            userService.createUser(userCreationRequest);
        });
        Assertions.assertThat(exception.getMessage()).isEqualTo("User already exists with this username");
    }

    @Test
    @WithMockUser(username = "testuser1") // co the them roles = {"USER"}) neu muon test role
    // Get My Info - Success
    void getMyInfo_valid_success() throws Exception {
        Mockito.when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        var response = userService.getMyInfo();

        Assertions.assertThat(response.getUsername()).isEqualTo("testuser1");
        Assertions.assertThat(response.getId()).isEqualTo("9c7d3dff0b5f");
    }

    @Test
    @WithMockUser(username = "testuser") // co the them roles = {"USER"}) neu muon test role
    // Get My Info - Fail
    void getMyInfo_userNotFound_fail() throws Exception {
        Mockito.when(userRepository.findByUsername(anyString())).thenReturn(Optional.ofNullable(null));

        var response = assertThrows(AppException.class, () -> {
            userService.getMyInfo();
        });

        Assertions.assertThat(response.getErrorCode().getCode()).isEqualTo(1008);
    }
}
