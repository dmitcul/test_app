package org.example.tech_spec_java_spring_final_v2.service;

import org.example.tech_spec_java_spring_final_v2.dto.UserDto;

public interface UserService {

    UserDto createUser(UserDto dto);
    UserDto getUser(Long id);
    UserDto updateUser(Long id, UserDto dto);
    void deleteUser(Long id);

}
