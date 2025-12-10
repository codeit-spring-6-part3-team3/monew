package com.team03.monew.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "로그인 응답")
public record UserLoginResponse(
        @Schema(description = "사용자 ID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID userId
) {
}




