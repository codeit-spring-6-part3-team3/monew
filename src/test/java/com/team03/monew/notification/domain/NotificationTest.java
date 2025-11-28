
package com.team03.monew.notification.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationTest {

    @Test
    @DisplayName("Notification 객체 정상 생성")
    void createNotification() {
        // given
        UUID userId = UUID.randomUUID();
        String context = userId + "님이 나의 댓글을 좋아합니다.";
        NoticeResourceType resource = NoticeResourceType.REPLY;
        UUID resourceId = UUID.randomUUID();

        // when
        Notification notification = Notification.from(userId, context, resource, resourceId);

        // then
        assertThat(notification.getUserId()).isEqualTo(userId);
        assertThat(notification.getContext()).isEqualTo(context);
        assertThat(notification.getResource()).isEqualTo(resource);
        assertThat(notification.getResourceId()).isEqualTo(resourceId);
        assertThat(notification.isChecked()).isFalse();
    }

    @Test
    @DisplayName("알림 확인 처리")
    void checkNotification() {
        // given
        Notification notification = Notification.from(
                UUID.randomUUID(),
                "테스트 알림",
                NoticeResourceType.REPLY,
                UUID.randomUUID()
        );

        // when
        Notification checkedNotification = notification.check();

        // then
        assertThat(checkedNotification.isChecked()).isTrue();
    }

    @Test
    @DisplayName("확인된 알림, 만료 대상 판단, 만료 맞음")
    void isExpired_WhenCheckedAndOlderThanOneWeek() {
        // given
        Notification notification = Notification.from(
                UUID.randomUUID(),
                "테스트 알림",
                NoticeResourceType.REPLY,
                UUID.randomUUID()
        );

        // creationAt을 1주일 전으로 설정
        setCreationAt(notification, LocalDateTime.now().minusWeeks(1).minusDays(1));
        notification.check();

        // when & then
        assertThat(notification.isExpired()).isTrue();
    }

    @Test
    @DisplayName("확인 안된 알림, 만료 대상 판단, 대상 아님")
    void isNotExpired_WhenNotChecked() {
        // given
        Notification notification = Notification.from(
                UUID.randomUUID(),
                "테스트 알림",
                NoticeResourceType.REPLY,
                UUID.randomUUID()
        );

        setCreationAt(notification, LocalDateTime.now().minusWeeks(2));

        // when & then
        assertThat(notification.isExpired()).isFalse();
    }

    @Test
    @DisplayName("확인된 알림, 1주일 지나지 않음, 만료 아님")
    void isNotExpired_WhenCheckedButRecentlyCreated() {
        // given
        Notification notification = Notification.from(
                UUID.randomUUID(),
                "테스트 알림",
                NoticeResourceType.REPLY,
                UUID.randomUUID()
        );

        setCreationAt(notification, LocalDateTime.now().minusDays(3));
        notification.check();

        // when & then
        assertThat(notification.isExpired()).isFalse();
    }

    // 테스트를 위한 헬퍼 메서드 (리플렉션 사용)
    private void setCreationAt(Notification notification, LocalDateTime creationAt) {
        try {
            java.lang.reflect.Field field = Notification.class.getDeclaredField("creationAt");
            field.setAccessible(true);
            field.set(notification, creationAt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}