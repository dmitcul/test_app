package org.example.tech_spec_java_spring_final_v2.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.tech_spec_java_spring_final_v2.dto.SubscriptionDto;
import org.example.tech_spec_java_spring_final_v2.entity.SubscriptionEntity;
import org.example.tech_spec_java_spring_final_v2.entity.UserEntity;
import org.example.tech_spec_java_spring_final_v2.repository.SubscriptionRepository;
import org.example.tech_spec_java_spring_final_v2.repository.UserRepository;
import org.example.tech_spec_java_spring_final_v2.service.SubscriptionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Override
    public SubscriptionDto addSubscription(Long userId, SubscriptionDto dto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id " + userId + " not found"));

        SubscriptionEntity subscription = SubscriptionEntity.builder()
                .user(user)
                .serviceName(dto.serviceName())
                .startDate(dto.startDate() != null ? dto.startDate() : LocalDate.now())
                .build();

        subscription = subscriptionRepository.save(subscription);
        return toDto(subscription);
    }

    @Override
    public List<SubscriptionDto> getUserSubscriptions(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id " + userId + " not found"));

        return subscriptionRepository.findByUser(user).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSubscription(Long userId, Long subscriptionId) {
        SubscriptionEntity subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription with id " + subscriptionId + " not found"));

        if (!subscription.getUser().getId().equals(userId)) {
            throw new RuntimeException("Subscription with id " + subscriptionId + " does not belong to user with id " + userId);
        }

        subscriptionRepository.delete(subscription);
    }

    @Override
    public List<SubscriptionDto> getTopSubscriptions() {
        List<Object[]> results = subscriptionRepository.findTop3PopularSubscriptions();
        List<SubscriptionDto> topSubscriptions = new ArrayList<>();

        for (Object[] result : results) {
            String serviceName = (String) result[0];
            Long count = ((Number) result[1]).longValue();
            
            topSubscriptions.add(new SubscriptionDto(
                    null,
                    null,
                    serviceName,
                    null
            ));
        }

        return topSubscriptions;
    }

    private SubscriptionDto toDto(SubscriptionEntity subscription) {
        return new SubscriptionDto(
                subscription.getId(),
                subscription.getUser().getId(),
                subscription.getServiceName(),
                subscription.getStartDate()
        );
    }
}