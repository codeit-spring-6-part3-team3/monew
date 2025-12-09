package com.team03.monew.web.controller.api;


import com.team03.monew.interest.dto.CursorPageResponseInterestDto;
import com.team03.monew.interest.dto.InterestDto;
import com.team03.monew.interest.dto.InterestRegisterRequest;
import com.team03.monew.interest.dto.InterestUpdateRequest;
import com.team03.monew.subscribe.dto.SubscribeDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.rmi.NoSuchObjectException;
import java.time.LocalDateTime;
import java.util.UUID;

@Tag(name = "관심사 관리",description = "관심사 관련 API")
public interface InterestApi {

    @Operation(summary = "관심사 목록 등록")
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "201", description = "새로운 관심사를 등록 합니다",
                    content = @Content(schema = @Schema(implementation = InterestDto.class))
            ),
            @ApiResponse(
                responseCode = "400", description = "잘못된 요청 (입력값 검증 실패)",
                    content = @Content(schema = @Schema(implementation = InterestDto.class))
            ),
            @ApiResponse(
                responseCode = "409", description = "유사 관심사 중복",
                    content = @Content(schema = @Schema(implementation = InterestDto.class))
            ),
            @ApiResponse(
                responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = InterestDto.class))
            )
    })
    ResponseEntity<InterestDto> interestCreate(
            @RequestBody(required = true,content = @Content(
                    schema = @Schema(implementation = InterestRegisterRequest.class)))
            InterestRegisterRequest interestRegisterRequest
    );

    @Operation(summary = "관심사 정보 수정")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = InterestDto.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "잘못된 요청 (입력값 검증 실패)",
                    content = @Content(schema = @Schema(implementation = InterestDto.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "관심사 정보 없음",
                    content = @Content(schema = @Schema(implementation = InterestDto.class))
            ),
            @ApiResponse(
                    responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = InterestDto.class))
            )

    })
    ResponseEntity<InterestDto> interestUpdate (
            @Parameter(description ="관심사 ID",required = true) UUID interestId,
            @RequestBody(required = true,content = @Content(
                    schema = @Schema(implementation = InterestUpdateRequest.class)))
            InterestUpdateRequest interestUpdateRequest

    ) throws NoSuchObjectException;

    @Operation(summary = "관심사 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = CursorPageResponseInterestDto.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "잘못된 요청 (정렬 기준 오류, 페이지네이션 파라미터 오류 등)",
                    content = @Content(schema = @Schema(implementation = CursorPageResponseInterestDto.class))
            ),
            @ApiResponse(
                    responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = CursorPageResponseInterestDto.class))
            )
    })
    ResponseEntity<CursorPageResponseInterestDto> interestList(
            @Parameter(description = "검색어(관심사 이름, 키워드)",example = "스포츠") String keyword,
            @Parameter(description = "정렬 속성 이름",required = true,
                schema = @Schema(allowableValues = {"name","subscriberCount"})) String orderBy,
            @Parameter(description = "정렬 방향 (ASC, DESC)",required = true,
                schema = @Schema(allowableValues = {"ASC", "DESC"})) String direction,
            @Parameter(description = "커서 값") String cursor,
            @Parameter(description = "보조 커서(createdAt) 값")LocalDateTime after,
            @Parameter(description = "커서 페이지 크기",required = true,example = "50") int limit,
            @Parameter(name ="Monew-Request-User-ID",description = "요청자 ID",required = true) UUID userId
            );

    @Operation(summary = "관심사 구독")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "구독 성공",
                    content = @Content(schema = @Schema(implementation = SubscribeDto.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "관심사 정보 없음",
                    content = @Content(schema = @Schema(implementation = SubscribeDto.class))
            ),
            @ApiResponse(
                    responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = SubscribeDto.class))
            )
    })
    ResponseEntity<SubscribeDto> subscribeCreate(
            @Parameter(description = "관심사 ID",required = true) UUID interestId,
            @Parameter(name ="Monew-Request-User-ID",description = "요청자 ID",required = true) UUID userId
    ) throws NoSuchObjectException;

    @Operation(summary = "관심사 구독 취소")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "구독 취소 성공"
            ),
            @ApiResponse(
                    responseCode = "404", description = "관심사 정보 없음"
            ),
            @ApiResponse(
                    responseCode = "500", description = "서버 내부 오류"
            )
    })
    ResponseEntity<Void> subscribeDelete(
            @Parameter(description = "관심사 ID",required = true) UUID interestId,
            @Parameter(name ="Monew-Request-User-ID",description = "요청자 ID",required = true) UUID userId
    ) throws NoSuchObjectException;

    @Operation(summary = "관심사 물리 삭제")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "404", description = "관심사 정보 없음"
            ),
            @ApiResponse(
                    responseCode = "500", description = "서버 내부 오류"
            )
    })
    ResponseEntity<Void> interestDelete(
            @Parameter(description = "관심사 ID",required = true) UUID interestId
    ) throws NoSuchObjectException;
}
