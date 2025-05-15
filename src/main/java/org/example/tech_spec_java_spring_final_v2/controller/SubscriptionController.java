package org.example.tech_spec_java_spring_final_v2.controller;

import lombok.RequiredArgsConstructor;
import org.example.tech_spec_java_spring_final_v2.dto.SubscriptionDto;
import org.example.tech_spec_java_spring_final_v2.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubscriptionController {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);

    private final SubscriptionService subscriptionService;

    @PostMapping("/users/{userId}/subscriptions")
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionDto addSubscription(@PathVariable Long userId, @RequestBody SubscriptionDto dto) {
        logger.info("Received request to add subscription for user with id: {}, service: {}", 
                userId, dto.serviceName());
        SubscriptionDto subscription = subscriptionService.addSubscription(userId, dto);
        logger.info("Subscription added successfully with id: {} for user with id: {}", 
                subscription.id(), userId);
        return subscription;
    }

    @GetMapping("/users/{userId}/subscriptions")
    public List<SubscriptionDto> getUserSubscriptions(@PathVariable Long userId) {
        logger.info("Received request to get subscriptions for user with id: {}", userId);
        List<SubscriptionDto> subscriptions = subscriptionService.getUserSubscriptions(userId);
        logger.info("Retrieved {} subscriptions for user with id: {}", subscriptions.size(), userId);
        return subscriptions;
    }

    @DeleteMapping("/users/{userId}/subscriptions/{subscriptionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubscription(@PathVariable Long userId, @PathVariable Long subscriptionId) {
        logger.info("Received request to delete subscription with id: {} for user with id: {}", 
                subscriptionId, userId);
        subscriptionService.deleteSubscription(userId, subscriptionId);
        logger.info("Subscription with id: {} deleted successfully for user with id: {}", 
                subscriptionId, userId);
    }

    @GetMapping("/subscriptions/top")
    public List<SubscriptionDto> getTopSubscriptions() {
        logger.info("Received request to get top subscriptions");
        List<SubscriptionDto> topSubscriptions = subscriptionService.getTopSubscriptions();
        logger.info("Retrieved {} top subscriptions", topSubscriptions.size());
        return topSubscriptions;
    }
}
