package org.example.tech_spec_java_spring_final_v2.controller;

import lombok.RequiredArgsConstructor;
import org.example.tech_spec_java_spring_final_v2.dto.UserDto;
import org.example.tech_spec_java_spring_final_v2.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody UserDto dto) {
        logger.info("Received request to create user with name: {}, email: {}", dto.name(), dto.email());
        UserDto createdUser = userService.createUser(dto);
        logger.info("User created successfully with id: {}", createdUser.id());
        return createdUser;
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        logger.info("Received request to get user with id: {}", id);
        UserDto user = userService.getUser(id);
        logger.info("Retrieved user with id: {}", id);
        return user;
    }

    @PutMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody UserDto dto) {
        logger.info("Received request to update user with id: {}, new name: {}, new email: {}", 
                id, dto.name(), dto.email());
        UserDto updatedUser = userService.updateUser(id, dto);
        logger.info("User with id: {} updated successfully", id);
        return updatedUser;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        logger.info("Received request to delete user with id: {}", id);
        userService.deleteUser(id);
        logger.info("User with id: {} deleted successfully", id);
    }

}
