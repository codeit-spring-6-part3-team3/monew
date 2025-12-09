package com.team03.monew.interest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team03.monew.interest.service.InterestService;
import com.team03.monew.subscribe.service.SubscribeService;
import com.team03.monew.web.controller.InterestController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InterestController.class)
public class InterestDeleteTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InterestService interestService;

    @MockitoBean
    private SubscribeService subscribeService;

    @Test
    @DisplayName("관심사 삭제 성공 검증")
    void InterestDeleteTestSuccess() throws Exception{
        //given
        UUID interestId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(delete("/api/interests/{interestId}", interestId)
        .content(objectMapper.writeValueAsString(interestId)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("관심사 삭제 관심사 없음 검증 ")
    void InterestDeleteTestFail() throws Exception{
        UUID interestId = UUID.randomUUID();

        willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "관심사 정보 없음"))
                .given(interestService)
                .interestDelete(any(UUID.class));

        mockMvc.perform(delete("/api/interests/{interestId}", interestId))
                .andExpect(status().isNotFound());
    }
}
