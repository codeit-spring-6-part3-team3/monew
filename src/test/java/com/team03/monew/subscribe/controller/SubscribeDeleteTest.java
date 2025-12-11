package com.team03.monew.subscribe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team03.monew.interest.service.InterestService;
import com.team03.monew.subscribe.service.SubscribeService;
import com.team03.monew.web.controller.InterestController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InterestController.class)
public class SubscribeDeleteTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InterestService interestService;

    @MockitoBean
    private SubscribeService subscribeService;

    @Test
    @DisplayName("구독 삭제 성공 검증")
    public void subscribeDeleteTestSuccess() throws Exception{
        //given
        UUID interestId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        // When & Then
        mockMvc.perform(delete("/api/interests/{interestId}/subscriptions",interestId)
                        .header("Monew-Request-User-ID", UUID.randomUUID().toString())
                        .param("Monew-Request-User-ID", String.valueOf(userId)))
                .andExpect(status().isNoContent());
    }
    @Test
    @DisplayName("구독 삭제 필수 요청값 없음 실패 검증")
    public void subscribeDeleteTestFail() throws Exception{
        //given
        UUID interestId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        // When & Then
        mockMvc.perform(delete("/api/interests/{interestId}/subscriptions",interestId)
                        .header("Monew-Request-User-ID", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest());
    }


}
