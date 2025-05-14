package org.example.tech_spec_java_spring_final_v2.service;

import org.example.tech_spec_java_spring_final_v2.dto.SubscriptionDto;

import java.util.List;

public interface SubscriptionService {

    SubscriptionDto addSubscription(Long userId, SubscriptionDto dto);
    
    List<SubscriptionDto> getUserSubscriptions(Long userId);
    
    void deleteSubscription(Long userId, Long subscriptionId);
    
    List<SubscriptionDto> getTopSubscriptions();
}