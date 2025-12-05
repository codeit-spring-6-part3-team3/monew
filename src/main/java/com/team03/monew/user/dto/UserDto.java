package com.team03.monew.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "사용자 응답")
public record UserDto(
        @Schema(description = "사용자 ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "이메일", example = "test@monew.com")
        String email,

        @Schema(description = "닉네임", example = "테스터")
        String nickname,

        @Schema(description = "생성일시", example = "2025-11-28T00:00:00")
        LocalDateTime createdAt
) {
}

