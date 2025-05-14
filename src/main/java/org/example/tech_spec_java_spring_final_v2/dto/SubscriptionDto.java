package org.example.tech_spec_java_spring_final_v2.dto;

import java.time.LocalDate;

public record SubscriptionDto(
    Long id,
    Long userId,
    String serviceName,
    LocalDate startDate
) {
}