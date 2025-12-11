package com.team03.monew.interest.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.team03.monew.interest.Fixture.InterestFixture;
import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.dto.InterestDto;
import com.team03.monew.interest.dto.InterestRegisterRequest;
import com.team03.monew.interest.service.InterestService;
import com.team03.monew.subscribe.service.SubscribeService;
import com.team03.monew.web.controller.InterestController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
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
public class InterestCreateTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InterestService interestService;

    @MockitoBean
    private SubscribeService subscribeService;

    @Test
    @DisplayName("관심사 생성 성공 검증")
    void interestCreateSuccess() throws Exception {
        // given
        InterestRegisterRequest request =new InterestRegisterRequest("오늘에 뉴스", List.of("경재","스포츠"));
        Interest interest = InterestFixture.interestCreate("오늘에 뉴스", List.of("경재","스포츠"));
        ReflectionTestUtils.setField(interest,"id", UUID.randomUUID());
        ReflectionTestUtils.setField(interest,"createdAt", LocalDateTime.now());

        InterestDto interestDto = InterestFixture.interestDtoCreate(interest,null);

        given(interestService.interestCreate(any(InterestRegisterRequest.class))).willReturn(interestDto);
        // When & Then
        mockMvc.perform(post("/api/interests")
                .header("Monew-Request-User-ID", UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(interestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(interestDto.id().toString()))
                .andExpect(jsonPath("$.name").value(interestDto.name()))
                .andExpect(jsonPath("$.keywords[0]").value(interestDto.keywords().get(0)))
                .andExpect(jsonPath("$.keywords[1]").value(interestDto.keywords().get(1)))
                .andExpect(jsonPath("$.subscriberCount").value(interestDto.subscriberCount()));
    }

    @Test
    @DisplayName("관심사 생성 관심사 이름 중복 실패 검증")
    void interestCreateDuplicateFail() throws Exception {
        InterestRegisterRequest request =new InterestRegisterRequest("", List.of("경재","스포츠"));

        given(interestService.interestCreate(any(InterestRegisterRequest.class)))
                .willThrow(DuplicateKeyException.class);

        // When & Then
        mockMvc.perform(post("/api/interests")
                        .header("Monew-Request-User-ID", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("관심사 생성 요청 값 없음 실패 검증")
    void interestCreateRequestNullFail() throws Exception {
        InterestRegisterRequest request =new InterestRegisterRequest("", List.of("경재","스포츠"));


        // When & Then
        mockMvc.perform(post("/api/interests")
                        .header("Monew-Request-User-ID", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}
