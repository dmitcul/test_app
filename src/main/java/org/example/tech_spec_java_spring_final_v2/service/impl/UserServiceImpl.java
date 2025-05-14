package org.example.tech_spec_java_spring_final_v2.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.tech_spec_java_spring_final_v2.dto.UserDto;
import org.example.tech_spec_java_spring_final_v2.entity.UserEntity;
import org.example.tech_spec_java_spring_final_v2.repository.UserRepository;
import org.example.tech_spec_java_spring_final_v2.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto dto) {
        UserEntity user = UserEntity.builder()
                .name(dto.name())
                .email(dto.email())
                .build();
        user = userRepository.save(user);
        return toDto(user);
    }

    @Override
    public UserDto getUser(Long id) {
        return userRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("User with id " + id + " not found"));
    }

    @Override
    public UserDto updateUser(Long id, UserDto dto) {
        var user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User with id " + id + " not found"));
        user.setName(dto.name());
        user.setEmail(dto.email());

        return toDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private UserDto toDto(UserEntity user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

}
