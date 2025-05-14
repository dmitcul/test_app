package org.example.tech_spec_java_spring_final_v2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tech_spec_java_spring_final_v2.dto.UserDto;
import org.example.tech_spec_java_spring_final_v2.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(UserControllerTest.TestConfig.class)
class UserControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private UserDto userDto;
    private final Long userId = 1L;
    private final String userName = "Test User";
    private final String userEmail = "test.user@example.com";

    @BeforeEach
    void setUp() {
        reset(userService);
        userDto = new UserDto(userId, userName, userEmail);
    }

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        // Arrange
        UserDto inputDto = new UserDto(null, userName, userEmail);
        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

        // Act & Assert
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId.intValue())))
                .andExpect(jsonPath("$.name", is(userName)))
                .andExpect(jsonPath("$.email", is(userEmail)));

        verify(userService, times(1)).createUser(any(UserDto.class));
    }

    @Test
    void getUser_WithExistingId_ShouldReturnUser() throws Exception {
        // Arrange
        when(userService.getUser(userId)).thenReturn(userDto);

        // Act & Assert
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId.intValue())))
                .andExpect(jsonPath("$.name", is(userName)))
                .andExpect(jsonPath("$.email", is(userEmail)));

        verify(userService, times(1)).getUser(userId);
    }

    @Test
    void getUser_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(userService.getUser(userId)).thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUser(userId);
    }

    @Test
    void updateUser_WithExistingId_ShouldReturnUpdatedUser() throws Exception {
        // Arrange
        UserDto updateDto = new UserDto(userId, "Updated Name", "updated.email@example.com");
        UserDto updatedUser = new UserDto(userId, "Updated Name", "updated.email@example.com");

        when(userService.updateUser(eq(userId), any(UserDto.class))).thenReturn(updatedUser);

        // Act & Assert
        mockMvc.perform(put("/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId.intValue())))
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.email", is("updated.email@example.com")));

        verify(userService, times(1)).updateUser(eq(userId), any(UserDto.class));
    }

    @Test
    void updateUser_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Arrange
        UserDto updateDto = new UserDto(userId, "Updated Name", "updated.email@example.com");

        when(userService.updateUser(eq(userId), any(UserDto.class)))
                .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        mockMvc.perform(put("/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updateUser(eq(userId), any(UserDto.class));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(userService).deleteUser(userId);

        // Act & Assert
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(userId);
    }
}
