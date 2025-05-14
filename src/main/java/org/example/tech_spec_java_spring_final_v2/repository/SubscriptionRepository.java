package org.example.tech_spec_java_spring_final_v2.repository;

import org.example.tech_spec_java_spring_final_v2.entity.SubscriptionEntity;
import org.example.tech_spec_java_spring_final_v2.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
    
    List<SubscriptionEntity> findByUser(UserEntity user);
    
    @Query(value = "SELECT s.service_name, COUNT(*) as count FROM subscriptions s " +
            "GROUP BY s.service_name ORDER BY count DESC LIMIT 3", nativeQuery = true)
    List<Object[]> findTop3PopularSubscriptions();
}