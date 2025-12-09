package com.team03.monew.interest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team03.monew.interest.Fixture.InterestFixture;
import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.dto.InterestDto;
import com.team03.monew.interest.dto.InterestUpdateRequest;
import com.team03.monew.interest.service.InterestService;
import com.team03.monew.subscribe.service.SubscribeService;
import com.team03.monew.web.controller.InterestController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InterestController.class)
public class InterestUpdateTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InterestService interestService;

    @MockitoBean
    private SubscribeService subscribeService;

    Interest interest;
    InterestDto interestDto;
    InterestUpdateRequest  request;
    @BeforeEach
    void setUp() {
        List<String> keywords = List.of("환률","경재");
        request = new InterestUpdateRequest(keywords);

        interest = InterestFixture.interestCreate("오늘의 뉴스",List.of("경재"));
        ReflectionTestUtils.setField(interest,"id",UUID.randomUUID());
        ReflectionTestUtils.setField(interest,"createdAt", LocalDateTime.now());
        interestDto = InterestFixture.interestDtoCreate(interest,null);
    }

    @Test
    @DisplayName("관심사 업데이트 성공 검증")
    void updateInterestSuccess() throws Exception {
        //given


        given(interestService.interestUpdate(any(UUID.class),any(InterestUpdateRequest.class))).willReturn(interestDto);

        // When & Then
        mockMvc.perform(patch("/api/interests/{interestId}",interest.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(interest.getId()))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(interestDto.id().toString()))
                .andExpect(jsonPath("$.name").value(interestDto.name()))
                .andExpect(jsonPath("$.keywords[0]").value(interestDto.keywords().get(0)))
                .andExpect(jsonPath("$.subscriberCount").value(interestDto.subscriberCount()))
                .andExpect(jsonPath("$.subscribedByMe").value(interestDto.subscribedByMe()));
    }

    @Test
    @DisplayName("관심사 업데이트 요청값 없음 실패 검증")
    void updateInterestFail() throws Exception {
        //given
        willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "관심사 정보 없음"))
                .given(interestService)
                .interestUpdate(any(UUID.class),any(InterestUpdateRequest.class));

        // When & Then
        mockMvc.perform(patch("/api/interests/{interestId}",interest.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(interest.getId()))
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("관심사 업데이트 관심사 없음 실패 검증")
    void updateInterestNotFoundFail() throws Exception {
        //given
        willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "관심사 정보 없음"))
                .given(interestService)
                .interestUpdate(any(UUID.class),any(InterestUpdateRequest.class));

        // When & Then
        mockMvc.perform(patch("/api/interests/{interestId}",interest.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(interest.getId()))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

}
