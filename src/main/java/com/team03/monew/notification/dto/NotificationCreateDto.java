package com.team03.monew.notification.dto;

import com.team03.monew.notification.domain.NoticeResourceType;
import lombok.Builder;

import java.util.UUID;
@Builder
public record NotificationCreateDto(
        UUID userId,
        String context,
        NoticeResourceType resource,
        UUID resourceId
) {}

/* 뉴스 수집에서 호출                          <-- 주석을 이런식으로 해도 되는지
NotificationCreateDto dto = new NotificationCreateDto(
    userId,     // 관심사 일치 사용자 ID
    ([관심사명] + "와 관련된 기사가 " + count + "건이 등록되었습니다."),
    NoticeResourceType.INTEREST,
    newsId
);
notificationService.createNotification(dto);
 */
/* 댓글 좋아요에서 호출
NotificationCreateDto dto = new NotificationCreateDto(
    commentOwnerId,     // 댓글 작성자 ID
    ([요청자명] + "님이 나의 댓글을 좋아합니다."),
    NoticeResourceType.REPLY, <-- NoticeResourceType은 Notification의 도메인 패키지에 위치했는데 외부에서 사용해도 되는건지, 아니면 다른 자료형으로 전송하는지
    commentId
);
notificationService.createNotification(dto);
 */