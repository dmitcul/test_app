package org.example.tech_spec_java_spring_final_v2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tech_spec_java_spring_final_v2.dto.SubscriptionDto;
import org.example.tech_spec_java_spring_final_v2.service.SubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubscriptionController.class)
@Import(SubscriptionControllerTest.TestConfig.class)
public class SubscriptionControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public SubscriptionService subscriptionService() {
            return Mockito.mock(SubscriptionService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        reset(subscriptionService);
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAddSubscription() throws Exception {
        Long userId = 1L;
        SubscriptionDto inputDto = new SubscriptionDto(null, userId, "Netflix", LocalDate.now());
        SubscriptionDto outputDto = new SubscriptionDto(1L, userId, "Netflix", LocalDate.now());

        when(subscriptionService.addSubscription(eq(userId), any(SubscriptionDto.class))).thenReturn(outputDto);

        mockMvc.perform(post("/users/{userId}/subscriptions", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(outputDto.id()))
                .andExpect(jsonPath("$.userId").value(outputDto.userId()))
                .andExpect(jsonPath("$.serviceName").value(outputDto.serviceName()));
    }

    @Test
    public void testGetUserSubscriptions() throws Exception {
        Long userId = 1L;
        List<SubscriptionDto> subscriptions = Arrays.asList(
                new SubscriptionDto(1L, userId, "Netflix", LocalDate.now()),
                new SubscriptionDto(2L, userId, "Spotify", LocalDate.now())
        );

        when(subscriptionService.getUserSubscriptions(userId)).thenReturn(subscriptions);

        mockMvc.perform(get("/users/{userId}/subscriptions", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(subscriptions.get(0).id()))
                .andExpect(jsonPath("$[0].serviceName").value(subscriptions.get(0).serviceName()))
                .andExpect(jsonPath("$[1].id").value(subscriptions.get(1).id()))
                .andExpect(jsonPath("$[1].serviceName").value(subscriptions.get(1).serviceName()));
    }

    @Test
    public void testDeleteSubscription() throws Exception {
        Long userId = 1L;
        Long subscriptionId = 1L;

        doNothing().when(subscriptionService).deleteSubscription(userId, subscriptionId);

        mockMvc.perform(delete("/users/{userId}/subscriptions/{subscriptionId}", userId, subscriptionId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetTopSubscriptions() throws Exception {
        List<SubscriptionDto> topSubscriptions = Arrays.asList(
                new SubscriptionDto(null, null, "Netflix", null),
                new SubscriptionDto(null, null, "Spotify", null),
                new SubscriptionDto(null, null, "YouTube Premium", null)
        );

        when(subscriptionService.getTopSubscriptions()).thenReturn(topSubscriptions);

        mockMvc.perform(get("/subscriptions/top"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].serviceName").value(topSubscriptions.get(0).serviceName()))
                .andExpect(jsonPath("$[1].serviceName").value(topSubscriptions.get(1).serviceName()))
                .andExpect(jsonPath("$[2].serviceName").value(topSubscriptions.get(2).serviceName()));
    }
}
