package org.example.tech_spec_java_spring_final_v2.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.tech_spec_java_spring_final_v2.dto.UserDto;
import org.example.tech_spec_java_spring_final_v2.entity.UserEntity;
import org.example.tech_spec_java_spring_final_v2.repository.UserRepository;
import org.example.tech_spec_java_spring_final_v2.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto dto) {
        logger.info("Creating new user with name: {}, email: {}", dto.name(), dto.email());

        UserEntity user = UserEntity.builder()
                .name(dto.name())
                .email(dto.email())
                .build();

        logger.debug("Saving new user to database");
        user = userRepository.save(user);

        UserDto result = toDto(user);
        logger.info("User created successfully with id: {}", result.id());
        return result;
    }

    @Override
    public UserDto getUser(Long id) {
        logger.info("Getting user with id: {}", id);

        try {
            UserDto result = userRepository.findById(id)
                    .map(user -> {
                        logger.debug("User found: {}", user.getName());
                        return toDto(user);
                    })
                    .orElseThrow(() -> {
                        logger.error("User with id {} not found", id);
                        return new RuntimeException("User with id " + id + " not found");
                    });

            logger.info("Successfully retrieved user with id: {}", id);
            return result;
        } catch (Exception e) {
            logger.error("Error getting user with id: {}", id, e);
            throw e;
        }
    }

    @Override
    public UserDto updateUser(Long id, UserDto dto) {
        logger.info("Updating user with id: {}, new name: {}, new email: {}", id, dto.name(), dto.email());

        var user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User with id {} not found for update", id);
                    return new RuntimeException("User with id " + id + " not found");
                });

        logger.debug("Found user to update: {}", user.getName());

        user.setName(dto.name());
        user.setEmail(dto.email());

        logger.debug("Saving updated user to database");
        UserEntity updatedUser = userRepository.save(user);

        UserDto result = toDto(updatedUser);
        logger.info("User with id: {} updated successfully", id);
        return result;
    }

    @Override
    public void deleteUser(Long id) {
        logger.info("Deleting user with id: {}", id);

        try {
            userRepository.deleteById(id);
            logger.info("User with id: {} deleted successfully", id);
        } catch (Exception e) {
            logger.error("Error deleting user with id: {}", id, e);
            throw e;
        }
    }

    private UserDto toDto(UserEntity user) {
        logger.trace("Converting user entity to DTO: id={}, name={}", user.getId(), user.getName());
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

}
