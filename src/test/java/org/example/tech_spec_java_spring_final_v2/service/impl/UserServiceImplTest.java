package org.example.tech_spec_java_spring_final_v2.service.impl;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.example.tech_spec_java_spring_final_v2.dto.UserDto;
import org.example.tech_spec_java_spring_final_v2.entity.UserEntity;
import org.example.tech_spec_java_spring_final_v2.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.util.List;
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

    private Logger logger;
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(userId, userName, userEmail);
        userEntity = UserEntity.builder()
                .id(userId)
                .name(userName)
                .email(userEmail)
                .build();

        // Set up logger and appender for capturing log messages
        logger = (Logger) LoggerFactory.getLogger(UserServiceImpl.class);
        logger.setLevel(Level.DEBUG); // Explicitly set log level to DEBUG
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        // Remove appender after test
        logger.detachAppender(listAppender);
    }

    @Test
    void createUser_ShouldReturnUserDtoAndLogMessages() {
        // Arrange
        UserDto inputDto = new UserDto(null, userName, userEmail);
        UserEntity savedEntity = UserEntity.builder()
                .id(userId)
                .name(userName)
                .email(userEmail)
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedEntity);

        // Act
        UserDto result = userService.createUser(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals(userName, result.name());
        assertEquals(userEmail, result.email());
        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(userRepository, times(1)).save(any(UserEntity.class));

        // Verify log messages
        List<ILoggingEvent> logsList = listAppender.list;

        // Should have at least 3 log messages (info at start, debug during save, info at end)
        assertTrue(logsList.size() >= 3);

        // Verify info log at the beginning
        assertEquals(Level.INFO, logsList.get(0).getLevel());
        assertEquals("Creating new user with name: {}, email: {}", logsList.get(0).getMessage());
        assertEquals(2, logsList.get(0).getArgumentArray().length);
        assertEquals(userName, logsList.get(0).getArgumentArray()[0]);
        assertEquals(userEmail, logsList.get(0).getArgumentArray()[1]);

        // Verify debug log during save
        boolean hasDebugLog = false;
        for (ILoggingEvent event : logsList) {
            if (event.getLevel() == Level.DEBUG && event.getMessage().contains("Saving new user")) {
                hasDebugLog = true;
                break;
            }
        }
        assertTrue(hasDebugLog, "Debug log message about saving user not found");

        // Verify info log at the end
        boolean hasSuccessLog = false;
        for (ILoggingEvent event : logsList) {
            if (event.getLevel() == Level.INFO && event.getMessage().contains("User created successfully")) {
                hasSuccessLog = true;
                break;
            }
        }
        assertTrue(hasSuccessLog, "Success log message not found");
    }

    @Test
    void createUser_WithExistingEmail_ShouldThrowExceptionAndLogError() {
        // Arrange
        UserDto inputDto = new UserDto(null, "New User", userEmail);
        UserEntity existingUser = UserEntity.builder()
                .id(2L)
                .name("Existing User")
                .email(userEmail)
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(existingUser));

        // Clear previous logs
        listAppender.list.clear();

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(inputDto);
        });

        assertEquals("User with email " + userEmail + " already exists", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(userRepository, never()).save(any(UserEntity.class));

        // Verify log messages
        List<ILoggingEvent> logsList = listAppender.list;

        // Should have at least 2 log messages (info at start, error when email exists)
        assertTrue(logsList.size() >= 2);

        // Verify info log at the beginning
        assertEquals(Level.INFO, logsList.get(0).getLevel());
        assertEquals("Creating new user with name: {}, email: {}", logsList.get(0).getMessage());

        // Verify error log when email exists
        boolean hasErrorLog = false;
        for (ILoggingEvent event : logsList) {
            if (event.getLevel() == Level.ERROR && event.getMessage().contains("User with email {} already exists")) {
                hasErrorLog = true;
                break;
            }
        }
        assertTrue(hasErrorLog, "Error log message about existing email not found");
    }

    @Test
    void getUser_WhenUserExists_ShouldReturnUserDtoAndLogMessages() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        // Clear previous logs
        listAppender.list.clear();

        // Act
        UserDto result = userService.getUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals(userName, result.name());
        assertEquals(userEmail, result.email());
        verify(userRepository, times(1)).findById(userId);

        // Verify log messages
        List<ILoggingEvent> logsList = listAppender.list;

        // Should have at least 3 log messages (info at start, debug when found, info at end)
        assertTrue(logsList.size() >= 3);

        // Verify info log at the beginning
        assertEquals(Level.INFO, logsList.get(0).getLevel());
        assertTrue(logsList.get(0).getMessage().contains("Getting user with id"));

        // Verify debug log when user is found
        boolean hasDebugLog = false;
        for (ILoggingEvent event : logsList) {
            if (event.getLevel() == Level.DEBUG && event.getMessage().contains("User found")) {
                hasDebugLog = true;
                break;
            }
        }
        assertTrue(hasDebugLog, "Debug log message about user found not found");

        // Verify info log at the end
        boolean hasSuccessLog = false;
        for (ILoggingEvent event : logsList) {
            if (event.getLevel() == Level.INFO && event.getMessage().contains("Successfully retrieved user")) {
                hasSuccessLog = true;
                break;
            }
        }
        assertTrue(hasSuccessLog, "Success log message not found");
    }

    @Test
    void getUser_WhenUserDoesNotExist_ShouldThrowExceptionAndLogError() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Clear previous logs
        listAppender.list.clear();

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUser(userId));
        assertEquals("User with id " + userId + " not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);

        // Verify log messages
        List<ILoggingEvent> logsList = listAppender.list;

        // Should have at least 2 log messages (info at start, error when not found)
        assertTrue(logsList.size() >= 2);

        // Verify info log at the beginning
        assertEquals(Level.INFO, logsList.get(0).getLevel());
        assertTrue(logsList.get(0).getMessage().contains("Getting user with id"));

        // Verify error log when user is not found
        boolean hasErrorLog = false;
        for (ILoggingEvent event : logsList) {
            if (event.getLevel() == Level.ERROR && event.getMessage().contains("User with id") && 
                event.getMessage().contains("not found")) {
                hasErrorLog = true;
                break;
            }
        }
        assertTrue(hasErrorLog, "Error log message about user not found not found");
    }

    @Test
    void updateUser_WhenUserExists_ShouldReturnUpdatedUserDtoAndLogMessages() {
        // Arrange
        String newEmail = "updated.email@example.com";
        UserDto updateDto = new UserDto(userId, "Updated Name", newEmail);
        UserEntity updatedEntity = UserEntity.builder()
                .id(userId)
                .name("Updated Name")
                .email(newEmail)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepository.findByEmail(newEmail)).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(updatedEntity);

        // Clear previous logs
        listAppender.list.clear();

        // Act
        UserDto result = userService.updateUser(userId, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals("Updated Name", result.name());
        assertEquals(newEmail, result.email());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findByEmail(newEmail);
        verify(userRepository, times(1)).save(any(UserEntity.class));

        // Verify log messages
        List<ILoggingEvent> logsList = listAppender.list;

        // Should have at least 4 log messages (info at start, debug when found, debug during save, info at end)
        assertTrue(logsList.size() >= 4);

        // Verify info log at the beginning
        assertEquals(Level.INFO, logsList.get(0).getLevel());
        assertTrue(logsList.get(0).getMessage().contains("Updating user with id"));

        // Verify debug log when user is found
        boolean hasFoundDebugLog = false;
        for (ILoggingEvent event : logsList) {
            if (event.getLevel() == Level.DEBUG && event.getMessage().contains("Found user to update")) {
                hasFoundDebugLog = true;
                break;
            }
        }
        assertTrue(hasFoundDebugLog, "Debug log message about found user not found");

        // Verify debug log during save
        boolean hasSaveDebugLog = false;
        for (ILoggingEvent event : logsList) {
            if (event.getLevel() == Level.DEBUG && event.getMessage().contains("Saving updated user")) {
                hasSaveDebugLog = true;
                break;
            }
        }
        assertTrue(hasSaveDebugLog, "Debug log message about saving user not found");

        // Verify info log at the end
        boolean hasSuccessLog = false;
        for (ILoggingEvent event : logsList) {
            if (event.getLevel() == Level.INFO && event.getMessage().contains("User with id") && 
                event.getMessage().contains("updated successfully")) {
                hasSuccessLog = true;
                break;
            }
        }
        assertTrue(hasSuccessLog, "Success log message not found");
    }

    @Test
    void updateUser_WithExistingEmail_ShouldThrowExceptionAndLogError() {
        // Arrange
        Long existingUserId = 2L;
        String existingEmail = "existing.email@example.com";

        UserDto updateDto = new UserDto(userId, "Updated Name", existingEmail);

        UserEntity userToUpdate = UserEntity.builder()
                .id(userId)
                .name(userName)
                .email(userEmail)
                .build();

        UserEntity existingUser = UserEntity.builder()
                .id(existingUserId)
                .name("Existing User")
                .email(existingEmail)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(userToUpdate));
        when(userRepository.findByEmail(existingEmail)).thenReturn(Optional.of(existingUser));

        // Clear previous logs
        listAppender.list.clear();

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(userId, updateDto);
        });

        assertEquals("Email " + existingEmail + " already in use by another user", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findByEmail(existingEmail);
        verify(userRepository, never()).save(any(UserEntity.class));

        // Verify log messages
        List<ILoggingEvent> logsList = listAppender.list;

        // Should have at least 3 log messages (info at start, debug when found, error when email exists)
        assertTrue(logsList.size() >= 3);

        // Verify info log at the beginning
        assertEquals(Level.INFO, logsList.get(0).getLevel());
        assertTrue(logsList.get(0).getMessage().contains("Updating user with id"));

        // Verify debug log when user is found
        boolean hasFoundDebugLog = false;
        for (ILoggingEvent event : logsList) {
            if (event.getLevel() == Level.DEBUG && event.getMessage().contains("Found user to update")) {
                hasFoundDebugLog = true;
                break;
            }
        }
        assertTrue(hasFoundDebugLog, "Debug log message about found user not found");

        // Verify error log when email exists
        boolean hasErrorLog = false;
        for (ILoggingEvent event : logsList) {
            if (event.getLevel() == Level.ERROR && event.getMessage().contains("Cannot update user. Email {} already in use")) {
                hasErrorLog = true;
                break;
            }
        }
        assertTrue(hasErrorLog, "Error log message about existing email not found");
    }

    @Test
    void updateUser_WhenUserDoesNotExist_ShouldThrowExceptionAndLogError() {
        // Arrange
        UserDto updateDto = new UserDto(userId, "Updated Name", "updated.email@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Clear previous logs
        listAppender.list.clear();

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> userService.updateUser(userId, updateDto));
        assertEquals("User with id " + userId + " not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(UserEntity.class));

        // Verify log messages
        List<ILoggingEvent> logsList = listAppender.list;

        // Should have at least 2 log messages (info at start, error when not found)
        assertTrue(logsList.size() >= 2);

        // Verify info log at the beginning
        assertEquals(Level.INFO, logsList.get(0).getLevel());
        assertTrue(logsList.get(0).getMessage().contains("Updating user with id"));

        // Verify error log when user is not found
        boolean hasErrorLog = false;
        for (ILoggingEvent event : logsList) {
            if (event.getLevel() == Level.ERROR && event.getMessage().contains("User with id") && 
                event.getMessage().contains("not found for update")) {
                hasErrorLog = true;
                break;
            }
        }
        assertTrue(hasErrorLog, "Error log message about user not found not found");
    }

    @Test
    void deleteUser_ShouldCallRepositoryDeleteByIdAndLogMessages() {
        // Arrange
        doNothing().when(userRepository).deleteById(userId);

        // Clear previous logs
        listAppender.list.clear();

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).deleteById(userId);

        // Verify log messages
        List<ILoggingEvent> logsList = listAppender.list;

        // Should have at least 2 log messages (info at start, info at end)
        assertTrue(logsList.size() >= 2);

        // Verify info log at the beginning
        assertEquals(Level.INFO, logsList.get(0).getLevel());
        assertTrue(logsList.get(0).getMessage().contains("Deleting user with id"));

        // Verify info log at the end
        boolean hasSuccessLog = false;
        for (ILoggingEvent event : logsList) {
            if (event.getLevel() == Level.INFO && event.getMessage().contains("User with id") && 
                event.getMessage().contains("deleted successfully")) {
                hasSuccessLog = true;
                break;
            }
        }
        assertTrue(hasSuccessLog, "Success log message not found");
    }

    @Test
    void deleteUser_WhenExceptionOccurs_ShouldLogError() {
        // Arrange
        doThrow(new RuntimeException("Database error")).when(userRepository).deleteById(userId);

        // Clear previous logs
        listAppender.list.clear();

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.deleteUser(userId));
        verify(userRepository, times(1)).deleteById(userId);

        // Verify log messages
        List<ILoggingEvent> logsList = listAppender.list;

        // Should have at least 2 log messages (info at start, error when exception occurs)
        assertTrue(logsList.size() >= 2);

        // Verify info log at the beginning
        assertEquals(Level.INFO, logsList.get(0).getLevel());
        assertTrue(logsList.get(0).getMessage().contains("Deleting user with id"));

        // Verify error log when exception occurs
        boolean hasErrorLog = false;
        for (ILoggingEvent event : logsList) {
            if (event.getLevel() == Level.ERROR && event.getMessage().contains("Error deleting user with id")) {
                hasErrorLog = true;
                break;
            }
        }
        assertTrue(hasErrorLog, "Error log message not found");
    }
}
