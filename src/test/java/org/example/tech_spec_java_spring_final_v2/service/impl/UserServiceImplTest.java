package org.example.tech_spec_java_spring_final_v2.service.impl;

import org.example.tech_spec_java_spring_final_v2.dto.UserDto;
import org.example.tech_spec_java_spring_final_v2.entity.UserEntity;
import org.example.tech_spec_java_spring_final_v2.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto;
    private UserEntity userEntity;
    private final Long userId = 1L;
    private final String userName = "John Doe";
    private final String userEmail = "john.doe@example.com";

    @BeforeEach
    void setUp() {
        userDto = new UserDto(userId, userName, userEmail);
        userEntity = UserEntity.builder()
                .id(userId)
                .name(userName)
                .email(userEmail)
                .build();
    }

    @Test
    void createUser_ShouldReturnUserDto() {
        // Arrange
        UserDto inputDto = new UserDto(null, userName, userEmail);
        UserEntity savedEntity = UserEntity.builder()
                .id(userId)
                .name(userName)
                .email(userEmail)
                .build();
        
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedEntity);

        // Act
        UserDto result = userService.createUser(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals(userName, result.name());
        assertEquals(userEmail, result.email());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void getUser_WhenUserExists_ShouldReturnUserDto() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        // Act
        UserDto result = userService.getUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals(userName, result.name());
        assertEquals(userEmail, result.email());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUser_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUser(userId));
        assertEquals("User with id " + userId + " not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void updateUser_WhenUserExists_ShouldReturnUpdatedUserDto() {
        // Arrange
        UserDto updateDto = new UserDto(userId, "Updated Name", "updated.email@example.com");
        UserEntity updatedEntity = UserEntity.builder()
                .id(userId)
                .name("Updated Name")
                .email("updated.email@example.com")
                .build();
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(updatedEntity);

        // Act
        UserDto result = userService.updateUser(userId, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals("Updated Name", result.name());
        assertEquals("updated.email@example.com", result.email());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void updateUser_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        UserDto updateDto = new UserDto(userId, "Updated Name", "updated.email@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> userService.updateUser(userId, updateDto));
        assertEquals("User with id " + userId + " not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void deleteUser_ShouldCallRepositoryDeleteById() {
        // Arrange
        doNothing().when(userRepository).deleteById(userId);

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).deleteById(userId);
    }
}