package com.team03.monew.news.controller.api;

import com.team03.monew.news.domain.NewsSourceType;
import com.team03.monew.news.dto.CursorPageResponseArticleDto;
import com.team03.monew.news.dto.NewsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

@Tag(name = "News", description = "뉴스 API")
public interface NewsApi {

  @Operation(summary = "뉴스 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  ResponseEntity<CursorPageResponseArticleDto<NewsDto>> findNews(
      @Parameter(description = "검색 키워드") String keyword,
      @Parameter(description = "관심사 ID") UUID interestId,
      @Parameter(description = "출처 목록") List<NewsSourceType> sourceIn,
      @Parameter(description = "게시일 시작") LocalDateTime publishDateFrom,
      @Parameter(description = "게시일 종료") LocalDateTime publishDateTo,
      @Parameter(description = "정렬 기준") String orderBy,
      @Parameter(description = "정렬 방향") String direction,
      @Parameter(description = "커서") String cursor,
      @Parameter(description = "커서 기준 시간") LocalDateTime after,
      @Parameter(description = "페이지 크기") int limit
  );

  // 뉴스 단건 조회
  @Operation(summary = "뉴스 단건 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "404", description = "뉴스 기사 정보 없음"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  ResponseEntity<NewsDto> getNewsDetails(
      @Parameter(description = "뉴스 기사 ID") UUID articleId,
      @Parameter(description = "요청자 ID", required = true) UUID userId
  );

  // 뉴스 논리 삭제
  @Operation(summary = "뉴스 논리 삭제")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "논리 삭제 성공"),
      @ApiResponse(responseCode = "404", description = "뉴스 기사 정보 없음"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  ResponseEntity<Void> deleteNewsLogical(
      @Parameter(description = "삭제 할 뉴스 기사 ID") UUID articleId
  );

  // 뉴스 물리 삭제
  @Operation(summary = "뉴스 물리 삭제")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "삭제 성공"),
      @ApiResponse(responseCode = "404", description = "뉴스 기사 정보 없음"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  ResponseEntity<Void> deleteNewsPhysical(
      @Parameter(description = "삭제 할 뉴스 기사 ID") UUID articleId
  );

  // 뉴스 복구
  @Operation(summary = "뉴스 복구")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "복구 성공"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  List<Object> restoreNews(
      @Parameter(description = "복구 시작 날짜", required = true) LocalDateTime from,
      @Parameter(description = "복구 종료 날짜", required = true) LocalDateTime to
  );

  // 출처 목록 조회
  @Operation(summary = "출처 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  ResponseEntity<NewsSourceType[]> getAllSources();
}
