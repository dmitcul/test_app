package org.example.tech_spec_java_spring_final_v2.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.tech_spec_java_spring_final_v2.dto.SubscriptionDto;
import org.example.tech_spec_java_spring_final_v2.entity.SubscriptionEntity;
import org.example.tech_spec_java_spring_final_v2.entity.UserEntity;
import org.example.tech_spec_java_spring_final_v2.repository.SubscriptionRepository;
import org.example.tech_spec_java_spring_final_v2.repository.UserRepository;
import org.example.tech_spec_java_spring_final_v2.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Override
    public SubscriptionDto addSubscription(Long userId, SubscriptionDto dto) {
        logger.info("Adding subscription for user with id: {}, service: {}", userId, dto.serviceName());

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with id {} not found", userId);
                    return new RuntimeException("User with id " + userId + " not found");
                });

        logger.debug("User found: {}", user.getName());

        LocalDate startDate = dto.startDate() != null ? dto.startDate() : LocalDate.now();
        SubscriptionEntity subscription = SubscriptionEntity.builder()
                .user(user)
                .serviceName(dto.serviceName())
                .startDate(startDate)
                .build();

        logger.debug("Saving subscription: {}", subscription.getServiceName());
        subscription = subscriptionRepository.save(subscription);

        SubscriptionDto result = toDto(subscription);
        logger.info("Subscription added successfully with id: {}", result.id());
        return result;
    }

    @Override
    public List<SubscriptionDto> getUserSubscriptions(Long userId) {
        logger.info("Getting subscriptions for user with id: {}", userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with id {} not found", userId);
                    return new RuntimeException("User with id " + userId + " not found");
                });

        logger.debug("User found: {}", user.getName());

        List<SubscriptionDto> subscriptions = subscriptionRepository.findByUser(user).stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        logger.info("Found {} subscriptions for user with id: {}", subscriptions.size(), userId);
        return subscriptions;
    }

    @Override
    public void deleteSubscription(Long userId, Long subscriptionId) {
        logger.info("Deleting subscription with id: {} for user with id: {}", subscriptionId, userId);

        SubscriptionEntity subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> {
                    logger.error("Subscription with id {} not found", subscriptionId);
                    return new RuntimeException("Subscription with id " + subscriptionId + " not found");
                });

        logger.debug("Subscription found: {}", subscription.getServiceName());

        if (!subscription.getUser().getId().equals(userId)) {
            logger.error("Subscription with id {} does not belong to user with id {}", subscriptionId, userId);
            throw new RuntimeException("Subscription with id " + subscriptionId + " does not belong to user with id " + userId);
        }

        logger.debug("Deleting subscription: {}", subscription.getServiceName());
        subscriptionRepository.delete(subscription);
        logger.info("Subscription with id: {} deleted successfully", subscriptionId);
    }

    @Override
    public List<SubscriptionDto> getTopSubscriptions() {
        logger.info("Getting top subscriptions");

        List<Object[]> results = subscriptionRepository.findTop3PopularSubscriptions();
        logger.debug("Found {} top subscription results", results.size());

        List<SubscriptionDto> topSubscriptions = new ArrayList<>();

        for (Object[] result : results) {
            String serviceName = (String) result[0];
            Long count = ((Number) result[1]).longValue();

            logger.debug("Top subscription: {}, count: {}", serviceName, count);

            topSubscriptions.add(new SubscriptionDto(
                    null,
                    null,
                    serviceName,
                    null
            ));
        }

        logger.info("Returning {} top subscriptions", topSubscriptions.size());
        return topSubscriptions;
    }

    private SubscriptionDto toDto(SubscriptionEntity subscription) {
        logger.trace("Converting subscription entity to DTO: id={}, service={}", 
                subscription.getId(), subscription.getServiceName());

        return new SubscriptionDto(
                subscription.getId(),
                subscription.getUser().getId(),
                subscription.getServiceName(),
                subscription.getStartDate()
        );
    }
}
