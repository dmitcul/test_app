package org.example.tech_spec_java_spring_final_v2.repository;

import org.example.tech_spec_java_spring_final_v2.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
