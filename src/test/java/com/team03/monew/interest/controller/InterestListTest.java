package com.team03.monew.interest.controller;

import com.team03.monew.interest.Fixture.InterestFixture;
import com.team03.monew.interest.domain.Interest;
import com.team03.monew.interest.dto.CursorPageResponseInterestDto;
import com.team03.monew.interest.dto.InterestDto;
import com.team03.monew.interest.dto.InterestSearchRequest;
import com.team03.monew.interest.service.InterestService;
import com.team03.monew.subscribe.service.SubscribeService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InterestController.class)
public class InterestListTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InterestService interestService;

    @MockitoBean
    private SubscribeService subscribeService;

    private UUID userId;
    private InterestDto interestDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        Interest interest1 = InterestFixture.interestCreate("오늘의 뉴스", List.of("경제", "주식"));
        ReflectionTestUtils.setField(interest1, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(interest1, "createdAt", LocalDateTime.now());

        interestDto = InterestFixture.interestDtoCreate(interest1, true);

    }

    @Test
    @DisplayName("관심사 목록 커서 없을 떄 조회 성공 검증")
    void InterestListSearchSuccess() throws Exception {
        //given
        CursorPageResponseInterestDto response = CursorPageResponseInterestDto.builder()
                .content(List.of(interestDto))
                .nextCursor(null)
                .nextAfter(null)
                .size(1)
                .totalElements(1L)
                .hasNext(false)
                .build();
        given(interestService.interestList(any(UUID.class), any(InterestSearchRequest.class))).willReturn(response);

        // When & Then
        mockMvc.perform(get("/api/interests")
                        .param("Monew-Request-User-ID", String.valueOf(userId))
                        .param("keyword", "오늘에 뉴스")
                        .param("orderBy", "name")
                        .param("direction", "ASC")
                        .param("limit", String.valueOf(1)))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("관심사 목록 커서 있을 떄 조회 성공 검증")
    void InterestListCursorSearchSuccess() throws Exception {
        //given
        CursorPageResponseInterestDto response = CursorPageResponseInterestDto.builder()
                .content(List.of(interestDto))
                .nextCursor("뉴스")
                .nextAfter(LocalDateTime.now().toString())
                .size(1)
                .totalElements(2L)
                .hasNext(true)
                .build();
        given(interestService.interestList(any(UUID.class), any(InterestSearchRequest.class))).willReturn(response);

        // When & Then
        mockMvc.perform(get("/api/interests")
                        .param("Monew-Request-User-ID", String.valueOf(userId))
                        .param("keyword", "오늘에 뉴스")
                        .param("orderBy", "name")
                        .param("direction", "ASC")
                        .param("limit", String.valueOf(1)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("괸심사 목록 필수 요청값 없음 실패 검증")
    void InterestListSearchFail() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/interests")
                        .param("Monew-Request-User-ID", String.valueOf(userId))
                        .param("keyword", "오늘에 뉴스")
                        .param("orderBy", "name")
                        .param("limit", String.valueOf(1)))
                .andExpect(status().isBadRequest());
    }
}
