package org.example.tech_spec_java_spring_final_v2.controller;

import lombok.RequiredArgsConstructor;
import org.example.tech_spec_java_spring_final_v2.dto.SubscriptionDto;
import org.example.tech_spec_java_spring_final_v2.service.SubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/users/{userId}/subscriptions")
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionDto addSubscription(@PathVariable Long userId, @RequestBody SubscriptionDto dto) {
        return subscriptionService.addSubscription(userId, dto);
    }

    @GetMapping("/users/{userId}/subscriptions")
    public List<SubscriptionDto> getUserSubscriptions(@PathVariable Long userId) {
        return subscriptionService.getUserSubscriptions(userId);
    }

    @DeleteMapping("/users/{userId}/subscriptions/{subscriptionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubscription(@PathVariable Long userId, @PathVariable Long subscriptionId) {
        subscriptionService.deleteSubscription(userId, subscriptionId);
    }

    @GetMapping("/subscriptions/top")
    public List<SubscriptionDto> getTopSubscriptions() {
        return subscriptionService.getTopSubscriptions();
    }
}