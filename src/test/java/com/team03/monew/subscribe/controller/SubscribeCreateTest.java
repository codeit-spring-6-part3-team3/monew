package com.team03.monew.subscribe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team03.monew.interest.Fixture.InterestFixture;
import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.service.InterestService;
import com.team03.monew.subscribe.domain.Subscribe;
import com.team03.monew.subscribe.dto.SubscribeDto;
import com.team03.monew.subscribe.fixture.SubscribeFixture;
import com.team03.monew.subscribe.service.SubscribeService;
import com.team03.monew.user.domain.User;
import com.team03.monew.web.controller.InterestController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InterestController.class)
public class SubscribeCreateTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InterestService interestService;

    @MockitoBean
    private SubscribeService subscribeService;

    private User user;
    private Subscribe subscribe;
    private Interest interest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@test.com")
                .nickname("nickname")
                .password("password")
                .build();
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());

        interest = InterestFixture.interestCreate("오늘에 뉴스", List.of("정치"));
        ReflectionTestUtils.setField(interest,"id",UUID.randomUUID());

        subscribe = SubscribeFixture.subscribeCreate(user.getId(),interest.getId());
        ReflectionTestUtils.setField(subscribe,"id",UUID.randomUUID());

    }

    @Test
    @DisplayName("구독 생성 성공 검증")
    public void subscribeCreateTestSuccess() throws Exception {
        //given
        SubscribeDto subscribeDto = SubscribeFixture.subscribeCreateDto(subscribe,interest);
        given(subscribeService.subscribeCreate(any(UUID.class),any(UUID.class))).willReturn(subscribeDto);

        // When & Then
        mockMvc.perform(post("/api/interests/{interestId}/subscriptions",interest.getId())
        .param("Monew-Request-User-ID",user.getId().toString())
                        .header("Monew-Request-User-ID", UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(subscribeDto.id().toString()))
                .andExpect(jsonPath("$.interestId").value(interest.getId().toString()))
                .andExpect(jsonPath("$.interestName").value(interest.getName()))
                .andExpect(jsonPath("$.interestKeywords[0]").value(interest.getKeywords().get(0)))
                .andExpect(jsonPath("$.interestSubscriberCount").value(0));
    }

    @Test
    @DisplayName("구독 생성 필수 요청값 없음 실패 검증")
    public void subscribeCreateTestFail() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/interests/{interestId}/subscriptions",interest.getId())
                        .header("Monew-Request-User-ID", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest());
    }
}
