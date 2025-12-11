package com.team03.monew.web.controller.api;

import com.team03.monew.articleviews.dto.ArticleViewDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "ArticleView", description = "뉴스 기사 뷰 API")
public interface ArticleViewApi {

  @Operation(summary = "기사 뷰 등록")
  @ApiResponses( value = {
      @ApiResponse(responseCode = "200", description = "기사 뷰 등록 성공"),
      @ApiResponse(responseCode = "404", description = "댓글 정보 없음"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  ResponseEntity<ArticleViewDto> register(
      @Parameter(description = "등록할 뉴스 기사 ID") UUID articleId,
      @RequestHeader("Monew-Request-User-ID") UUID viewedBy
  );
}
