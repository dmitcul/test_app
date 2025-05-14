package org.example.tech_spec_java_spring_final_v2.service;

import org.example.tech_spec_java_spring_final_v2.dto.UserDto;
import org.example.tech_spec_java_spring_final_v2.entity.UserEntity;
import org.example.tech_spec_java_spring_final_v2.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private UserDto testUserDto;
    private final String userName = "Integration Test User";
    private final String userEmail = "integration.test@example.com";

    @BeforeEach
    void setUp() {
        testUserDto = new UserDto(null, userName, userEmail);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_ShouldPersistUserAndReturnDto() {
        // Act
        UserDto createdUser = userService.createUser(testUserDto);

        // Assert
        assertNotNull(createdUser.id());
        assertEquals(userName, createdUser.name());
        assertEquals(userEmail, createdUser.email());

        // Verify user was persisted
        assertTrue(userRepository.findById(createdUser.id()).isPresent());
    }

    @Test
    void getUser_WithExistingId_ShouldReturnCorrectUser() {
        // Arrange
        UserEntity savedEntity = userRepository.save(UserEntity.builder()
                .name(userName)
                .email(userEmail)
                .build());

        // Act
        UserDto retrievedUser = userService.getUser(savedEntity.getId());

        // Assert
        assertEquals(savedEntity.getId(), retrievedUser.id());
        assertEquals(savedEntity.getName(), retrievedUser.name());
        assertEquals(savedEntity.getEmail(), retrievedUser.email());
    }

    @Test
    void getUser_WithNonExistingId_ShouldThrowException() {
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.getUser(999L));
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void updateUser_WithExistingId_ShouldUpdateUserAndReturnDto() {
        // Arrange
        UserEntity savedEntity = userRepository.save(UserEntity.builder()
                .name(userName)
                .email(userEmail)
                .build());

        UserDto updateDto = new UserDto(
                savedEntity.getId(),
                "Updated Name",
                "updated.email@example.com"
        );

        // Act
        UserDto updatedUser = userService.updateUser(savedEntity.getId(), updateDto);

        // Assert
        assertEquals(savedEntity.getId(), updatedUser.id());
        assertEquals("Updated Name", updatedUser.name());
        assertEquals("updated.email@example.com", updatedUser.email());

        // Verify changes were persisted
        UserEntity updatedEntity = userRepository.findById(savedEntity.getId()).orElseThrow();
        assertEquals("Updated Name", updatedEntity.getName());
        assertEquals("updated.email@example.com", updatedEntity.getEmail());
    }

    @Test
    void updateUser_WithNonExistingId_ShouldThrowException() {
        // Arrange
        UserDto updateDto = new UserDto(999L, "Updated Name", "updated.email@example.com");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.updateUser(999L, updateDto));
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void deleteUser_WithExistingId_ShouldRemoveUser() {
        // Arrange
        UserEntity savedEntity = userRepository.save(UserEntity.builder()
                .name(userName)
                .email(userEmail)
                .build());
        Long userId = savedEntity.getId();

        // Act
        userService.deleteUser(userId);

        // Assert
        assertFalse(userRepository.existsById(userId));
    }

    @Test
    void deleteUser_WithNonExistingId_ShouldNotThrowException() {
        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> userService.deleteUser(999L));
    }
}